package controllers

import java.io.File

import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import repository._
import services.PageService

import scala.concurrent.ExecutionContext


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

class PageAssetController @Inject()(config: Configuration, projectRepository: ProjectRepository)(implicit ec: ExecutionContext) extends InjectedController {

  val projectsRootDirectory = config.get[String]("projects.root.directory")

  def getImageFromPath(path: String): Action[AnyContent] = Action {
    val params = path.split(">")

    (for {
      projectId <- params.lift(0)
      branchName <- params.lift(1)
      relativePath <- params.lift(2)

      documentationRootPath <- projectRepository.findById(projectId).flatMap(_.documentationRootPath)
      assetFile = new File(s"$projectsRootDirectory/$projectId/$branchName/$documentationRootPath/$relativePath")

      if assetFile.exists()

    } yield Ok.sendFile(assetFile)).getOrElse(NotFound(s"Asset $path not found"))
  }
}
