package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc._
import repository._

case class BranchDocumentationDTO(id: Long, name: String, isStable: Boolean, features: Seq[Feature])

object BranchDocumentationDTO {
  def apply(branch: Branch,  features: Seq[Feature]): BranchDocumentationDTO = {
    BranchDocumentationDTO(branch.id, branch.name, branch.isStable, features)
  }
}

case class ProjectDocumentationDTO(id: String, name:String, branches: Seq[BranchDocumentationDTO])

object ProjectDocumentationDTO {
  def apply(project: Project, branches: Seq[BranchDocumentationDTO]): ProjectDocumentationDTO = {
    ProjectDocumentationDTO(project.id, project.name, branches)
  }
}

case class Documentation(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectDocumentationDTO], children: Seq[Documentation])
object Documentation {
  def apply(hierarchyNode: HierarchyNode, projects: Seq[ProjectDocumentationDTO], children: Seq[Documentation]): Documentation = {
    Documentation(hierarchyNode.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, projects, children)
  }
}



@Api(value = "GenerateDocumentationController", produces = "application/json")
class GenerateDocumentationController @Inject()(hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository, branchRepository: BranchRepository, featureRepository: FeatureRepository) extends InjectedController {

  implicit val branchFormat = Json.format[BranchDocumentationDTO]
  implicit val projectFormat = Json.format[ProjectDocumentationDTO]
  implicit val documentationFormat = Json.format[Documentation]

  @ApiOperation(value = "Get documentation", response = classOf[Documentation])
  def generateDocumentation(): Action[AnyContent] = Action { request =>

    val nodes = request.queryString.getOrElse("node", Seq()).map(_.split("_").toSeq.filter(_.isEmpty))
    val projects = request.queryString.getOrElse("project", Seq()).map { p =>
      val hierarchy = p.substring(0, p.indexOf(">")).split("_").toSeq.filterNot(_.isEmpty).flatMap(hierarchyRepository.findBySlugName)
      val projectId = p.substring(p.indexOf(">") + 1)

      val branches = branchRepository.findAllByProjectId(projectId).map(b => BranchDocumentationDTO(b, featureRepository.findAllByBranchId(b.id)))
      val project = projectRepository.findAllById(Seq(projectId)).map(ProjectDocumentationDTO(_, branches))

      buildDocumentation(hierarchy, project)
    }

    Ok(Json.toJson(projects.head))
  }

  def buildDocumentation(hierarchy: Seq[HierarchyNode], projects: Seq[ProjectDocumentationDTO]) : Documentation = {
    hierarchy.toList match {
      case Nil =>  Documentation(hierarchyRepository.findAll().head, projects, Seq())
      case h :: Nil => Documentation(h, projects, Seq())
      case h :: tail => Documentation(h, Seq(), Seq(buildDocumentation(tail, projects)))
    }

  }

}
