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

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService, config: Config, actorSystem: ActorSystem, featureService: FeatureService, featureRepository: FeatureRepository, branchRepository: BranchRepository, criteriaService: CriteriaService)(implicit ec: ExecutionContext) {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val synchronizeInterval = config.getInt("projects.synchronize.interval")
  val synchronizeInitialDelay = config.getInt("projects.synchronize.initial.delay")

  actorSystem.scheduler.schedule(initialDelay = synchronizeInitialDelay.seconds, interval = synchronizeInterval.seconds)(synchronizeAll())

  def getLocalRepository(projectId: String, branch: String) = s"$projectsRootDirectory$projectId/$branch/"

  def checkoutRemoteBranches(project: Project): Future[Unit] = {
    for {
      remoteBranches <- gitService.getRemoteBranches(project.repositoryUrl)
    } yield checkoutBranches(project, remoteBranches.toSet)
  }

  private def parseAndSaveFeatures(project: Project, branch: Branch): Unit = {
    val features = featureService.parseBranchDirectory(project, branch, getLocalRepository(project.id, branch.name) + project.featuresRootPath)
    featureRepository.saveAll(features)

    Logger.debug(s"${features.size} features saved for project ${project.id} on branch ${branch.name}")
  }

  private def checkoutBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) Logger.debug(s"git checkout ${project.id} branches ${branches.mkString(",")}")

    Future.sequence(
      branches.map { name =>
        val localRepository = getLocalRepository(project.id, name)

        val branch = branchRepository.save(Branch(-1, name, name == project.stableBranch, project.id))

        for {
          _ <- gitService.clone(project.repositoryUrl, localRepository)
          _ <- gitService.checkout(name, localRepository)
        } yield parseAndSaveFeatures(project, branch)
      }
    )
  }

  def filterFeatureFile(filePaths: Seq[String]): Seq[String] = filePaths.filter(_.endsWith(".feature"))

  private def updateBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) Logger.debug(s"git pull ${project.id} branches ${branches.mkString(",")}")

    Future.sequence(
      branches.map { branchName =>
        gitService.pull(getLocalRepository(project.id, branchName)).map { case (created, updated, deleted) =>
          (filterFeatureFile(created), filterFeatureFile(updated), filterFeatureFile(deleted))
        }.map { case (created, updated, deleted) =>
          branchRepository.findByProjectIdAndName(project.id, branchName).foreach { branch =>
            (updated ++ deleted).flatMap(path => featureRepository.findByBranchIdAndPath(branch.id, path)).foreach(featureRepository.delete)

            (created ++ updated).flatMap(filePath => featureService.parseFeatureFile(project.id, branch, getLocalRepository(project.id, branchName) + filePath)).foreach(featureRepository.save)

            Logger.debug(s"${created.size} features created, ${updated.size} features updated and ${deleted.size} features deleted, for project ${project.id} on branch ${branch.name}")
          }
        }
      }
    )
  }

  def deleteBranches(project: Project, branches: Set[String]) = {
    if (branches.nonEmpty) Logger.debug(s"delete ${project.id} branches ${branches.mkString(",")}")

    Future {
      for (branch <- branches) {
        deleteDirectory(new File(getLocalRepository(project.id, branch)))
        branchRepository.findByProjectIdAndName(project.id, branch).map(_.id).foreach(featureRepository.deleteAllByBranchId)
      }

      branchRepository.deleteAll(branchRepository.findAllByProjectId(project.id).filter(b => branches.contains(b.name)))
    }
  }

  def synchronizeAll(): Future[Unit] = {
    Logger.info("Start synchronizing projects")

    val projects = projectRepository.findAll()

    Future.sequence(
      projects.map(synchronize)
    ).map(_ => criteriaService.refreshCache())
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

      Logger.info(s"Project ${project.id}, branches to update: ${branchesToUpdate.mkString(",")}, branches to checkout: ${branchesToCheckout.mkString(",")}, branches to delete: ${branchesToDelete.mkString(",")}")

      for {
        _ <- updateBranches(project, branchesToUpdate)
        _ <- checkoutBranches(project, branchesToCheckout)
      } yield deleteBranches(project, branchesToDelete)
    }
  }
}
