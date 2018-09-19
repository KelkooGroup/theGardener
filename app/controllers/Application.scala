package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.mvc._
import repository._
import services.FeatureService
import views._

class Application @Inject()(featureService: FeatureService, projectRepository: ProjectRepository, branchRepository: BranchRepository, configuration: Configuration) extends InjectedController {

  val projectsRootDirectory = configuration.get[String]("projects.root.directory")

  def index = Action {
    Ok(html.index("Hello, the Gardener"))
  }

  def feature(project: String, feature: String) = Action {
    val projectName = projectRepository.findById(project).map(_.name).getOrElse(project)
    val branch = branchRepository.findByProjectIdAndName(project, "master").get
    Ok(html.feature(projectName, branch, featureService.parseFeatureFile(project, branch.id, s"$projectsRootDirectory/$project/master/test/features/$feature")))
  }
}
