package controllers


import io.swagger.annotations._
import javax.inject.Inject
import julienrf.json.derived
import models._
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import repository.ProjectRepository
import services.FeatureService

@Api(value = "FeatureController", produces = "application/json")
class FeatureController @Inject()(featureService: FeatureService, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  implicit val stepFormat = Json.format[Step]
  implicit val examplesFormat = Json.format[Examples]
  implicit val scenarioFormat = derived.flat.oformat[ScenarioDefinition]((__ \ "keyword").format[String])
  implicit val featureFormat = Json.format[Feature]

  @ApiOperation(value = "Get a feature", response = classOf[Feature])
  def feature(@ApiParam("Project id") project: String, @ApiParam("Feature file name") feature: String) = Action {
    Ok(Json.toJson(featureService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }
}

@Api(value = "ProjectController", produces = "application/json")
class ProjectController @Inject()(projectRepository: ProjectRepository) extends InjectedController {

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
      Created(Json.toJson(savedProject))
    }
  }

  @ApiOperation(value = "Get all projects", response = classOf[Project])
  def getAllProjects(): Action[AnyContent] = Action {
    Ok(Json.toJson(projectRepository.findAll()))
  }

  @ApiOperation(value = "Get a project", response = classOf[Project])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def getProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action {

    projectRepository.findById(id) match {
      case Some(project) => Ok(Json.toJson(project))

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

        Ok(Json.toJson(projectRepository.findById(id)))

      } else {
        NotFound(s"No project $id")
      }
    }
  }

  @ApiOperation(value = "Delete a project")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def deleteProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action {

    if (projectRepository.existsById(id)) {
      projectRepository.deleteById(id)

      Ok

    } else {
      NotFound(s"No project $id")
    }
  }
}
