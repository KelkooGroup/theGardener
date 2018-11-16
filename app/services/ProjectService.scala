package services

import java.io._

import akka.actor.ActorSystem
import com.typesafe.config._
import javax.inject._
import models._
import org.apache.commons.io.FileUtils._
import play._
import repository._

import scala.concurrent._
import scala.concurrent.duration._

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService, config: Config, actorSystem: ActorSystem, featureService: FeatureService, featureRepository: FeatureRepository, branchRepository: BranchRepository)(implicit ec: ExecutionContext) {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val synchronizeInterval = config.getInt("projects.synchronize.interval")
  val synchronizeInitialDelay = config.getInt("projects.synchronize.initial.delay")

  actorSystem.scheduler.schedule(initialDelay = synchronizeInitialDelay.seconds, interval = synchronizeInterval.seconds)(synchronizeAll())

  def getLocalRepository(projectId: String, branch: String) = s"$projectsRootDirectory$projectId/$branch"

  def checkoutRemoteBranches(project: Project): Future[Unit] = {
    for {
      remoteBranches <- gitService.getRemoteBranches(project.repositoryUrl)
    } yield checkoutBranches(project, remoteBranches.toSet)
  }

  private def parseAndSaveFeatures(project: Project, branchName: String, branchId: Long): Unit = {
    val features = featureService.parseBranchDirectory(project, branchId, getLocalRepository(project.id, branchName) + "/" + project.featuresRootPath)
    featureRepository.saveAll(features)

    Logger.debug(s"${features.size} features saved for project ${project.id} on branch $branchName")
  }

  private def checkoutBranches(project: Project, branches: Set[String]) = {
    Logger.debug(s"git checkout ${project.id} branches ${branches.mkString(",")}")

    Future.sequence(
      branches.map { name =>
        val localRepository = getLocalRepository(project.id, name)

        val branchId = branchRepository.save(Branch(-1, name, name == project.stableBranch, project.id)).id

        for {
          _ <- gitService.clone(project.repositoryUrl, localRepository)
          _ <- gitService.checkout(name, localRepository)
        } yield parseAndSaveFeatures(project, name, branchId)
      }
    )
  }

  private def updateBranches(project: Project, branches: Set[String]) = {
    Logger.debug(s"git pull ${project.id} branches ${branches.mkString(",")}")

    Future.sequence(
      branches.map { branch =>
        gitService.pull(getLocalRepository(project.id, branch)).map { _ =>
          branchRepository.findByProjectIdAndName(project.id, branch).map(_.id).foreach { branchId =>
            featureRepository.deleteAllByBranchId(branchId)
            parseAndSaveFeatures(project, branch, branchId)
          }
        }
      }
    )
  }

  private def deleteBranches(project: Project, branches: Set[String]) = {
    Logger.debug(s"delete ${project.id} branches ${branches.mkString(",")}")

    Future {
      for (branch <- branches) {
        deleteDirectory(new File(getLocalRepository(project.id, branch)))
        branchRepository.findByProjectIdAndName(project.id, branch).map(_.id).foreach(featureRepository.deleteAllByBranchId)
      }

      branchRepository.deleteAll(branchRepository.findAllByProjectId(project.id).filter(b => branches.contains(b.name)))
    }
  }

  def synchronizeAll(): Future[Seq[Unit]] = {
    Logger.info("Start synchronizing projects")

    val projects = projectRepository.findAll()

    Future.sequence(
      projects.map(synchronize)
    )
  }

  def synchronize(project: Project): Future[Unit] = {
    Logger.debug(s"Start synchronizing project ${project.id}")

    val localBranches = branchRepository.findAllByProjectId(project.id).map(_.name).toSet

    Logger.debug(s"Local branches of project ${project.id} : ${localBranches.mkString(",")}")

    gitService.getRemoteBranches(project.repositoryUrl).map(_.toSet).flatMap { remoteBranches =>
      Logger.debug(s"Remotes branches of project ${project.id} : ${remoteBranches.mkString(",")}")

      val branchesToUpdate = localBranches.intersect(remoteBranches)
      val branchesToCheckout = remoteBranches -- localBranches
      val branchesToDelete = localBranches -- remoteBranches

      Logger.info(s"Project ${project.id}, branches to update: $branchesToUpdate, branches to checkout: $branchesToCheckout, branches to delete: $branchesToDelete")

      for {
        _ <- updateBranches(project, branchesToUpdate)
        _ <- checkoutBranches(project, branchesToCheckout)
      } yield deleteBranches(project, branchesToDelete)
    }
  }
}
