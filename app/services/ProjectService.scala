package services

import java.io._

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import com.github.ghik.silencer.silent
import javax.inject._
import models._
import org.apache.commons.io.FileUtils._
import play.api.{Configuration, Environment, Logging, Mode}
import repositories._
import utils._

import scala.concurrent._
import scala.concurrent.duration._

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService, featureService: FeatureService,
                               featureRepository: FeatureRepository, branchRepository: BranchRepository, directoryRepository: DirectoryRepository,
                               pageRepository: PageRepository, menuService: MenuService, pageService: PageService,
                               config: Configuration, environment: Environment, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends Logging {
  val projectsRootDirectory = config.get[String]("projects.root.directory")
  val synchronizeInterval = config.get[Int]("projects.synchronize.interval")
  val synchronizeInitialDelay = config.get[Int]("projects.synchronize.initial.delay")
  val documentationMetaFile = config.get[String]("documentation.meta.file")
  val synchronizeFromRemoteEnabled = config.get[Boolean]("projects.synchronize.from.remote.enabled")


  if (environment.mode != Mode.Test) {
    val synchronizeJob = actorSystem.scheduler.schedule(initialDelay = synchronizeInitialDelay.seconds, interval = synchronizeInterval.seconds) {
      synchronizeAll()
      ()
    }(actorSystem.dispatcher)
    CoordinatedShutdown(actorSystem).addTask(
      CoordinatedShutdown.PhaseBeforeServiceUnbind, "cancelSynchronizeJob") { () =>
      Future(synchronizeJob.cancel()).map(_ => Done)
    }
  }

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def checkoutRemoteBranches(project: Project): Future[Unit] = {
    for {
      remoteBranches <- gitService.getRemoteBranches(project.repositoryUrl)
      _ <- checkoutBranches(project, remoteBranches.toSet)
    } yield ()
  }

  private def parseAndSaveFeatures(project: Project, branch: Branch): Unit = {
    project.featuresRootPath match {
      case Some(featuresRootPath) =>

        val features = featureService.parseBranchDirectory(project, branch, getLocalRepository(project.id, branch.name) + featuresRootPath)
        featureRepository.saveAll(features)

        logger.info(s"${features.size} features saved for project ${project.id} on branch ${branch.name}")

      case _ => logger.info(s"Features ignored for project ${project.id} on branch ${branch.name} because no featuresRootPath is defined")
    }
  }

  private def countDirectories(directory: Directory): Int = 1 + directory.children.map(countDirectories).sum

  private def countPages(directory: Directory): Int = directory.pages.size + directory.children.map(countPages).sum

  private def parseAndSaveDirectoriesAndPages(project: Project, branch: Branch): Unit = {
    project.documentationRootPath match {
      case Some(documentationRootPath) =>
        val rootDirectory = pageService.processDirectory(branch, project.id + ">" + branch.name + ">/", getLocalRepository(project.id, branch.name) + documentationRootPath)
        logger.info(s"${rootDirectory.map(countDirectories).getOrElse(0)} directories and ${rootDirectory.map(countPages).getOrElse(0)} pages saved for project ${project.id} on branch ${branch.name}")

      case _ => logger.info(s"Directories and pages ignored for project ${project.id} on branch ${branch.name} because no documentationRootPath is defined")
    }
  }

  private def checkoutBranches(project: Project, branches: Set[String]): Future[Unit] = {
    if (branches.nonEmpty) {
      logger.info(s"checkout ${project.id} branches ${branches.mkString(", ")}")

      FutureExt.sequentially(branches.toSeq) { branchName =>
        val localRepository = getLocalRepository(project.id, branchName)

        val branch = branchRepository.findByProjectIdAndName(project.id, branchName).getOrElse(branchRepository.save(Branch(-1, branchName, branchName == project.stableBranch, project.id)))

        for {
          _ <- gitService.clone(project.repositoryUrl, localRepository)
          _ <- gitService.checkout(branchName, localRepository)
        } yield (parseAndSaveFeatures(project, branch), parseAndSaveDirectoriesAndPages(project, branch))

      }.map(_ => ())

    } else Future.successful(())
  }

  def reloadFromDatabase(projectId: String): Option[Seq[String]] = {
    projectRepository.findById(projectId).map { project =>
      refreshAllPages(project)
      branchRepository.findAllByProjectId(projectId).map(_.name)
    }
  }

  def reloadFromDisk(projectId: String): Option[Seq[String]] = {
    projectRepository.findById(projectId).map { project =>
      val branches = branchRepository.findAllByProjectId(projectId)
      deleteEntitiesRelatedToBranchesInDatabase(projectId, branches.map(_.name).toSet)
      branches.map { branch =>
        parseAndSaveFeatures(project, branch)
        parseAndSaveDirectoriesAndPages(project, branch)
      }
      refreshAllPages(project)
      branchRepository.findAllByProjectId(projectId).map(_.name)
    }
  }

  def reloadFromRemote(projectId: String): Option[Future[Seq[String]]] = {
    projectRepository.findById(projectId).map { project =>
      val branches = branchRepository.findAllByProjectId(projectId)
      deleteEntitiesRelatedToBranchesInDatabase(projectId, branches.map(_.name).toSet)
      branchRepository.deleteAll(branches)
      deleteDirectory(new File(s"$projectsRootDirectory$projectId".fixPathSeparator))
      synchronize(project).map(_ => branchRepository.findAllByProjectId(projectId).map(_.name))
    }
  }


  def filterFeatureFile(filePaths: Seq[String], featureRootPath: Option[String]): Seq[String] = filePaths.filter(path => featureRootPath.exists(path.startsWith) && path.endsWith(".feature"))

  def filterDocumentationFile(filePaths: Seq[String], documentationRootPath: Option[String]): Seq[String] = filePaths.filter(path => documentationRootPath.exists(path.startsWith) && path.endsWith(".md"))

  def filterDocumentationMetaFile(filePaths: Seq[String], documentationRootPath: Option[String]): Seq[String] = filePaths.filter(path => documentationRootPath.exists(path.startsWith) && path.endsWith(documentationMetaFile))

  private def updateBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) {
      logger.info(s"update ${project.id} branches ${branches.mkString(", ")}")

      FutureExt.sequentially(branches.toSeq) { branchName =>

        val localRepo = getLocalRepository(project.id, branchName)
        val localRepoFile = new File(localRepo)
        if (!localRepoFile.exists()) {
          checkoutBranches(project, Set(branchName))
        }

        gitService.pull(localRepo).map {
          case (created, updated, deleted) =>
            branchRepository.findByProjectIdAndName(project.id, branchName).foreach { branch =>
              sychronizeOneBranchFeatures(project, branch, created, updated, deleted)
              synchronizeOneBranchPages(project, branch, created, updated, deleted)
            }
        }.recoverWith {
          case _ => deleteBranches(project.id, Set(branchName)).flatMap(_ => checkoutBranches(project, Set(branchName)))
        }
      }
    } else Future.successful(())
  }

  case class DeltaFileSystemDatabase(directoryToCreate: Seq[String], directoryToUpdate: Seq[String], directoryToDelete: Seq[String], pageToCreate: Seq[String], pageToUpdate: Seq[String], pageToDelete: Seq[String]) {

    def onlyUpdatedPages: Boolean = {
      (directoryToCreate ++ directoryToUpdate ++ directoryToDelete).isEmpty && (pageToCreate ++ pageToDelete).isEmpty && pageToUpdate.nonEmpty
    }

    def directoriesRefactoring: Boolean = {
      (directoryToCreate ++ directoryToUpdate ++ directoryToDelete).nonEmpty
    }

    def pagesRefactoring: Boolean = {
      (directoryToCreate ++ directoryToUpdate ++ directoryToDelete).nonEmpty
    }
  }


  private def synchronizeOneBranchPages(project: Project, branch: Branch, created: Seq[String], updated: Seq[String], deleted: Seq[String]) = {
    project.documentationRootPath.foreach { documentationRootPath =>
      if (directoryRepository.findAllByBranchId(branch.id).nonEmpty) {

        val delta = computeDeltaFileSystemDatabase(project, created, updated, deleted)
        if (delta.onlyUpdatedPages) {
          logger.info(s"update ${delta.pageToUpdate.size} pages, for project ${project.id} on branch ${branch.name}")
          delta.pageToUpdate.flatMap(path => pageRepository.findByPath(pageService.getPagePath(project.id, branch.name, path, project.documentationRootPath.getOrElse("")))).foreach(pageService.processPage(project.id, branch.name, _, documentationRootPath))

        } else if (delta.directoriesRefactoring || delta.pagesRefactoring) {
          logger.info(s"init directories and pages, as there where some pages or directories refactoring, for project ${project.id} on branch ${branch.name}")
          pageRepository.deleteAllByStartingPath(project.id + ">" + branch.name + ">/")
          directoryRepository.deleteAllByStartingPath(project.id + ">" + branch.name + ">/")
          parseAndSaveDirectoriesAndPages(project, branch)
        }
      } else {
        parseAndSaveDirectoriesAndPages(project, branch)
      }
    }
  }

  private def computeDeltaFileSystemDatabase(project: Project, created: Seq[String], updated: Seq[String], deleted: Seq[String]): DeltaFileSystemDatabase = {
    val directoryToCreate = filterDocumentationMetaFile(created, project.documentationRootPath)
    val directoryToUpdate = filterDocumentationMetaFile(updated, project.documentationRootPath)
    val directoryToDelete = filterDocumentationMetaFile(deleted, project.documentationRootPath)

    val pageToCreate = filterDocumentationFile(created, project.documentationRootPath)
    val pageToUpdate = filterDocumentationFile(updated, project.documentationRootPath)
    val pageToDelete = filterDocumentationFile(deleted, project.documentationRootPath)

    DeltaFileSystemDatabase(directoryToCreate, directoryToUpdate, directoryToDelete, pageToCreate, pageToUpdate, pageToDelete)
  }

  private def sychronizeOneBranchFeatures(project: Project, branch: Branch, created: Seq[String], updated: Seq[String], deleted: Seq[String]) = {
    if (featureRepository.findAllByBranchId(branch.id).nonEmpty) {
      val featureToCreate = filterFeatureFile(created, project.featuresRootPath)
      val featureToUpdate = filterFeatureFile(updated, project.featuresRootPath)
      val featureToDelete = filterFeatureFile(deleted, project.featuresRootPath)


      (featureToUpdate ++ featureToDelete).flatMap(path => featureRepository.findByBranchIdAndPath(branch.id, path)).foreach(featureRepository.delete)

      (featureToUpdate ++ featureToCreate).flatMap(filePath => featureService.parseFeatureFile(project.id, branch, getLocalRepository(project.id, branch.name) + filePath).toOption).foreach(featureRepository.save)

      logger.info(s"${featureToCreate.size} features created, ${featureToUpdate.size} features updated and ${featureToDelete.size} features deleted, for project ${project.id} on branch ${branch.name}")

    } else {
      parseAndSaveFeatures(project, branch)
    }
  }

  def deleteBranches(projectId: String, branches: Set[String]): Future[Unit] = {
    if (branches.nonEmpty) {
      logger.info(s"delete $projectId branches ${branches.mkString(", ")}")

      Future {
        deleteEntitiesRelatedToBranchesInDatabase(projectId, branches)
        for (branch <- branches) {
          deleteDirectory(new File(getLocalRepository(projectId, branch)))
        }
        branchRepository.deleteAll(branchRepository.findAllByProjectId(projectId).filter(b => branches.contains(b.name)))

      }.logError(s"Error while deleting project $projectId branches ${branches.mkString(", ")}")

    } else Future.successful(())
  }

  private def deleteEntitiesRelatedToBranchesInDatabase(projectId: String, branches: Set[String]) = {
    for (branch <- branches) {
      branchRepository.findByProjectIdAndName(projectId, branch).map(_.id).foreach { branchId =>
        featureRepository.deleteAllByBranchId(branchId)
        directoryRepository.deleteAllByBranchId(branchId)
      }
    }
  }

  val lockFile = s"${projectsRootDirectory}globalSynchroOnGoing".fixPathSeparator

  def isGlobalSynchroOnGoing(): Boolean = {
    new File(lockFile).exists()
  }

  def canStartGlobalSynchro(): Boolean = {
    if (isGlobalSynchroOnGoing) {
      false
    } else {
      new File(lockFile).createNewFile()
    }
  }

  def finishGlobalSynchro(): Boolean = new File(lockFile).delete()

  def synchronizeAll(): Future[Unit] = {

    if (canStartGlobalSynchro) {
      logger.info("Start synchronizing projects")

      val projects = projectRepository.findAll()

      FutureExt.sequentially(projects)(synchronize).flatMap(_ => Future.fromTry(menuService.refreshCache())).map { _ =>
        logger.info(s"Synchronization of ${projects.size} projects is finished")
        finishGlobalSynchro
        ()
      }

    } else {
      logger.info("Synchronization already ongoing. Abort")
      Future.successful(())
    }
  }

  def refreshAllPages(project: Project): Unit = {
    logger.info(s"The pages from ${project.name} will be computed again from the database only")
    pageRepository.findAllByProjectId(project.id).map { page =>
      pageService.computePageFromPathUsingDatabase(page.path)
    }
    ()
  }

  def synchronizeProjectId(projectId: String): Option[Future[Unit]] = {
    projectRepository.findById(projectId).map { project =>
      synchronize(project)
    }
  }

  def synchronize(project: Project): Future[Unit] = {

    if (!synchronizeFromRemoteEnabled) {
      logger.info(s"No synchronization of project ${project.id}, as this feature is disabled")
      refreshAllPages(project)
      Future.successful({})
    } else {

      logger.info(s"Start synchronizing project ${project.id}")

      val localBranches = branchRepository.findAllByProjectId(project.id).map(_.name).toSet.filter(branch => new File(getLocalRepository(project.id, branch)).exists)

      logger.info(s"Local branches of project ${project.id} : ${localBranches.mkString(", ")}")

      gitService.getRemoteBranches(project.repositoryUrl).map(_.toSet).flatMap { remoteBranches =>
        logger.info(s"Remotes branches of project ${project.id} : ${remoteBranches.mkString(", ")}")

        val filteredRemoteBranches = remoteBranches.filter(branch => project.displayedBranches.forall(regex => branch.matches(regex)))

        val branchesToUpdate = localBranches.intersect(filteredRemoteBranches)
        val branchesToCheckout = filteredRemoteBranches -- localBranches
        val branchesToDelete = localBranches -- filteredRemoteBranches

        logger.info(s"Project ${project.id}, displayed branches: ${project.displayedBranches}, branches to update: ${branchesToUpdate.mkString(", ")}, branches to checkout: ${branchesToCheckout.mkString(", ")}, branches to delete: ${branchesToDelete.mkString(", ")}")

        for {
          _ <- checkoutBranches(project, branchesToCheckout)
          _ <- updateBranches(project, branchesToUpdate)
          _ <- deleteBranches(project.id, branchesToDelete)
        } yield ()
      }
    }
  }

  @silent("Interpolated")
  @silent("missing interpolator")
  def getVariables(page: Page): Option[Seq[Variable]] = {
    for {
      directory <- directoryRepository.findById(page.directoryId)
      branch <- branchRepository.findById(directory.branchId)
      project <- projectRepository.findById(branch.projectId)
      availableImplicitVariable = Seq(Variable("${project.current}", s"${project.name}"), Variable("${branch.current}", s"${branch.name}"), Variable("${branch.stable}", s"${project.stableBranch}"))
    } yield project.variables.getOrElse(Seq()).++(availableImplicitVariable)
  }
}
