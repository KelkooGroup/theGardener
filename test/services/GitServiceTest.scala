package services


import java.io.File

import org.apache.commons.io.FileUtils._
import org.eclipse.jgit.api.Git
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class GitServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with ScalaFutures {
  val encoding = "UTF-8"

  val remoteRepositoryDirectory = new File("target/data/gitService/remote")
  val localRepositoryDirectory = "target/data/gitService/local"

  val remoteFile = new File(remoteRepositoryDirectory, "test.txt")
  val localFile = new File(localRepositoryDirectory, "test.txt")

  var remoteGit: Git = _

  before {
    remoteGit = Git.init().setDirectory(remoteRepositoryDirectory).call()
    addFileToRemote(remoteGit, remoteFile, "commit test.txt")
  }

  after {
    deleteDirectory(remoteRepositoryDirectory)
    deleteDirectory(new File(localRepositoryDirectory))
  }

  def addFileToRemote(git: Git, file: File, message: String) : Unit = {
    forceMkdirParent(file)
    write(file, "test", encoding)

    git.add().addFilepattern(".").call()

    git.commit().setMessage(message).call()
  }

  "GitService" should {
    "clone a remote repository" in {
      val future = new GitService().clone(remoteRepositoryDirectory.toURI.toString, localRepositoryDirectory)

      whenReady(future, timeout(30.seconds)) { _ =>
        readFileToString(localFile, encoding) mustBe readFileToString(remoteFile, encoding)
      }
    }

    "checkout a branch" in {
      val branchName = "newbranch"

      remoteGit.checkout().setCreateBranch(true).setName(branchName).call()

      val remoteFile1 = new File(remoteRepositoryDirectory, "test1.txt")
      addFileToRemote(remoteGit, remoteFile1, " commit test1.txt")

      Git.cloneRepository().setURI(remoteRepositoryDirectory.toURI.toString).setDirectory(new File(localRepositoryDirectory)).call()

      val future = new GitService().checkout(branchName, localRepositoryDirectory)

      whenReady(future, timeout(30.seconds)) { _ =>
        readFileToString(remoteFile1, encoding) mustBe readFileToString(new File(localRepositoryDirectory, "test1.txt"), encoding)
      }
    }

    "pull the updates from remote" in {
      Git.cloneRepository().setURI(remoteRepositoryDirectory.toURI.toString).setDirectory(new File(localRepositoryDirectory)).call()

      val remoteFile2 = new File(remoteRepositoryDirectory, "test2.txt")
      addFileToRemote(remoteGit, remoteFile2, "commit test2.txt")

      val future = new GitService().pull(localRepositoryDirectory)

      whenReady(future, timeout(30.seconds)) { _ =>
        readFileToString(remoteFile2, encoding) mustBe readFileToString(new File(localRepositoryDirectory, "test2.txt"), encoding)
      }
    }
  }
}
