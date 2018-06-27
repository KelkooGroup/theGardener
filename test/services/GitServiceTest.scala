package services


import java.io.File

import org.apache.commons.io.FileUtils._
import org.eclipse.jgit.api.Git
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class GitServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with ScalaFutures {

  val remoteRepositoryDirectory = new File("target/data/gitService/remote")
  val localRepositoryDirectory = "target/data/gitService/local"
  val remoteFile = new File(remoteRepositoryDirectory, "test.txt")
  val localFile = new File(localRepositoryDirectory, "test.txt")


  var git: Git = _

  before {
    git = Git.init().setDirectory(remoteRepositoryDirectory).call()
    addFileToRemote(git, remoteFile, "test commit")
  }

  private def addFileToRemote(git: Git, file: File, message: String) = {
    forceMkdirParent(file)
    write(file, "test", "UTF-8")


    git.add().addFilepattern(".").call()

    git.commit().setMessage(message).call()
  }

  after {
    deleteDirectory(remoteRepositoryDirectory)
    deleteDirectory(new File(localRepositoryDirectory))
  }

  "GitService" should {
    "clone a remote repository" in {
      val future = new GitService().clone(remoteRepositoryDirectory.toURI.toString, localRepositoryDirectory)

      whenReady(future, timeout(30.seconds)) { _ =>
        readFileToString(localFile, "UTF-8") mustBe readFileToString(remoteFile, "UTF-8")
      }
    }

    "checkout a branch" in {
      val future = new GitService().checkout("test", localRepositoryDirectory)
      val remoteFile1 = new File(remoteRepositoryDirectory, "test1.txt")
      val localFile1 = new File(localRepositoryDirectory, "test1.txt")

      git.branchCreate().setName("test").call()
      git.checkout().setName("test").call()

      addFileToRemote(git, remoteFile1, " created test to remoteRepository")
      addFileToRemote(git, localFile1, " created test to localRepository")

      whenReady(future, timeout(30.seconds)) { _ =>
        readFileToString(remoteFile1, "UTF-8") mustBe readFileToString(localFile1, "UTF-8")
      }
    }

    "pull the updates from remote " ignore {
      val future = new GitService().pull(localRepositoryDirectory)
      val remoteFile2 = new File(remoteRepositoryDirectory, "test2.txt")
      val localFile2 = new File(localRepositoryDirectory, "test2.txt")

      git.fetch().setRemote(remoteFile2.getPath).call()
      git.pull()

      addFileToRemote(git, remoteFile2, "created test2")
      addFileToRemote(git, localFile2, "created test2")

      whenReady(future, timeout(30.seconds)) { _ =>
        readFileToString(remoteFile2, "UTF-8") mustBe readFileToString(localFile2, "UTF-8")
      }
    }
  }
}
