package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.mvc._
import repository.ProjectRepository
import services.FeatureService
import views._

class Application @Inject()(featureService: FeatureService, projectRepository: ProjectRepository, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def index = Action {
    Ok(html.index("Hello, the Gardener"))
  }

  def feature(project: String, feature: String) = Action {
    val projectName = projectRepository.getOneById(project).map(_.name).getOrElse(project)

    Ok(html.feature(projectName, featureService.parseFeatureFile(project, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }

}
