package controllers

import javax.inject.Inject
import models.Project
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}
import repository.ProjectRepository
import services.FeatureService

class Api @Inject()(componentService: FeatureService, projectRepository: ProjectRepository, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def feature(project: String, feature: String) = Action {
    Ok(Json.toJson(componentService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }


  def registerProject() = Action { implicit request =>
    val project = request.body.asJson.map(_.as[Project])
    project match {
      case Some(p) => projectRepository.insertOne(p)
        Created(Json.toJson(projectRepository.getOneById(p.id)))
      case _ => BadRequest
    }

  }

  def getProject(id: String): Action[AnyContent] = Action {
    projectRepository.getOneById(id) match {
      case Some(project) => Ok(Json.toJson(project))
      case _ => NotFound(s"No project $id")
    }
  }

  def updateProject(id: String) = Action { implicit request =>
    val project = request.body.asJson.map(_.as[Project])
    project match {
      case Some(p) => require(id == p.id)

        if (projectRepository.getOneById(id).isEmpty) NotFound(s"No project $id")

        projectRepository.update(p)
        Created(Json.toJson(projectRepository.getOneById(id)))
      case _ => BadRequest
    }
  }


}
