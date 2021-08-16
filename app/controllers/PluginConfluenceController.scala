package controllers

import controllers.dto.MessageDTO
import io.swagger.annotations._
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import services.ConfluenceService
import services.clients.ConfluenceError

import javax.inject.Inject
import scala.concurrent.ExecutionContext

@Api(value = "PluginConfluenceController", produces = "application/json")
class PluginConfluenceController @Inject()(confluenceService: ConfluenceService)(implicit ec: ExecutionContext) extends InjectedController with Logging {

  @ApiOperation(value = "Refresh a project pages in Confluence", response = classOf[String], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def refreshProjectInConfluence(projectId: String): Action[AnyContent] = Action.async {
    logger.info(s"Starting refreshing project $projectId in Confluence")
    confluenceService.refreshProjectInConfluence(projectId).map {
      case Left(error) => error.`type` match {
        case ConfluenceError.PROJECT_NOT_FOUND => NotFound(Json.toJson(MessageDTO(error.error)))
        case ConfluenceError.STABLE_BRANCH_NOT_FOUND => NotFound(Json.toJson(MessageDTO(error.error)))
        case ConfluenceError.ROOT_DIRECTORY_NOT_FOUND => NotFound(Json.toJson(MessageDTO(error.error)))
        case _ => InternalServerError(Json.toJson(MessageDTO(error.error)))
      }
      case Right(_) => {
        val message = s"Project $projectId has been refreshed"
        logger.info(message)
        Ok(Json.toJson(MessageDTO(message)))
      }
    }
  }

}
