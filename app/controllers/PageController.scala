package controllers

import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import repository.PageRepository


@Api(value = "PageController", produces = "application/json")
class PageController @Inject()(pageRepository: PageRepository) extends InjectedController {

  @ApiOperation(value = "Get pages from path", response = classOf[PageDTO])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Page not found")))
  def getPagesFromPath(path: String): Action[AnyContent] = Action {
    pageRepository.findByPath(path) match {
      case Some(page) => Ok(Json.toJson(PageDTO(page)))
      case _ => NotFound(s"No Page $path")
    }
  }
}
