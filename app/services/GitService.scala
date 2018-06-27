package services

import java.io.File

import javax.inject.Inject
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}


class GitService @Inject()(implicit ec: ExecutionContext) {

  def clone(url: String, localDirectory: String): Future[Unit] = {
    Future {
      Git.cloneRepository().setURI(url).setDirectory(new File(localDirectory)).call()
      Logger.info(s"Cloning $url to $localDirectory")
    }
  }


  def checkout(branch: String, localRepository: String): Future[Unit] = Future {
    Future {
      val git = new Git(new FileRepository(localRepository))
      git.checkout().setName(branch).setCreateBranch(true).addPath("target/data/gitService/local/test").call()
      Logger.info(s"checkout a $branch to $localRepository")
    }
  }

  def pull(localRepository: String): Future[Unit] = {
    Future {
      val git = new Git(new FileRepository(localRepository))
      git.pull().call()
      Logger.info(s"pull to $localRepository")
    }
  }


}
