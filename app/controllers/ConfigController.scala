package controllers

import io.swagger.annotations.{Api, ApiOperation}
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json._
import play.api.mvc._

@Api(value = "Angular Configuration", produces = "application/json")
class ConfigController @Inject()(config: Configuration) extends InjectedController {

  val Config = JsObject(Seq(
    "windowTitle" -> JsString(config.get[String]("application.windowTitle")),
    "title" -> JsString(config.get[String]("application.title")),
    "logoSrc" -> JsString(config.get[String]("application.logoSrc")),
    "faviconSrc" -> JsString(config.get[String]("application.faviconSrc")),
    "colorMain" -> JsString(config.get[String]("color.main")),
    "baseUrl" -> JsString(config.get[String]("application.baseUrl"))
  ))

  @ApiOperation(value = "Get configuration", response = classOf[Map[String, String]])
  def getConfig(): Action[AnyContent] = Action {
    Ok(Config)
  }

}

