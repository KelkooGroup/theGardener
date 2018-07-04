package services

import java.io.File

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import play.api.Logger

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class SynchronizedService extends GitService {
  var git: Git = _


  def init(url: String): Future[Unit] = {
    Git.init().setDirectory(new File(url)).call()
    pull("target/data/gitService/local")
  }

  def update(localRepo: String, newBranch: String): Future[Unit] = {
    Future {
      val refs = git.branchList.call
      for (ref <- refs) {
        if (ref.getName.equals("refs/heads/newBranch")) {
          git.branchDelete.setBranchNames(newBranch).call
        }
      }

      git.branchCreate().setName(newBranch).call()
      checkout(newBranch, localRepo)
      val listRefsBranches = git.branchList().setListMode(ListMode.ALL).call()
      for (refBranch <- listRefsBranches) {
        Logger.info(s"Branch : " + refBranch.getName)
      }

      git.branchDelete().setBranchNames(newBranch).call()
      for (ref <- git.branchList.call) {
        Logger.info(s"Branch-After $ref")
      }
    }
  }

}
