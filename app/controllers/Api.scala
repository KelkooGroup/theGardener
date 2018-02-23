package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import services.ComponentService

class Api @Inject()(componentService: ComponentService) extends InjectedController {

  def feature(project: String, feature: String) = Action {


    Ok(Json.toJson(componentService.parseFeatureFile("test", "test/features/show_features/show_a_feature.feature")))
  }

}
