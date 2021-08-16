package services

import models._
import repositories._
import utils.FutureExt

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class DirectoryService @Inject()(directoryRepository: DirectoryRepository, pageRepository: PageRepository) {

  def buildDirectoryTree(branchId: Long): Option[Directory] = {
    val directoriesWithPagesAsList = directoryRepository.findAllByBranchId(branchId).map { directory =>
      directory.pages = pageRepository.findAllByDirectoryIdWithContent(directory.id)
      directory
    }.sortWith(_.relativePath < _.relativePath)
    directoriesWithPagesAsList.headOption.map { root =>
      val otherDirectories = directoriesWithPagesAsList.filterNot(_ == root)
      buildDirectoryTreeRecursive(root, otherDirectories)
    }
  }

  private def buildDirectoryTreeRecursive(current: Directory, directories: Seq[Directory]): Directory = {
    val (children, others) = directories.partition {
      d => d.relativePath == s"${current.relativePath}${d.name}/"
    }
    children.foreach(d => d.parent = Some(current))
    if (others.isEmpty) {
      current.children = children
      current
    } else {
      val childrenWithChildren = children.map { child =>
        buildDirectoryTreeRecursive(child, others.filter(_.relativePath.startsWith(child.relativePath)).filterNot(_ == child))
      }
      current.children = childrenWithChildren
      current
    }
  }

  def walkOnDirectoryTree[L, R](directory: Directory, action: Directory => Future[Either[L, R]], failure: (Throwable) => L): Future[Either[L, Seq[R]]] = {
    val actions = walkOnDirectoryTreeRecursive(directory, action, failure, List())
    FutureExt.seq {
      actions
    }.map { results =>
      val (lefts, rights) = results.partition(_.isLeft)
      if (lefts.nonEmpty) {
        Left(lefts.head.swap.getOrElse(null.asInstanceOf[L]))
      } else {
        Right(rights.map(r => r.getOrElse(null.asInstanceOf[R])))
      }
    }
  }

  private def walkOnDirectoryTreeRecursive[L, R](directory: Directory, action: Directory => Future[Either[L, R]], failure: (Throwable) => L, acc: List[() => Future[Either[L, R]]]): List[() => Future[Either[L, R]]] = {
    val current = acc.+:(() => action(directory))
    if (directory.children.nonEmpty) {
      val childrenActions = directory.children.flatMap { child =>
        walkOnDirectoryTreeRecursive(child, action, failure, acc)
      }.toList
      current.concat(childrenActions)
    } else {
      current
    }
  }
}


