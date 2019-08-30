package controllers

import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import models.Variable
import play.api.libs.json.Json
import play.api.mvc._
import repository._
import services._


@Api(value = "PageController", produces = "application/json")
class PageController @Inject()(pageRepository: PageRepository, projectService: ProjectService, pageservice : PageService) extends InjectedController {

  @ApiOperation(value = "Get pages from path", response = classOf[PageDTO], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Page not found")))
  def getPageFromPath(path: String): Action[AnyContent] = Action {
    pageRepository.findByPath(path) match {
      case Some(page) =>
        val variables = projectService.getVariables(page)
        Ok(Json.toJson(Seq(PageDTO(pageservice.replaceVariablesInMarkdown(page,variables)))))
      case _ => NotFound(s"No Page $path")
    }
  }
}
