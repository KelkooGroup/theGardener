package controllers

import javax.inject.Inject

import io.swagger.annotations._
import models._
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import repository.ProjectRepository
import services.FeatureService

@Api(value = "Feature Service", produces = "application/json")
class FeatureController @Inject()(featureService: FeatureService, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  @ApiOperation(value = "Get a feature", response = classOf[Feature])
  def feature(@ApiParam("Project id") project: String, @ApiParam("Feature file name") feature: String) = Action {
    Ok(Json.toJson(featureService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }
}

@Api(value = "Project Service", produces = "application/json")
class ProjectController @Inject()(projectRepository: ProjectRepository) extends InjectedController {

  @ApiOperation(value = "Register a new project", code = 201, response = classOf[Project])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The project to register", required = true, dataType = "models.Project", paramType = "body")))
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json")))
  def registerProject() = Action { implicit request =>

    request.body.asJson.map(_.as[Project]) match {
      case Some(project) => require(!projectRepository.existsById(project.id))

        val savedProject = projectRepository.save(project)
        Created(Json.toJson(savedProject))

      case _ => BadRequest
    }
  }

  @ApiOperation(value = "Get all projects", response = classOf[Project])
  def getAllProjects() = Action {
    Ok(Json.toJson(projectRepository.findAll()))
  }

  @ApiOperation(value = "Get a project", response = classOf[Project])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def getProject(@ApiParam("Project id") id: String) = Action {

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
  def updateProject(@ApiParam("Project id") id: String) = Action { implicit request =>

    request.body.asJson.map(_.as[Project]) match {
      case Some(project) => require(id == project.id)

        if (projectRepository.findById(id).isDefined) {
          projectRepository.save(project)

          Ok(Json.toJson(projectRepository.findById(id)))

        } else {
          NotFound(s"No project $id")
        }

      case _ => BadRequest
    }
  }

}
