package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import services.ComponentService

class Api @Inject()(componentService: ComponentService, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def feature(project: String, feature: String) = Action {
    Ok(Json.toJson(componentService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }

}
