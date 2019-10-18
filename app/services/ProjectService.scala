package services

import java.io._

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
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

              if (featureRepository.findAllByBranchId(branch.id).nonEmpty) {
                val featureToCreate = filterFeatureFile(created, project.featuresRootPath)
                val featureToUpdate = filterFeatureFile(updated, project.featuresRootPath)
                val featureToDelete = filterFeatureFile(deleted, project.featuresRootPath)


                (featureToUpdate ++ featureToDelete).flatMap(path => featureRepository.findByBranchIdAndPath(branch.id, path)).foreach(featureRepository.delete)

                (featureToUpdate ++ featureToCreate).flatMap(filePath => featureService.parseFeatureFile(project.id, branch, getLocalRepository(project.id, branchName) + filePath).toOption).foreach(featureRepository.save)

                logger.info(s"${featureToCreate.size} features created, ${featureToUpdate.size} features updated and ${featureToDelete.size} features deleted, for project ${project.id} on branch ${branch.name}")

              } else {
                parseAndSaveFeatures(project, branch)
              }

              project.documentationRootPath.foreach { documentationRootPath =>
                if (directoryRepository.findAllByBranchId(branch.id).nonEmpty) {

                  val directoryToCreate = filterDocumentationMetaFile(created, project.documentationRootPath)
                  val directoryToUpdate = filterDocumentationMetaFile(updated, project.documentationRootPath)
                  val directoryToDelete = filterDocumentationMetaFile(deleted, project.documentationRootPath)

                  val pageToCreate = filterDocumentationFile(created, project.documentationRootPath)
                  val pageToUpdate = filterDocumentationFile(updated, project.documentationRootPath)
                  val pageToDelete = filterDocumentationFile(deleted, project.documentationRootPath)

                  (directoryToUpdate ++ directoryToDelete).flatMap(path => directoryRepository.findByBranchIdAndRelativePath(branch.id, path.substring(path.indexOf(documentationRootPath) + documentationRootPath.length, path.length))).foreach(directoryRepository.delete)

                  pageToDelete.flatMap(path => pageRepository.findByPath(pageService.getPagePath(project.id, branch.name, path, project.documentationRootPath.getOrElse("")))).foreach(pageRepository.delete)

                  (directoryToCreate ++ directoryToUpdate).flatMap(directoryPath => pageService.processDirectory(branch, directoryPath, getLocalRepository(project.id, branchName) + directoryPath))

                  pageToUpdate.flatMap(path => pageRepository.findByPath(pageService.getPagePath(project.id, branch.name, path, project.documentationRootPath.getOrElse("")))).foreach(pageService.processPage(project.id, branch.name, _, documentationRootPath))

                  logger.info(s"${directoryToCreate.size} directories created, ${directoryToUpdate.size} directories updated and ${directoryToDelete.size} directories deleted, for project ${project.id} on branch ${branch.name}")
                  logger.info(s"${pageToCreate.size} pages created, ${pageToUpdate.size} pages updated and ${pageToDelete.size} pages deleted, for project ${project.id} on branch ${branch.name}")

                } else {
                  parseAndSaveDirectoriesAndPages(project, branch)
                }
              }
            }
        }.recoverWith {
          case _ => deleteBranches(project.id, Set(branchName)).flatMap(_ => checkoutBranches(project, Set(branchName)))
        }
      }
    } else Future.successful(())
  }

  def deleteBranches(projectId: String, branches: Set[String]): Future[Unit] = {
    if (branches.nonEmpty) {
      logger.info(s"delete $projectId branches ${branches.mkString(", ")}")

      Future {
        for (branch <- branches) {
          deleteDirectory(new File(getLocalRepository(projectId, branch)))
          branchRepository.findByProjectIdAndName(projectId, branch).map(_.id).foreach { branchId =>
            featureRepository.deleteAllByBranchId(branchId)
            directoryRepository.deleteAllByBranchId(branchId)
          }
        }

        branchRepository.deleteAll(branchRepository.findAllByProjectId(projectId).filter(b => branches.contains(b.name)))

      }.logError(s"Error while deleting project $projectId branches ${branches.mkString(", ")}")

    } else Future.successful(())
  }

  def synchronizeAll(): Future[Unit] = {
    logger.info("Start synchronizing projects")
    val projects = projectRepository.findAll()
    FutureExt.sequentially(projects)(synchronize).flatMap(_ => Future.fromTry(menuService.refreshCache())).map(_ => logger.info(s"${projects.size} projects synchronized"))
  }

  def synchronize(project: Project): Future[Unit] = {

    if (!synchronizeFromRemoteEnabled) {
      logger.info(s"No synchronization of project ${project.id}, as this feature is disabled")
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
}
