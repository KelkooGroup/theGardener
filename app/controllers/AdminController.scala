package controllers

import io.swagger.annotations._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import repositories.ProjectRepository
import services.{MenuService, ProjectService}

import scala.concurrent.{ExecutionContext, Future}

@Api(value = "AdminController", produces = "application/json")
class AdminController @Inject()(menuService: MenuService, projectService: ProjectService, projectRepository: ProjectRepository)(implicit ec: ExecutionContext) extends InjectedController {

  @ApiOperation(value = "Refresh menu in cache from the database")
  def refreshMenu(): Action[AnyContent] = Action {
    menuService.refreshCache()
    Ok
  }

  @ApiOperation(value = "Refresh a project from the disk, get refreshed branches", response = classOf[String], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def refreshProjectFromDisk(projectId: String): Action[AnyContent] = Action {
    projectService.reloadFromDisk(projectId) match {
      case Some(branches) => {
        menuService.refreshCache()
        Ok(Json.toJson(branches))
      }
      case None => NotFound
    }
  }

  @ApiOperation(value = "Refresh all projects from the disk, get refreshed projects", response = classOf[String], responseContainer = "list")
  def refreshAllProjectsFromDisk(): Action[AnyContent] = Action {
    val projects = projectRepository.findAll().map { project =>
      projectService.reloadFromDisk(project.id)
      project.id
    }
    menuService.refreshCache()
    Ok(Json.toJson(projects))
  }

  @ApiOperation(value = "Refresh projects from the remote git repository")
  def refreshProjectsFromRemote(projectId: String): Action[AnyContent] = Action.async {
    projectService.reloadFromRemote(projectId) match {
      case Some(project) => {
        project.map { branches =>
          menuService.refreshCache()
          Ok(Json.toJson(branches))
        }
      }
      case None => Future.successful(NotFound)
    }
  }

  @ApiOperation(value = "Refresh all projects from the remote git repository")
  def refreshAllProjectsFromRemote(): Action[AnyContent] = Action {
    val projects = projectRepository.findAll().map { project =>
      projectService.reloadFromRemote(project.id)
      project.id
    }
    menuService.refreshCache()
    Ok(Json.toJson(projects))
  }
}
