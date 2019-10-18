package controllers

import controllers.dto.DirectoryDTO
import io.swagger.annotations._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import repositories._


@Api(value = "DirectoryController", produces = "application/json")
class DirectoryController @Inject()(branchRepository: BranchRepository, directoryRepository: DirectoryRepository, pageRepository: PageRepository) extends InjectedController {

  @ApiOperation(value = "Get directory from path", response = classOf[DirectoryDTO], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Directory not found")))
  def getDirectoryFromPath(path: String): Action[AnyContent] = Action {
    val params = path.split(">")

    (for {
      projectId <- params.lift(0)
      branchName <- params.lift(1)
      relativePath <- params.lift(2)

      branchId <- branchRepository.findByProjectIdAndName(projectId, branchName).map(_.id)
      directory <- directoryRepository.findByBranchIdAndRelativePath(branchId, relativePath)
      pagesPaths = pageRepository.findAllByDirectoryId(directory.id)

    } yield directory.copy(pages = pagesPaths)) match {

      case Some(directory) => Ok(Json.toJson(Seq(DirectoryDTO(directory))))
      case _ => NotFound(s"No Directory $path")
    }
  }
}
