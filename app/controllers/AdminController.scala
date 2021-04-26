package controllers

import controllers.dto.MessageDTO
import io.swagger.annotations._
import javax.inject.Inject
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import repositories.{BranchRepository, ProjectRepository}
import services.{MenuService, ProjectService}

import scala.concurrent.{ExecutionContext, Future}

@Api(value = "AdminController", produces = "application/json")
class AdminController @Inject()(menuService: MenuService, projectService: ProjectService, projectRepository: ProjectRepository, branchRepository: BranchRepository)(implicit ec: ExecutionContext) extends InjectedController with Logging {

  @ApiOperation(value = "Refresh menu in cache from the database")
  def refreshMenu(): Action[AnyContent] = Action {
    menuService.refreshCache()
    returnOkAndLogMessage("Cached menu refreshed from the database")
  }


  @ApiOperation(value = "Refresh a project from the database, get refreshed branches", response = classOf[String], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def refreshProjectFromDatabase(projectId: String): Action[AnyContent] = Action {
    logger.info(s"Starting refreshing project $projectId from the database")
    projectService.reloadFromDatabase(projectId) match {
      case Some(branches) =>
        menuService.refreshCache()
        returnOkAndLogMessage(s"Branches refreshed from the database linked to project $projectId are", Some(branches))
      case None => returnNotFoundAndLogMessage(s"$projectId not found while refreshing from the disk")
    }
  }

  @ApiOperation(value = "Refresh all projects from the database, get refreshed projects", response = classOf[String], responseContainer = "list")
  def refreshAllProjectsFromDatabase(): Action[AnyContent] = Action {
    logger.info("Starting refreshing all projects from the database")
    val projects = projectRepository.findAll().map { project =>
      projectService.reloadFromDatabase(project.id)
      project.id
    }
    menuService.refreshCache()
    returnOkAndLogMessage("Projects refreshed from the database are", Some(projects))
  }

  @ApiOperation(value = "Refresh a project from the disk, get refreshed branches", response = classOf[String], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def refreshProjectFromDisk(projectId: String): Action[AnyContent] = Action {
    logger.info(s"Starting refreshing project $projectId from the disk")
    projectService.reloadFromDisk(projectId) match {
      case Some(branches) =>
        menuService.refreshCache()
        returnOkAndLogMessage(s"Branches refreshed from the disk linked to project $projectId are", Some(branches))
      case None => returnNotFoundAndLogMessage(s"$projectId not found while refreshing from the disk")
    }
  }

  @ApiOperation(value = "Refresh all projects from the disk, get refreshed projects", response = classOf[String], responseContainer = "list")
  def refreshAllProjectsFromDisk(): Action[AnyContent] = Action {
    logger.info("Starting refreshing all projects from the disk")
    val projects = projectRepository.findAll().map { project =>
      projectService.reloadFromDisk(project.id)
      project.id
    }
    menuService.refreshCache()
    returnOkAndLogMessage("Projects refreshed from the disk are", Some(projects))
  }

  @ApiOperation(value = "Synchronize a project from the remote git repository, get synchronized branches", response = classOf[String], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def synchronizeProjectFromRemote(projectId: String): Action[AnyContent] = Action.async {
    logger.info(s"Starting synchronizing project $projectId")
    projectRepository.findById(projectId)
      .map {
        projectService.synchronize(_)
          .map { _ =>
            menuService.refreshCache()
            branchRepository.findAllByProjectId(projectId).map(_.name)
          }.map(branches => returnOkAndLogMessage(s"Branches synchronized from the remote git repository linked to project $projectId are", Some(branches)))

      }
      .getOrElse(Future.successful(returnNotFoundAndLogMessage(s"$projectId not found while synchronizing from the remote git repository")))
  }

  @ApiOperation(value = "Synchronize all projects with the same git repository, get synchronized branches", response = classOf[String], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def synchronizeProjectFromRemoteWithSameGitRepository(projectId: String): Action[AnyContent] = Action.async {
    logger.info(s"Starting synchronizing project $projectId and all other projects with the same git repository")
    val projectWithSameGitRepo = projectRepository.findById(projectId).
      map { project =>
        projectRepository.findAllByGitRepository(project.repositoryUrl)
      }
    projectWithSameGitRepo.map { projectWithSameGitRepo =>
      projectService.synchronizeProjects(projectWithSameGitRepo)
        .map { _ =>
          val projectIdsWithSameGitRepo = projectWithSameGitRepo.map(p=>p.id)
          returnOkAndLogMessage(s"Projects synchronized from the remote git repository linked to project $projectId are", Some(projectIdsWithSameGitRepo))
        }
    }.getOrElse(Future.successful(returnNotFoundAndLogMessage(s"$projectId not found while synchronizing from the remote git repository")))
  }

  @ApiOperation(value = "Refresh projects from the remote git repository")
  def refreshProjectFromRemote(projectId: String): Action[AnyContent] = Action.async {
    if (projectService.isGlobalSynchroOnGoing()) {
      Future.successful(ServiceUnavailable("Global synchro on going"))
    } else {
      logger.info(s"Starting refreshing project $projectId from the remote git repository")
      projectService.reloadFromRemote(projectId) match {
        case Some(project) =>
          project.map { branches =>
            menuService.refreshCache()
            returnOkAndLogMessage(s"Branches refreshed from the remote git repository linked to project $projectId are", Some(branches))
          }
        case None => Future.successful(returnNotFoundAndLogMessage(s"$projectId not found while refreshing from the remote git repository"))
      }
    }
  }

  private def returnOkAndLogMessage(message: String, elements: Option[Seq[String]] = None): Result = {
    val messageToLog = elements.map { e => s"$message ${e.mkString(",")}" }.getOrElse(message)
    logger.info(messageToLog)
    Ok(Json.toJson(MessageDTO(message, elements)))
  }

  private def returnNotFoundAndLogMessage(message: String): Result = {
    logger.info(message)
    NotFound(Json.toJson(MessageDTO(message)))
  }
}
