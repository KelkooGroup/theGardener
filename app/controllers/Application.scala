package controllers

import controllers.Assets.Asset
import javax.inject.Inject
import play.api.Configuration
import play.api.mvc._
import repository.ProjectRepository
import services.FeatureService
import views._

class Application @Inject()(featureService: FeatureService, projectRepository: ProjectRepository, configuration: Configuration, assets: Assets) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def index = Action {
    Ok(html.index("Hello, the Gardener"))
  }

  def app(path: String): Action[AnyContent] = assets.at("dist/index.html")

  def assets(file: Asset): Action[AnyContent] = assets.versioned(path = "/public", file)
  // Workaround for the images because url of images in html files are not rewritten (prefixed) by Angular
  def imageAssets(file: Asset): Action[AnyContent] = assets.versioned(path = "/public/dist/assets/images", file)



}
