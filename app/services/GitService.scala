package services

import java.io.File

import javax.inject.Inject
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode._
import org.eclipse.jgit.api.Git
import play.api.Logger

import scala.collection.JavaConverters._
import scala.concurrent._


class GitService @Inject()(implicit ec: ExecutionContext) {

  def clone(url: String, localDirectory: String): Future[Unit] = {
    Future {
      Git.cloneRepository().setURI(url).setDirectory(new File(localDirectory)).call()
      Logger.info(s"Cloning $url to $localDirectory")
    }
  }

  def checkout(branch: String, localRepository: String, create: Boolean = false): Future[Unit] = {
    Future {
      val git = Git.open(new File(localRepository))
      if (create) {
        git.checkout().setCreateBranch(true).setName(branch).setUpstreamMode(SET_UPSTREAM).setStartPoint(s"origin/$branch").call()
      } else {
        git.checkout.setName(branch).call()
      }

      Logger.info(s"checkout $localRepository to branch $branch")
    }
  }

  def pull(localRepository: String): Future[Unit] = {
    Future {
      Git.open(new File(localRepository)).pull().call()
      Logger.info(s"pull in $localRepository")
    }
  }

  def getRemoteBranches(url: String): Future[Seq[String]] = {
    Future {
      Git.lsRemoteRepository().setHeads(true).setRemote(url).call().asScala.toSeq.map(_.getName.replace("refs/heads/", ""))
    }
  }
}
