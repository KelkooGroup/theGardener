package controllers

import java.io.File

import controllers.AssetAccessError.{AssetNotAllowed, AssetNotFound}
import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import repositories._
import services._

import scala.concurrent.ExecutionContext

@Api(value = "PageController", produces = "application/json")
class PageController @Inject()(pageService: PageService, searchService: SearchService)(implicit ec: ExecutionContext) extends InjectedController {


  @ApiOperation(value = "Get pages from path", response = classOf[PageDTO], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Page not found")))
  def getPageFromPath(path: String): Action[AnyContent] = Action.async {
    pageService.computePageFromPath(path).map {
      case Some(pageDto) => Ok(Json.toJson(Seq(pageDto)))
      case None => NotFound(s"No Page $path")
    }
  }

  @ApiOperation(value = "Search pages through a keyword", response = classOf[SearchResult])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Page not found")))
  def searchPage(keyword: String): Action[AnyContent] = Action {
    Ok(Json.toJson(searchService.searchForPage(keyword)))
  }

}

sealed abstract class AssetAccessError(message: String) extends Throwable(message)


object AssetAccessError {

  case class AssetNotAllowed(message: String) extends AssetAccessError(message)

  case class AssetNotFound(message: String) extends AssetAccessError(message)

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
      assetFileAccess = accessToAsset(s"$projectsRootDirectory/$projectId/$branchName/$documentationRootPath", relativePath)
    } yield (relativePath, assetFileAccess)) match {

      case None => NotFound("Project not found or bad configuration")
      case Some((_, Left(AssetNotAllowed(message)))) => Forbidden(message)
      case Some((_, Left(AssetNotFound(message)))) => NotFound(message)
      case Some((_, Right(assetFile))) => Ok.sendFile(assetFile)
    }
  }

  def accessToAsset(documentationRootPath: String, assetRelativePath: String): Either[AssetAccessError, File] = {
    val assetFile = new File(s"$documentationRootPath/$assetRelativePath")
    val documentationCanonicalPath = new File(documentationRootPath).getCanonicalPath
    val assetCanonicalPath = assetFile.getCanonicalPath

    if (!assetCanonicalPath.contains(documentationCanonicalPath)) {
      Left(AssetNotAllowed(s"Asset $assetRelativePath not allowed"))
    } else if (!assetFile.exists()) {
      Left(AssetNotFound(s"Asset $assetRelativePath not found"))
    } else {
      Right(assetFile)
    }
  }

}
