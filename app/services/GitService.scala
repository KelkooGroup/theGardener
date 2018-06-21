package services

import java.io.File

import javax.inject.Inject
import org.eclipse.jgit.api.Git
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}


class GitService @Inject()(implicit ec: ExecutionContext) {

  def clone(url: String, localDirectory: String): Future[Unit] = {
    Future {
      val featureFile = new File(localDirectory)
      val log = Logger


      log.info("Cloning " + url)
      log.info("to " + featureFile)

      Git.cloneRepository().setURI(url).setDirectory(featureFile).call()
    }


  }


  def checkout(branch: String, localRepository: String): Future[Unit] = {
    ???
  }

  def pull(localRepository: String): Future[Unit] = {
    ???
  }


}
