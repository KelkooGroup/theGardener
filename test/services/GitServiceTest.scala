package services


import java.io.File

import org.apache.commons.io.FileUtils
import org.eclipse.jgit.api._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


class GitServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with ScalaFutures {

  val remoteRepositoryDirectory = new File("target/data/gitService/remote")
  val localRepositoryDirectory = "target/data/gitService/local"
  val remoteFile = new File(remoteRepositoryDirectory, "test.txt")
  val localFile = new File(localRepositoryDirectory, "test.txt")


  before {
    FileUtils.forceMkdirParent(remoteFile)
    FileUtils.write(remoteFile, "test", "UTF-8")

    val git = Git.init().setDirectory(remoteRepositoryDirectory).call()

    git.add().addFilepattern(".").call()

    git.commit().setMessage("test commit").call()
  }

  after {
    FileUtils.deleteDirectory(remoteRepositoryDirectory)
    FileUtils.deleteDirectory(new File(localRepositoryDirectory))
  }

  "GitService" should {
    "clone a remote repository" in {
      val future = new GitService().clone(remoteRepositoryDirectory.toURI.toString, localRepositoryDirectory)

      whenReady(future, timeout(30.seconds)) { _ =>
        FileUtils.readFileToString(localFile, "UTF-8") mustBe FileUtils.readFileToString(remoteFile, "UTF-8")
      }
    }

    "checkout a branch" ignore {
      //val future = new GitService().checkout()
    }

    "pull the updates from remote " ignore {
      ???
    }
  }
}
