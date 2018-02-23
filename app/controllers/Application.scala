package controllers

import javax.inject.Inject
import models.Feature
import play.api.libs.json._
import play.api.mvc._
import services.ComponentService
import views._

class Application @Inject()(componentService: ComponentService) extends InjectedController {

  def index = Action {
    Ok(html.index("Hello, the Gardener"))
  }

  def feature(project: String, feature: String) = Action {


    Ok(html.feature(project, componentService.parseFeatureFile(project, s"target/data/git/$project/master/test/features/$feature")))
  }

}
