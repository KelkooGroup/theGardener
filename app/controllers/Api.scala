package controllers

import javax.inject.Inject
import models.Project
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import repository.ProjectRepository
import services.ComponentService

class Api @Inject()(componentService: ComponentService, projectRepository: ProjectRepository, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def feature(project: String, feature: String) = Action {
    Ok(Json.toJson(componentService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }

  def project() = Action { implicit request =>

    val project = request.body.asJson.map(_.as[Project])

    project match {
      case Some(p) =>
        projectRepository.insertOne(p)

        Created(Json.toJson(projectRepository.getOneById(p.id)))
      case _ => BadRequest
    }

  }
}
