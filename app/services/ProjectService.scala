package services

import java.io._

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.util.Timeout
import com.typesafe.config._
import javax.inject._
import models._
import org.apache.commons.io.FileUtils._
import play.api.{Environment, Logging, Mode}
import repository._
import utils._

import scala.concurrent._
import scala.concurrent.duration._

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService, featureService: FeatureService,
                               featureRepository: FeatureRepository, branchRepository: BranchRepository, directoryRepository: DirectoryRepository,
                               pageRepository: PageRepository, menuService: MenuService, pageService: PageService,
                               config: Config, environment: Environment, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends Logging {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val synchronizeInterval = config.getInt("projects.synchronize.interval")
  val synchronizeInitialDelay = config.getInt("projects.synchronize.initial.delay")
  val documentationMetaFile = config.getString("documentation.meta.file")

  if (environment.mode != Mode.Test) {
    val synchronizeJob = actorSystem.scheduler.schedule(initialDelay = synchronizeInitialDelay.seconds, interval = synchronizeInterval.seconds)(synchronizeAll())(actorSystem.dispatcher)
    CoordinatedShutdown(actorSystem).addTask(
      CoordinatedShutdown.PhaseBeforeServiceUnbind, "cancelSynchronizeJob") { () ⇒
      implicit val timeout = Timeout(5.seconds)
      synchronizeJob.cancel()
      Future.successful(Done)
    }
  }

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def checkoutRemoteBranches(project: Project): Future[Unit] = {
    for {
      remoteBranches <- gitService.getRemoteBranches(project.repositoryUrl)
    } yield checkoutBranches(project, remoteBranches.toSet)
  }

  private def parseAndSaveFeatures(project: Project, branch: Branch): Unit = {
    val features = featureService.parseBranchDirectory(project, branch, getLocalRepository(project.id, branch.name) + project.featuresRootPath)
    featureRepository.saveAll(features)

    logger.info(s"${features.size} features saved for project ${project.id} on branch ${branch.name}")
  }

  private def parseAndSaveDirectoriesAndPages(project: Project, branch: Branch): Unit = {
    val directory = pageService.processDirectory(branch, project.id + ">" + branch.name + ">/", getLocalRepository(project.id, branch.name) + project.documentationRootPath.get)
    logger.info(s" directories and pages from ${directory.get.path} saved for project ${project.id} on branch ${branch.name}")
  }

  private def checkoutBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) {
      logger.info(s"checkout ${project.id} branches ${branches.mkString(", ")}")

      FutureExt.sequentially(branches.toSeq) {
        branchName =>
          val localRepository = getLocalRepository(project.id, branchName)

          val branch = branchRepository.save(Branch(-1, branchName, branchName == project.stableBranch, project.id))

          for {
            _ <- gitService.clone(project.repositoryUrl, localRepository)
            _ <- gitService.checkout(branchName, localRepository)
          } yield (parseAndSaveFeatures(project, branch), parseAndSaveDirectoriesAndPages(project, branch))
      }
    } else Future.successful(())
  }

  def filterFeatureFile(filePaths: Seq[String]): Seq[String] = filePaths.filter(_.endsWith(".feature"))

  def filterDocumentationFile(filePaths: Seq[String]): Seq[String] = filePaths.filter(_.endsWith(".md"))

  def filterDocumentationMetaFile(filePaths: Seq[String]): Seq[String] = filePaths.filter(_.endsWith(documentationMetaFile))

  private def updateBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) {
      logger.info(s"update ${project.id} branches ${branches.mkString(", ")}")

      FutureExt.sequentially(branches.toSeq) {
        branchName =>

          val localRepo = getLocalRepository(project.id, branchName)
          val localRepoFile = new File(localRepo)
          if (!localRepoFile.exists()) {
            checkoutBranches(project, Set(branchName))
          }

          gitService.pull(localRepo).map {
            case (created, updated, deleted) =>
              branchRepository.findByProjectIdAndName(project.id, branchName).foreach {
                branch =>
                  if (featureRepository.findAllByBranchId(branch.id).nonEmpty) {
                    filterFeatureFile(updated ++ deleted).flatMap(path => featureRepository.findByBranchIdAndPath(branch.id, path)).foreach(featureRepository.delete)

                    filterFeatureFile(created ++ updated).flatMap(filePath => featureService.parseFeatureFile(project.id, branch, getLocalRepository(project.id, branchName) + filePath).toOption).foreach(featureRepository.save)

                    logger.info(s"${created.size} features created, ${updated.size} features updated and ${deleted.size} features deleted, for project ${project.id} on branch ${branch.name}")

                  } else {
                    parseAndSaveFeatures(project, branch)
                  }

                  project.documentationRootPath.foreach { documentationRootPath =>
                    if (directoryRepository.findAllByBranchId(branch.id).nonEmpty) {

                      filterDocumentationMetaFile(updated ++ deleted).filter(_.contains(documentationRootPath)).flatMap { path =>
                        directoryRepository.findByBranchIdAndRelativePath(branch.id, path.substring(path.indexOf(documentationRootPath) + documentationRootPath.length, path.length))
                      }.foreach(directoryRepository.delete)

                      filterDocumentationFile(updated ++ deleted).flatMap(path => pageRepository.findByPath(path)).foreach(pageRepository.delete)

                      filterDocumentationMetaFile(created ++ updated).flatMap(directoryPath => pageService.processDirectory(branch, directoryPath, getLocalRepository(project.id, branchName) + directoryPath))

                      logger.info(s"${created.size} directories created, ${updated.size} directories updated and ${deleted.size} directories deleted, for project ${project.id} on branch ${branch.name}")

                    } else {
                      parseAndSaveDirectoriesAndPages(project, branch)
                    }
                  }
              }
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
          branchRepository.findByProjectIdAndName(projectId, branch).map(_.id).foreach(featureRepository.deleteAllByBranchId)
        }

        branchRepository.deleteAll(branchRepository.findAllByProjectId(projectId).filter(b => branches.contains(b.name)))

      }.logError(s"Error while deleting project $projectId branches ${branches.mkString(", ")}")

    } else Future.successful(())
  }

  def synchronizeAll(): Future[Unit] = {
    logger.info("Start synchronizing projects")

    val projects = projectRepository.findAll()

    FutureExt.sequentially(projects)(synchronize).map(_ => menuService.refreshCache())
  }

  def synchronize(project: Project): Future[Unit] = {
    logger.info(s"Start synchronizing project ${project.id}")

    val localBranches = branchRepository.findAllByProjectId(project.id).map(_.name).toSet

    logger.info(s"Local branches of project ${project.id} : ${localBranches.mkString(", ")}")

    gitService.getRemoteBranches(project.repositoryUrl).map(_.toSet).flatMap {
      remoteBranches =>
        logger.info(s"Remotes branches of project ${project.id} : ${remoteBranches.mkString(", ")}")

        val branchesToUpdate = localBranches.intersect(remoteBranches)
        val branchesToCheckout = remoteBranches -- localBranches
        val branchesToDelete = localBranches -- remoteBranches

        logger.info(s"Project ${project.id}, branches to update: ${branchesToUpdate.mkString(", ")}, branches to checkout: ${branchesToCheckout.mkString(", ")}, branches to delete: ${branchesToDelete.mkString(", ")}")

        for {
          _ <- checkoutBranches(project, branchesToCheckout)
          _ <- updateBranches(project, branchesToUpdate)
        } yield deleteBranches(project.id, branchesToDelete)
    }
  }
}
