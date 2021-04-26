package controllers

import scala.annotation.nowarn
import controllers.Assets.Asset
import javax.inject.Inject
import play.api.mvc._

class Application @Inject()(assets: Assets) extends InjectedController {

  def index: Action[AnyContent] = Action {
    Redirect("/app/")
  }

  @nowarn("cat=unused-params")
  def app(path: String): Action[AnyContent] = assets.at("dist/index.html")

  def assets(file: Asset): Action[AnyContent] = assets.versioned(path = "/public", file)

  // Workaround for the images because url of images in html files are not rewritten (prefixed) by Angular
  def imageAssets(file: Asset): Action[AnyContent] = assets.versioned(path = "/public/dist/assets/images", file)

}
