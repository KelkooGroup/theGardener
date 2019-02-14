package controllers


import io.swagger.annotations._
import javax.inject._
import models._
import play.api.libs.json._
import play.api.mvc._
import repository._
import services.CriteriaService

@Api(value = "HierarchyController", produces = "application/json")
class HierarchyController @Inject()(hierarchyRepository: HierarchyRepository, criteriaService: CriteriaService) extends InjectedController {

  implicit val hierarchyFormat = Json.format[HierarchyNode]

  @ApiOperation(value = "Add a  new Hierarchy", code = 201, response = classOf[HierarchyNode])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The hierarchy to add", required = true, dataType = "models.HierarchyNode", paramType = "body")))
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json")))
  def addHierarchy(): Action[HierarchyNode] = Action(parse.json[HierarchyNode]) { implicit request =>
    val hierarchy = request.body

    if (hierarchyRepository.existsById(hierarchy.id)) {
      BadRequest

    } else {
      val addHierarchy = hierarchyRepository.save(hierarchy)

      criteriaService.refreshCache()

      Created(Json.toJson(addHierarchy))
    }
  }

  @ApiOperation(value = "Get all hierarchies", response = classOf[HierarchyNode])
  def getAllHierarchies(): Action[AnyContent] = Action {
    Ok(Json.toJson(hierarchyRepository.findAll()))
  }

  @ApiOperation(value = "Update an hierarchy", response = classOf[HierarchyNode])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The hierarchy to update", required = true, dataType = "models.HierarchyNode", paramType = "body")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Hierarchy not found"))
  )
  def updateHierarchy(@ApiParam("Hierarchy id") id: String): Action[HierarchyNode] = Action(parse.json[HierarchyNode]) { implicit request =>
    val hierarchy = request.body

    if (id != hierarchy.id) {
      BadRequest

    } else {
      if (hierarchyRepository.existsById(id)) {
        hierarchyRepository.save(hierarchy)

        criteriaService.refreshCache()

        Ok(Json.toJson(hierarchyRepository.findById(id)))

      } else {
        NotFound(s"No hierarchy $id")
      }
    }
  }

  @ApiOperation(value = "Delete an hierarchy")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Hierarchy not found")))
  def deleteHierarchy(@ApiParam("Hierarchy id") id: String): Action[AnyContent] = Action {

    if (hierarchyRepository.existsById(id)) {
      hierarchyRepository.deleteById(id)

      criteriaService.refreshCache()

      Ok

    } else {
      NotFound(s"No hierarchy $id")
    }
  }
}
