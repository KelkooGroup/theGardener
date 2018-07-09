package services

import com.typesafe.config.Config
import javax.inject.Inject
import models.Project
import repository.ProjectRepository

import scala.concurrent._

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService, config: Config)(implicit ec: ExecutionContext) {
  val projectsRootDirectory = config.getString("projects.root.directory")

  def getLocalRepository(projectId: String, branch: String) = s"$projectsRootDirectory/$projectId/$branch"

  def create(project: Project): Future[Unit] = {
    for {
      remoteBranches <- gitService.getRemoteBranches(project.repositoryUrl)
      _ <- checkoutBranches(project, remoteBranches)

    } yield projectRepository.save(project)
  }

  private def checkoutBranches(project: Project, branches: Seq[String]) = {
    Future.sequence(
      branches.map { branch =>
        val localRepository = getLocalRepository(project.id, branch)

        for {
          _ <- gitService.clone(project.repositoryUrl, localRepository)
          res <- gitService.checkout(branch, localRepository)
        } yield res
      }
    )
  }

  def synchronizeAll(): Future[Unit] = {
    ???
  }
}
