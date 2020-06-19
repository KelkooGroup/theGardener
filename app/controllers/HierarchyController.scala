package controllers


import io.swagger.annotations._
import javax.inject._
import models._
import play.api.libs.json._
import play.api.mvc._
import services.{HierarchyService, MenuService}

@Api(value = "HierarchyController", produces = "application/json")
class HierarchyController @Inject()(hierarchyService: HierarchyService, criteriaService: MenuService) extends InjectedController {

  implicit val hierarchyFormat = Json.format[HierarchyNode]

  @ApiOperation(value = "Add a  new Hierarchy", code = 201, response = classOf[HierarchyNode])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The hierarchy to add", required = true, dataType = "models.HierarchyNode", paramType = "body")))
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json")))
  def addHierarchy(): Action[HierarchyNode] = Action(parse.json[HierarchyNode]) { implicit request =>
    val hierarchy = request.body

    if (hierarchyService.existsById(hierarchy.id) || ! hierarchyService.wellFormedId(hierarchy.id) || ! hierarchyService.wellFormedShortcut(hierarchy)) {
      BadRequest

    } else {
      val addHierarchy = hierarchyService.save(hierarchy)

      criteriaService.refreshCache()

      Created(Json.toJson(addHierarchy))
    }
  }

  @ApiOperation(value = "Get all hierarchies", response = classOf[HierarchyNode])
  def getAllHierarchies(): Action[AnyContent] = Action {
    Ok(Json.toJson(hierarchyService.findAll()))
  }

  @ApiOperation(value = "Update an hierarchy", response = classOf[HierarchyNode])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The hierarchy to update", required = true, dataType = "models.HierarchyNode", paramType = "body")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Hierarchy not found"))
  )
  def updateHierarchy(@ApiParam("Hierarchy id") id: String): Action[HierarchyNode] = Action(parse.json[HierarchyNode]) { implicit request =>
    val hierarchy = request.body

    if (id != hierarchy.id || ! hierarchyService.wellFormedShortcut(hierarchy)) {
      BadRequest

    } else {
      if (hierarchyService.existsById(id)) {
        hierarchyService.save(hierarchy)

        criteriaService.refreshCache()

        Ok(Json.toJson(hierarchyService.findById(id)))

      } else {
        NotFound(s"No hierarchy $id")
      }
    }
  }

  @ApiOperation(value = "Delete an hierarchy")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Hierarchy not found")))
  def deleteHierarchy(@ApiParam("Hierarchy id") id: String): Action[AnyContent] = Action {

    if (hierarchyService.existsById(id)) {
      hierarchyService.deleteById(id)

      criteriaService.refreshCache()

      Ok

    } else {
      NotFound(s"No hierarchy $id")
    }
  }
}
