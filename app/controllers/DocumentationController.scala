package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc._
import repository._

case class BranchDocumentationDTO(id: Long, name: String, isStable: Boolean, features: Seq[Feature]) {
  def merge(other: BranchDocumentationDTO): BranchDocumentationDTO = {
    if (id == other.id) this.copy(features = (features ++ other.features).distinct) else this
  }
}

object BranchDocumentationDTO {
  def apply(branch: Branch, features: Seq[Feature]): BranchDocumentationDTO = {
    BranchDocumentationDTO(branch.id, branch.name, branch.isStable, features)
  }
}

case class ProjectDocumentationDTO(id: String, name: String, branches: Seq[BranchDocumentationDTO]) {
  def merge(other: ProjectDocumentationDTO): ProjectDocumentationDTO = {
    if (id == other.id) this.copy(branches = branches.flatMap(b => other.branches.map(_.merge(b))).distinct) else this
  }
}

object ProjectDocumentationDTO {
  def apply(project: Project, branches: Seq[BranchDocumentationDTO]): ProjectDocumentationDTO = {
    ProjectDocumentationDTO(project.id, project.name, branches)
  }
}

case class Documentation(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectDocumentationDTO], children: Seq[Documentation]) {

  def merge(other: Documentation): Documentation = {
    if (id == other.id) this.copy(projects = (projects ++ other.projects).distinct, children = children.flatMap(c => other.children.map(_.merge(c))).distinct) else this
  }
}

object Documentation {
  def apply(hierarchyNode: HierarchyNode, projects: Seq[ProjectDocumentationDTO], children: Seq[Documentation]): Documentation = {
    Documentation(hierarchyNode.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, projects, children)
  }
}


@Api(value = "DocumentationController", produces = "application/json")
class DocumentationController @Inject()(hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository, branchRepository: BranchRepository, featureRepository: FeatureRepository) extends InjectedController {

  implicit val branchFormat = Json.format[BranchDocumentationDTO]
  implicit val projectFormat = Json.format[ProjectDocumentationDTO]
  implicit val documentationFormat = Json.format[Documentation]

  @ApiOperation(value = "Get documentation", response = classOf[Documentation])
  def generateDocumentation(): Action[AnyContent] = Action { request =>

    try {
      val projects = request.queryString.getOrElse("project", Seq()).map { p =>
        val hierarchy = p.split(">")(0).split("_").toSeq.filterNot(_.isEmpty).flatMap(hierarchyRepository.findBySlugName)
        val projectId = p.split(">")(1)
        val branchId = p.split(">").lift(2)

        val branches = branchRepository.findAllByProjectId(projectId)
          .filter(b => branchId.forall(_ == b.name))
          .map(b => BranchDocumentationDTO(b, featureRepository.findAllByBranchId(b.id)))
        val project = projectRepository.findAllById(Seq(projectId)).map(ProjectDocumentationDTO(_, branches))

        buildDocumentation(hierarchy, project)
      }

      if (projects.nonEmpty) {
        val documentation = projects.reduceLeft((acc: Documentation, current: Documentation) => acc.merge(current))

        Ok(Json.toJson(documentation))

      } else {
        NotFound
      }
    } catch {
      case _: Exception => BadRequest
    }
  }

  def buildDocumentation(hierarchy: Seq[HierarchyNode], projects: Seq[ProjectDocumentationDTO]): Documentation = {
    hierarchy.toList match {
      case Nil => Documentation(hierarchyRepository.findAll().head, projects, Seq())
      case h :: Nil => Documentation(h, projects, Seq())
      case h :: tail => Documentation(h, Seq(), Seq(buildDocumentation(tail, projects)))
    }

  }

}
