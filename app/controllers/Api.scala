package controllers

import javax.inject.Inject
import models.Project
import play.api.db.Database
import play.api.{Configuration, Logger, http}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}
import repository.ProjectRepository
import services.ProjectService

class Api @Inject()(implicit val db: Database, componentService: ProjectService, projectRepository: ProjectRepository, configuration: Configuration) extends InjectedController {

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
    Logger.debug(s"Project.getProject $id")
    projectRepository.getOneById(id) match {
      case Some(project) => Ok(Json.toJson(project))
      case _ => Status(http.Status.NOT_FOUND)(s"No project $id")
    }
  }

}
