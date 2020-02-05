package services

import java.io.File
import java.io.File.separator


import utils.CustomConfigSystemReader._
import org.apache.commons.io.FileUtils._
import org.eclipse.jgit.api._
import org.scalatest._
import org.scalatest.concurrent._
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global


class GitServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with ScalaFutures {
  override implicit val patienceConfig = PatienceConfig(timeout = scaled(30.seconds))

  val encoding = "UTF-8"

  val remoteRepositoryDirectory = new File(s"target${separator}data${separator}gitService${separator}remote")
  val localRepositoryDirectory = s"target${separator}data${separator}gitService${separator}local"

  val remoteFile = new File(remoteRepositoryDirectory, "test.txt")
  val localFile = new File(localRepositoryDirectory, "test.txt")

  var remoteGit: Git = _

  overrideSystemGitConfig()

  before {
    deleteDirectory(new File(localRepositoryDirectory))
    deleteDirectory(remoteRepositoryDirectory)
    remoteGit = Git.init().setDirectory(remoteRepositoryDirectory).call()
    addFileToRemote(remoteGit, remoteFile, "commit test.txt")
  }

  after {
    deleteDirectory(remoteRepositoryDirectory)
    deleteDirectory(new File(localRepositoryDirectory))
  }

  def addFileToRemote(git: Git, file: File, message: String): Unit = {
    forceMkdirParent(file)
    write(file, "test", encoding)

    git.add().addFilepattern(".").call()

    git.commit().setMessage(message).call()
    ()
  }

  "GitService" should {
    "clone a remote repository" in {
      val future = new GitService().clone(remoteRepositoryDirectory.toURI.toString, localRepositoryDirectory)

      whenReady(future) { _ =>
        readFileToString(localFile, encoding) mustBe readFileToString(remoteFile, encoding)
      }
    }

    "checkout a branch" in {
      val branchName = "newbranch"
      val fileName = "test1.txt"
      val remoteFile1 = new File(remoteRepositoryDirectory, fileName)

      Git.cloneRepository().setURI(remoteRepositoryDirectory.toURI.toString).setDirectory(new File(localRepositoryDirectory)).call().close()

      createBranch(branchName, remoteFile1)

      val future = new GitService().checkout(branchName, localRepositoryDirectory)

      whenReady(future) { _ =>
        readFileToString(remoteFile1, encoding) mustBe readFileToString(new File(localRepositoryDirectory, fileName), encoding)
      }
    }

    "pull the updates from remote" in {
      Git.cloneRepository().setURI(remoteRepositoryDirectory.toURI.toString).setDirectory(new File(localRepositoryDirectory)).call().close()

      val remoteFile2 = new File(remoteRepositoryDirectory, "test2.txt")
      addFileToRemote(remoteGit, remoteFile2, "commit test2.txt")

      val future = new GitService().pull(localRepositoryDirectory)

      whenReady(future) { case (created, updated, deleted) =>

        created mustBe Seq("test2.txt")
        updated mustBe Seq()
        deleted mustBe Seq()

        readFileToString(remoteFile2, encoding) mustBe readFileToString(new File(localRepositoryDirectory, "test2.txt"), encoding)
      }
    }

    "list the branch of remote" in {
      val branchName = "newbranch3"
      val fileName = "test3.txt"
      val remoteFile3 = new File(remoteRepositoryDirectory, fileName)

      createBranch(branchName, remoteFile3)
      val future = new GitService().getRemoteBranches(remoteRepositoryDirectory.toURI.toString)

      whenReady(future) { branches =>
        branches must contain theSameElementsAs Seq(branchName, "master")
      }
    }
  }

  private def createBranch(branchName: String, remoteFile: File) = {
    remoteGit.checkout().setCreateBranch(true).setName(branchName).call()
    addFileToRemote(remoteGit, remoteFile, "commit " + remoteFile.getName)
  }
}
