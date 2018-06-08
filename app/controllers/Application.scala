package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.mvc._
import services.ProjectService
import views._

class Application @Inject()(componentService: ProjectService, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def index = Action {
    Ok(html.index("Hello, the Gardener"))
  }

  def feature(project: String, feature: String) = Action {
    val projectName = componentService.projects.get(project).map(_.name).getOrElse(project)

    Ok(html.feature(projectName, componentService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }

}
