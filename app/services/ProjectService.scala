package services

import javax.inject.Inject
import repository.ProjectRepository

import scala.concurrent._

class ProjectService @Inject()(projectRepository: ProjectRepository, gitService: GitService)(implicit ec: ExecutionContext) {

  def create(url: String): Future[Unit] = {
    ???
  }

  def update(localRepo: String, newBranch: String): Future[Unit] = {
    ???
  }

  def synchronizeAll(): Future[Unit] = {
    ???
  }
}
