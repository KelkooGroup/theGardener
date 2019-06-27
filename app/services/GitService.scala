package services

import java.io.File

import javax.inject.Inject
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry.ChangeType
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import play.api.Logging
import resource._
import utils._

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scala.concurrent._
import scala.util.Try


class GitService @Inject()(implicit ec: ExecutionContext) extends Logging {

  def clone(url: String, localDirectory: String): Future[Unit] = {
    Future {
      Git.cloneRepository().setURI(url).setDirectory(new File(localDirectory)).call().close()
      logger.info(s"Cloning $url to $localDirectory")

    }.logError(s"Error while cloning repository $url in $localDirectory")
  }

  def checkout(branch: String, localRepository: String): Future[Unit] = {
    Future {
      managed(Git.open(new File(localRepository))).acquireAndGet { git =>

        Try(git.fetch().call())

        Try(git.branchCreate().setName(branch).setUpstreamMode(SET_UPSTREAM).setStartPoint(s"origin/$branch").setForce(true).call())
          .logError(s"Error while creating the local branch $branch in $localRepository")

        git.checkout.setName(branch).call()
      }

      logger.info(s"git checkout $localRepository to branch $branch")

    }.logError(s"Error while checkout branch $branch in $localRepository")
  }

  def pull(localRepository: String): Future[(Seq[String], Seq[String], Seq[String])] = {
    Future {

      managed(Git.open(new File(localRepository))).acquireAndGet { git =>

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

        val logMessage = if (created.isEmpty && updated.isEmpty && deleted.isEmpty) "no changes"
        else {
          val changes = ListBuffer[String]()
          if (created.nonEmpty) changes += "created : " + created.mkString(", ")
          if (updated.nonEmpty) changes += "updated : " + updated.mkString(", ")
          if (deleted.nonEmpty) changes += "deleted : " + updated.mkString(", ")

          changes.mkString(", ")
        }

        logger.info(s"git pull in $localRepository : $logMessage")

        (created, updated, deleted)
      }

    }.logError(s"Error while pull updates in $localRepository")
  }

  def getRemoteBranches(url: String): Future[Seq[String]] = {
    logger.debug(s"Get remote branches of $url")
    Future {
      val branches = Git.lsRemoteRepository().setHeads(true).setRemote(url).call().asScala.toSeq.map(_.getName.replace("refs/heads/", ""))
      logger.info(s"git ls $url : ${branches.mkString(", ")}")

      branches

    }.logError(s"Error while getting remote branches of $url")
  }
}
