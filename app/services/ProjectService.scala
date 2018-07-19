package services

import java.io._

import akka.actor.ActorSystem
import com.typesafe.config._
import javax.inject._
import models._
import org.apache.commons.io.FileUtils._
import org.apache.commons.io.filefilter._
import play._
import repository._

import scala.concurrent._
import scala.concurrent.duration._

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService, config: Config, actorSystem: ActorSystem)(implicit ec: ExecutionContext) {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val synchronizeInterval = config.getInt("projects.synchronize.interval")
  val synchronizeInitialDelay = config.getInt("projects.synchronize.initial.delay")

  actorSystem.scheduler.schedule(initialDelay = synchronizeInitialDelay.seconds, interval = synchronizeInterval.seconds)(synchronizeAll())

  def getLocalRepository(projectId: String, branch: String) = s"$projectsRootDirectory/$projectId/$branch"

  def checkoutRemoteBranches(project: Project): Future[Unit] = {
    for {
      remoteBranches <- gitService.getRemoteBranches(project.repositoryUrl)
    } yield checkoutBranches(project, remoteBranches.toSet)
  }

  private def checkoutBranches(project: Project, branches: Set[String]) = {
    Future.sequence(
      branches.map { branch =>
        val localRepository = getLocalRepository(project.id, branch)

        for {
          _ <- gitService.clone(project.repositoryUrl, localRepository)
        } yield gitService.checkout(branch, localRepository)
      }
    )
  }

  private def updateBranches(project: Project, branches: Set[String]) = {
    Future.sequence(
      branches.map { branch =>
        gitService.pull(getLocalRepository(project.id, branch))
      }
    )
  }

  private def deleteBranches(project: Project, branches: Set[String]) = {
    Future {
      for (branch <- branches) {
        deleteDirectory(new File(getLocalRepository(project.id, branch)))
      }
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
    val localBranchesArray = new File(s"$projectsRootDirectory/${project.id}").list(DirectoryFileFilter.INSTANCE)
    val localBranches = if (localBranchesArray != null) localBranchesArray.toSet else Set[String]()

    gitService.getRemoteBranches(project.repositoryUrl).map(_.toSet).flatMap { remoteBranches =>

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
