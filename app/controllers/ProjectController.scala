package controllers


import io.swagger.annotations._
import javax.inject._
import models._
import play.api.libs.json._
import play.api.mvc._
import repositories._
import services._

import scala.concurrent._

@Api(value = "ProjectController", produces = "application/json")
class ProjectController @Inject()(projectRepository: ProjectRepository, projectService: ProjectService, hierarchyRepository: HierarchyRepository,
                                  branchRepository: BranchRepository, menuService: MenuService, replicaService: ReplicaService)
                                 (implicit ec: ExecutionContext) extends InjectedController {

  implicit val pageFormat = Json.format[Page]
  implicit val directoryFormat = Json.format[Directory]
  implicit val branchFormat = Json.format[Branch]
  implicit val hierarchyFormat = Json.format[HierarchyNode]
  implicit val variableFomrat = Json.format[Variable]
  implicit val projectFormat = Json.format[Project]

  @ApiOperation(value = "Register a new project", code = 201, response = classOf[Project])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The project to register", required = true, dataType = "models.Project", paramType = "body")))
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json")))
  def registerProject(): Action[Project] = Action(parse.json[Project]) { implicit request =>
    val project = request.body

    if (projectRepository.existsById(project.id)) {
      BadRequest

    } else {
      val savedProject = projectRepository.save(project)

      projectService.checkoutRemoteBranches(savedProject)

      menuService.refreshCache()

      Created(Json.toJson(savedProject))
    }
  }

  @ApiOperation(value = "Get all projects", response = classOf[Project], responseContainer = "list")
  def getAllProjects(): Action[AnyContent] = Action {
    Ok(Json.toJson(projectRepository.findAll()))
  }

  @ApiOperation(value = "Get a project", response = classOf[Project])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def getProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action {
    projectRepository.findById(id) match {

      case Some(project) =>
        val hierarchy = hierarchyRepository.findAllByProjectId(project.id)

        Ok(Json.toJson(project.copy(hierarchy = if (hierarchy.nonEmpty) Some(hierarchy) else None)))

      case _ => NotFound(s"No project $id")
    }
  }

  @ApiOperation(value = "Get variables from a project", response = classOf[Seq[Variable]])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def getVariables(@ApiParam("Project id") id: String): Action[AnyContent] = Action {
    projectRepository.findById(id) match {

      case Some(project) =>

        Ok(Json.toJson(project.variables))

      case _ => NotFound(s"No project $id")
    }
  }

  @ApiOperation(value = "Update variables of a project", response = classOf[Project])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The variables to update", required = true, dataType = "models.Variable", collectionFormat = "list",paramType = "body")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Project not found"))
  )
  def updateVariables(@ApiParam("Project id") id: String): Action[Seq[Variable]] = Action(parse.json[scala.Seq[models.Variable]]) { implicit request =>
    val variables = request.body

    projectRepository.findById(id) match {

      case Some(project) =>
       val newVariables = project.variables.getOrElse(Seq()) ++ variables

        projectRepository.save(project.copy(variables = if (newVariables.nonEmpty) Some(newVariables) else None))

        menuService.refreshCache()

        Ok(Json.toJson(projectRepository.findById(id)))

      case _ => NotFound(s"No project $id")
    }

  }

  @ApiOperation(value = "Update a project", response = classOf[Project])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The project to update", required = true, dataType = "models.Project", paramType = "body")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Project not found"))
  )
  def updateProject(@ApiParam("Project id") id: String): Action[Project] = Action(parse.json[Project]) { implicit request =>
    val project = request.body

    if (id != project.id) {
      BadRequest

    } else {
      if (projectRepository.existsById(id)) {
        projectRepository.save(project)

        menuService.refreshCache()

        Ok(Json.toJson(projectRepository.findById(id)))

      } else {
        NotFound(s"No project $id")
      }
    }
  }

  @ApiOperation(value = "Delete a project")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def deleteProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action {

    projectService.deleteBranches(id, branchRepository.findAllByProjectId(id).map(_.name).toSet)

    if (projectRepository.existsById(id)) {

      projectRepository.deleteById(id)

      menuService.refreshCache()

      Ok

    } else {
      NotFound(s"No project $id")
    }
  }

  @ApiOperation(value = "Link a Project to hierarchy", code = 201, response = classOf[HierarchyNode])
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json"), new ApiResponse(code = 404, message = "Project or hierarchy not found")))
  def linkProjectToHierarchy(@ApiParam("Project Id") id: String, @ApiParam("Hierarchy Id") hierarchyId: String): Action[AnyContent] = Action {
    if (projectRepository.existsById(id)) {
      if (hierarchyRepository.existsById(hierarchyId)) {
        projectRepository.linkHierarchy(id, hierarchyId)
        val hierarchy = hierarchyRepository.findAllByProjectId(id)

        menuService.refreshCache()

        Created(Json.toJson(hierarchy))

      } else {
        NotFound(s"No hierarchy node $hierarchyId")
      }
    } else {
      NotFound(s"No project $id")
    }
  }

  @ApiOperation(value = "Delete a link hierarchy to a project")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Link hierarchy project not found")))
  def deleteLinkProjectToHierarchy(@ApiParam("Project id") id: String, @ApiParam("Hierarchy Id") hierarchyId: String): Action[AnyContent] = Action {

    if (projectRepository.existsLinkByIds(id, hierarchyId)) {
      projectRepository.unlinkHierarchy(id, hierarchyId)

      menuService.refreshCache()

      Ok(Json.toJson(hierarchyRepository.findAllByProjectId(id)))

    } else {
      NotFound(s"No link hierarchy $hierarchyId to a project $id")
    }
  }

  @ApiOperation(value = "Webhook to synchronize a new project")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def synchronizeProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action.async {
    projectRepository.findById(id)
      .map(projectService.synchronize(_).map(_ => menuService.refreshCache()).map(_ => replicaService.triggerSychronizeOnReplica(id)).map(_ => Ok))
      .getOrElse(Future.successful(NotFound(s"No project $id")))
  }

  @ApiOperation(value = "get the hierarchy link to a project", response = classOf[HierarchyNode])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def getLinkProjectToHierarchy(@ApiParam("project Id") id: String): Action[AnyContent] = Action {

    if (projectRepository.existsById(id)) {
      Ok(Json.toJson(hierarchyRepository.findAllByProjectId(id)))

    } else {
      NotFound(s"No project $id")
    }
  }
}
