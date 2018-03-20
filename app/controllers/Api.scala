package controllers

import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.InjectedController
import services.ComponentService

class Api @Inject()(componentService: ComponentService) extends InjectedController {

  def feature(project: String, feature: String) = Action {


    Ok(Json.toJson(componentService.parseFeatureFile("test", "target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature")))
  }

}
