package services

import models._
import play.api.Logging
import repositories.{BranchRepository, ProjectRepository}
import services.clients.{ConfluenceClient, ConfluenceError, ConfluencePage, ConfluencePageTitle}
import utils.FutureExt

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}


case class ConfluenceSynchroContext(project: Project, confluenceProjectParentPage: ConfluencePage, directory: Directory)

class ConfluenceService @Inject()(confluenceClient: ConfluenceClient,
                                  projectRepository: ProjectRepository, branchRepository: BranchRepository, directoryService: DirectoryService
                                 )(implicit ec: ExecutionContext) extends Logging {

  def refreshProjectInConfluence(projectId: String): Future[Either[ConfluenceError, Seq[Seq[ConfluencePage]]]] = {
    projectRepository.findById(projectId) match {
      case None => Future.successful(Left(ConfluenceError(s"Project $projectId not found", ConfluenceError.PROJECT_NOT_FOUND)))
      case Some(project) => {
        branchRepository.findByProjectIdAndName(projectId, project.stableBranch) match {
          case None => Future.successful(Left(ConfluenceError(s"Branch $projectId ${project.stableBranch} not found", ConfluenceError.STABLE_BRANCH_NOT_FOUND)))
          case Some(branch) => {
            if (project.confluenceParentPageId.isEmpty) {
              Future.successful(Left(ConfluenceError(s"Project $projectId has no confluence page id", ConfluenceError.PROJECT_NOT_SETUP)))
            } else {
              directoryService.buildDirectoryTree(branch.id) match {
                case None => Future.successful(Left(ConfluenceError(s"No directories for $projectId ${project.stableBranch}", ConfluenceError.ROOT_DIRECTORY_NOT_FOUND)))
                case Some(directoriesWithPagesAsTree) => {
                  directoryService.walkOnDirectoryTree[ConfluenceError, Seq[ConfluencePage]](directoriesWithPagesAsTree,
                    (d: Directory) => refreshDirectoryInConfluence(project, project.confluenceParentPageId.get.toLong, d),
                    (t: Throwable) => ConfluenceError(t.getMessage))
                }
              }
            }
          }
        }
      }
    }
  }

  def refreshDirectoryInConfluence(project: Project, confluenceProjectParentPageId: Long, directory: Directory): Future[Either[ConfluenceError, Seq[ConfluencePage]]] = {
    confluenceClient.getConfluencePage(confluenceProjectParentPageId).map {
      case Right(confluenceProjectParentPage) => {
        if (directory.isRoot()) {
          refreshRootDirectoryInConfluence(project, confluenceProjectParentPageId, directory)
        } else {
          refreshInternalDirectoryInConfluence(confluenceProjectParentPage.space.key, directory)
        }
      }
      case Left(error) => Future.successful(Left(error))
    }.flatten
  }

  private def refreshRootDirectoryInConfluence(project: Project, confluenceProjectParentPageId: Long, rootDirectory: Directory): Future[Either[ConfluenceError, Seq[ConfluencePage]]] = {
    confluenceClient.getConfluencePage(confluenceProjectParentPageId).map {
      case Right(confluenceProjectParentPage) => {
        confluenceClient.getChildrenConfluencePageTitle(confluenceProjectParentPageId).map {
          case Right(existingProjects) => {
            existingProjects.find(p => ConfluenceClient.sameTitles(p.title, project.name)) match {
              case Some(existingConfluenceProjectPage) => {
                rootDirectory.externalId = Some(existingConfluenceProjectPage.id.toLong)
                createOrUpdateDirectoryPagesAndPurge(confluenceProjectParentPage.space.key, rootDirectory)
              }
              case None => {
                confluenceClient.createConfluencePage(confluenceProjectParentPage.space.key, confluenceProjectParentPage.id.toLong, project.name, "project", "").map {
                  case Left(error) => {
                    logger.warn(s"Unable to create root page for project '${project.name}'")
                    Future.successful(Left(error))
                  }
                  case Right(createdConfluenceProjectPage) => {
                    rootDirectory.externalId = Some(createdConfluenceProjectPage.id.toLong)
                    createOrUpdateDirectoryPagesAndPurge(confluenceProjectParentPage.space.key, rootDirectory)
                  }
                }.flatten
              }
            }
          }
          case Left(error) => Future.successful(Left(error))
        }.flatten
      }
      case Left(error) => Future.successful(Left(error))
    }.flatten
  }

  private def refreshInternalDirectoryInConfluence(space: String, internalDirectory: Directory): Future[Either[ConfluenceError, Seq[ConfluencePage]]] = {
    internalDirectory.parent match {
      case None => Future.successful(Left(ConfluenceError(s"Parent not provided for the directory ${internalDirectory.path}")))
      case Some(parentDirectory) =>
        parentDirectory.externalId match {
          case None => Future.successful(Left(ConfluenceError(s"ConfluencePageId not provided for the parent the directory ${internalDirectory.path}")))
          case Some(parentConfluencePageId) => {
            createOrUpdateConfluencePage(parentConfluencePageId, space, title = internalDirectory.name, titleContext = internalDirectory.path, content = "").map {
              case Left(error) => Future.successful(Left(error))
              case Right(confluencePage) => {
                internalDirectory.externalId = Some(confluencePage.id.toLong)
                createOrUpdateDirectoryPagesAndPurge(space, internalDirectory)
              }
            }.flatten
          }
        }
    }
  }

  private def createOrUpdateDirectoryPagesAndPurge(space: String, directory: Directory): Future[Either[ConfluenceError, Seq[ConfluencePage]]] = {
    directory.externalId match {
      case None => Future.successful(Left(ConfluenceError(s"Confluence page id not provided for $directory ")))
      case Some(externalId) => {
        createOrUpdateDirectoryPages(space, directory).flatMap {
          case Left(error) => Future.successful(Left(error))
          case Right(_) => {
            purgeUnknownPagesFromConfluenceDirectory(directory, externalId)
          }
        }
      }
    }
  }

  private def purgeUnknownPagesFromConfluenceDirectory(directory: Directory, confluencePageId: Long): Future[Either[ConfluenceError, Seq[ConfluencePage]]] = {
    confluenceClient.getChildrenConfluencePageTitle(confluencePageId).map {
      case Left(error) => Future.successful(Left(error))
      case Right(existingPages) => {
        val expectedPagesTitles = directory.pages.map(_.label).concat(directory.children.map(_.name))
        val pagesToRemove = existingPages.filterNot(e => expectedPagesTitles.find(d => ConfluenceClient.sameTitles(e.title, d)).isDefined)
        FutureExt.sequentially[ConfluencePageTitle, Either[ConfluenceError, ConfluencePageTitle]](pagesToRemove) { pageToRemove =>
          confluenceClient.deleteConfluencePage(pageToRemove.id.toLong).map {
            case Left(error) => {
              logger.warn(s"Unable to delete page ${pageToRemove.id.toLong} ${pageToRemove.title}: ${error.error}")
              Left(error)
            }
            case Right(_) => {
              Right(pageToRemove)
            }
          }
        }.map { results =>
          val (lefts, rights) = results.partition(_.isLeft)
          if (lefts.nonEmpty) {
            Left(lefts.head.swap.getOrElse(ConfluenceError("")))
          } else {
            if (rights.nonEmpty) {
              val pagesRemoved = rights.map(r =>  r.getOrElse(ConfluencePageTitle("","")).title).mkString("'",",","'")
              logger.info(s"Pages with titles $pagesRemoved ('${directory.path}') has been deleted.")
            }
            Right(Seq())
          }
        }
      }
    }.flatten
  }


  def createOrUpdateDirectoryPages(space: String, directory: Directory): Future[Either[ConfluenceError, Seq[ConfluencePage]]] = {
    directory.externalId match {
      case Some(parentConfluencePageId) => {
        confluenceClient.getChildrenConfluencePageTitle(parentConfluencePageId).map {
          case Left(error) => handleErrorAsFuture(error, s"Unable to get children confluence pages from the page ${parentConfluencePageId}: ${error.error}")
          case Right(existingConfluencePages) => {
            FutureExt.sequentially[Page, Either[ConfluenceError, ConfluencePage]](directory.pages) { page =>
              createOrUpdateConfluencePageWithExistingPages(parentConfluencePageId, space, existingConfluencePages, page.label, page.path, page.markdown.getOrElse(""))
            }.map { results =>
              val (lefts, rights) = results.partition(_.isLeft)
              if (lefts.nonEmpty) {
                Left(lefts.head.swap.getOrElse(ConfluenceError("")))
              } else {
                Right(rights.map(r => r.getOrElse(ConfluencePage())))
              }
            }
          }
        }.flatten
      }
      case None => {
        logger.warn(s"ConfluencePageId not provided for the directory ${directory}")
        Future.successful(Left(ConfluenceError(s"ConfluencePageId not provided for the directory ${directory}")))
      }
    }
  }

  def createOrUpdateConfluencePage(parentConfluencePageId: Long, space: String, title: String, titleContext: String, content: String): Future[Either[ConfluenceError, ConfluencePage]] = {
    confluenceClient.getChildrenConfluencePageTitle(parentConfluencePageId).map {
      case Left(error) => Future.successful(Left(error))
      case Right(existingConfluencePages) => createOrUpdateConfluencePageWithExistingPages(parentConfluencePageId, space, existingConfluencePages, title, titleContext, content)
    }.flatten
  }

  def createOrUpdateConfluencePageWithExistingPages(parentConfluencePageId: Long, space: String, existingPages: Seq[ConfluencePageTitle], title: String, titleContext: String, content: String): Future[Either[ConfluenceError, ConfluencePage]] = {
    existingPages.find(p => ConfluenceClient.sameTitles(p.title, title)) match {
      case None => {
        confluenceClient.createConfluencePage(space, parentConfluencePageId, title, titleContext, content).map {
          case Left(error) => handleError(error, s"Unable to create page with title '${title}' ('${titleContext}') under the the confluence page ${parentConfluencePageId}: ${error.error}")
          case Right(createdPage) => {
            logger.info(s"Page with title '${title}' ('${titleContext}') has been created with id ${createdPage.id} under the the confluence page ${parentConfluencePageId}.")
            Right(createdPage)
          }
        }
      }
      case Some(existingPage) => updateConfluencePage(existingPage.id.toLong, title, titleContext, content)
    }
  }

  def updateConfluencePage(confluencePageId: Long, title: String, titleContext: String, content: String): Future[Either[ConfluenceError, ConfluencePage]] = {
    confluenceClient.getConfluencePage(confluencePageId).flatMap {
      case Left(error) => Future.successful(Left(error))
      case Right(existingPage) => {
        if (ConfluenceClient.sameTitles(existingPage.title, title) && existingPage.body.editor.value == content) {
          Future.successful(Right(existingPage))
        } else {
          confluenceClient.updateConfluencePageContent(confluencePageId, title, titleContext, content, "current").map { result =>
            logger.info(s"Page with title '${title}' ('${titleContext}') has been updated on the confluence page id ${confluencePageId}.")
            result
          }
        }
      }
    }
  }

  private def handleError(error: ConfluenceError, message: String): Left[ConfluenceError, Nothing] = {
    logger.warn(message)
    Left(error)
  }

  private def handleErrorAsFuture(error: ConfluenceError, message: String): Future[Left[ConfluenceError, Nothing]] = {
    logger.warn(message)
    Future.successful(Left(error))
  }
}
