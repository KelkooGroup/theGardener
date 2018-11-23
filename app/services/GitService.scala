package services

import java.io.File

import javax.inject.Inject
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry.ChangeType
import org.eclipse.jgit.treewalk.CanonicalTreeParser
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

      Logger.info(s"git checkout $localRepository to branch $branch")
    }
  }

  def pull(localRepository: String): Future[(Seq[String], Seq[String], Seq[String])] = Future {

    val git = Git.open(new File(localRepository))

    val oldHead = git.getRepository.resolve("HEAD^{tree}")

    git.pull().call()

    val head = git.getRepository.resolve("HEAD^{tree}")

    val reader = git.getRepository.newObjectReader()

    val oldTreeIter = new CanonicalTreeParser()
    oldTreeIter.reset(reader, oldHead)

    val newTreeIter = new CanonicalTreeParser()
    newTreeIter.reset(reader, head)

    val diffs = git.diff.setNewTree(newTreeIter).setOldTree(oldTreeIter).call().asScala

    val created = diffs.filter(diff => diff.getChangeType == ChangeType.ADD || diff.getChangeType == ChangeType.RENAME || diff.getChangeType == ChangeType.COPY).map(_.getNewPath)
    val updated = diffs.filter(_.getChangeType == ChangeType.MODIFY).map(_.getOldPath)
    val deleted = diffs.filter(diff => diff.getChangeType == ChangeType.DELETE || diff.getChangeType == ChangeType.RENAME).map(_.getOldPath)

    Logger.info(s"git pull in $localRepository created : ${created.mkString(",")}, updated : ${updated.mkString(",")}, deleted : ${deleted.mkString(",")} ")

    (created, updated, deleted)
  }

  def getRemoteBranches(url: String): Future[Seq[String]] = {
    Logger.debug(s"Get remote branches of $url")
    Future {
      val branches = Git.lsRemoteRepository().setHeads(true).setRemote(url).call().asScala.toSeq.map(_.getName.replace("refs/heads/", ""))
      Logger.info(s"git ls $url : ${branches.mkString(",")}")

      branches
    }
  }
}
