package controllers

import javax.inject.Inject
import models.Feature
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._
import services.ComponentService
import views._

class Application @Inject()(componentService: ComponentService, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def index = Action {
    Ok(html.index("Hello, the Gardener"))
  }

  def feature(project: String, feature: String) = Action {


    Ok(html.feature(project, componentService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }

}
