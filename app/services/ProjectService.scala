package services

import java.io._

import akka.actor.ActorSystem
import com.typesafe.config._
import javax.inject._
import models._
import org.apache.commons.io.FileUtils._
import play.api.Logging
import repository._
import utils._

import scala.concurrent._
import scala.concurrent.duration._

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService, featureService: FeatureService, featureRepository: FeatureRepository, branchRepository: BranchRepository, criteriaService: CriteriaService,
                               config: Config, actorSystem: ActorSystem)(implicit ec: ExecutionContext) extends Logging {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val synchronizeInterval = config.getInt("projects.synchronize.interval")
  val synchronizeInitialDelay = config.getInt("projects.synchronize.initial.delay")

  actorSystem.scheduler.schedule(initialDelay = synchronizeInitialDelay.seconds, interval = synchronizeInterval.seconds)(synchronizeAll())

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def checkoutRemoteBranches(project: Project): Future[Unit] = {
    for {
      remoteBranches <- gitService.getRemoteBranches(project.repositoryUrl)
    } yield checkoutBranches(project, remoteBranches.toSet)
  }

  private def parseAndSaveFeatures(project: Project, branch: Branch): Unit = {
    val features = featureService.parseBranchDirectory(project, branch, getLocalRepository(project.id, branch.name) + project.featuresRootPath)
    featureRepository.saveAll(features)

    logger.debug(s"${features.size} features saved for project ${project.id} on branch ${branch.name}")
  }

  private def checkoutBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) {
      logger.debug(s"checkout ${project.id} branches ${branches.mkString(", ")}")

      FutureExt.sequentially(branches.toSeq) { branchName =>
        val localRepository = getLocalRepository(project.id, branchName)

        val branch = branchRepository.save(Branch(-1, branchName, branchName == project.stableBranch, project.id))

        for {
          _ <- gitService.clone(project.repositoryUrl, localRepository)
          _ <- gitService.checkout(branchName, localRepository)
        } yield parseAndSaveFeatures(project, branch)
      }
    } else Future.successful(())
  }

  def filterFeatureFile(filePaths: Seq[String]): Seq[String] = filePaths.filter(_.endsWith(".feature"))

  private def updateBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) {
      logger.debug(s"update ${project.id} branches ${branches.mkString(", ")}")

      FutureExt.sequentially(branches.toSeq) { branchName =>

        val localRepo = getLocalRepository(project.id, branchName)
        val localRepoFile = new File(localRepo)
        if (!localRepoFile.exists()) {
          checkoutBranches(project, Set(branchName))
        }

        gitService.pull(localRepo).map { case (created, updated, deleted) =>
          (filterFeatureFile(created), filterFeatureFile(updated), filterFeatureFile(deleted))
        }.map { case (created, updated, deleted) =>
          branchRepository.findByProjectIdAndName(project.id, branchName).foreach { branch =>
            if (featureRepository.findAllByBranchId(branch.id).nonEmpty) {
              (updated ++ deleted).flatMap(path => featureRepository.findByBranchIdAndPath(branch.id, path)).foreach(featureRepository.delete)

              (created ++ updated).flatMap(filePath => featureService.parseFeatureFile(project.id, branch, getLocalRepository(project.id, branchName) + filePath).toOption).foreach(featureRepository.save)

              logger.debug(s"${created.size} features created, ${updated.size} features updated and ${deleted.size} features deleted, for project ${project.id} on branch ${branch.name}")
            } else {
              parseAndSaveFeatures(project, branch)
            }
          }
        }
      }
    } else Future.successful(())
  }

  def deleteBranches(projectId: String, branches: Set[String]): Future[Unit] = {
    if (branches.nonEmpty) {
      logger.debug(s"delete $projectId branches ${branches.mkString(", ")}")

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

    FutureExt.sequentially(projects)(synchronize).map(_ => criteriaService.refreshCache())
  }

  def synchronize(project: Project): Future[Unit] = {
    logger.debug(s"Start synchronizing project ${project.id}")

    val localBranches = branchRepository.findAllByProjectId(project.id).map(_.name).toSet

    logger.debug(s"Local branches of project ${project.id} : ${localBranches.mkString(", ")}")

    gitService.getRemoteBranches(project.repositoryUrl).map(_.toSet).flatMap { remoteBranches =>
      logger.debug(s"Remotes branches of project ${project.id} : ${remoteBranches.mkString(", ")}")

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
