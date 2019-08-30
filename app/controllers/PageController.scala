package controllers

import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import repository.PageRepository
import services.PageService


@Api(value = "PageController", produces = "application/json")
class PageController @Inject()(pageRepository: PageRepository, pageService: PageService) extends InjectedController {

  @ApiOperation(value = "Get pages from path", response = classOf[PageDTO], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Page not found")))
  def getPageFromPath(path: String): Action[AnyContent] = Action {
    pageRepository.findByPath(path) match {
      case Some(page) => Ok(Json.toJson(Seq(PageDTO(page.copy(markdown = page.markdown.map(content => pageService.findPageMeta(content).map(meta => content.replace(s"""```thegardener$meta```""", "").trim).getOrElse(content)))))))
      case _ => NotFound(s"No Page $path")
    }
  }
}
