package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._
import repository._
import services.CriteriaService

case class BranchDocumentationDTO(id: Long, name: String, isStable: Boolean, features: Seq[Feature])

object BranchDocumentationDTO {
  def apply(branch: Branch, features: Seq[Feature]): BranchDocumentationDTO = {
    BranchDocumentationDTO(branch.id, branch.name, branch.isStable, features)
  }
}

case class ProjectDocumentationDTO(id: String, name: String, branches: Seq[BranchDocumentationDTO])

object ProjectDocumentationDTO {
  def apply(project: Project, branches: Seq[BranchDocumentationDTO]): ProjectDocumentationDTO = {
    ProjectDocumentationDTO(project.id, project.name, branches)
  }
}

case class Documentation(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectDocumentationDTO], children: Seq[Documentation]) {

  def merge(other: Documentation): Option[Documentation] = {
    if (id == other.id) {
      val (childrenCommonLeft, childrenOnlyLeft) = children.partition(c => other.children.exists(_.id == c.id))
      val (childrenCommonRight, childrenOnlyRight) = other.children.partition(c => children.exists(_.id == c.id))
      val childrenCommonMerged = childrenCommonLeft.flatMap(c => childrenCommonRight.flatMap(c.merge)).distinct

      Some(this.copy(projects = (projects ++ other.projects).distinct, children = childrenOnlyLeft ++ childrenOnlyRight ++ childrenCommonMerged))

    } else None
  }
}

object Documentation {
  def apply(hierarchyNode: HierarchyNode, projects: Seq[ProjectDocumentationDTO], children: Seq[Documentation]): Documentation = {
    Documentation(hierarchyNode.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, projects, children)
  }
}


@Api(value = "DocumentationController", produces = "application/json")
class DocumentationController @Inject()(documentationRepository: DocumentationRepository, criteriaService: CriteriaService, hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository) extends InjectedController {

  implicit val branchFormat = Json.format[BranchDocumentationDTO]
  implicit val projectFormat = Json.format[ProjectDocumentationDTO]
  implicit val documentationFormat = Json.format[Documentation]

  @ApiOperation(value = "Get documentation", response = classOf[Documentation])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "project", dataType = "string", paramType = "query", allowMultiple = true),
    new ApiImplicitParam(name = "node", dataType = "string", paramType = "query", allowMultiple = true)
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Project not found")
  ))
  def generateDocumentation(): Action[AnyContent] = Action { request =>
    try {
      val criteriasTree = criteriaService.getCriteriasTree()
      val criteriaMap = criteriaService.getCriterias().map(c => c.id -> CriteriaService.findCriteriasSubtree(c.id)(criteriasTree)).toMap

      val projectDocumentations = request.queryString.getOrElse("project", Seq()).flatMap { projectParam =>
        val hierarchy = projectParam.split(">")(0).split("_").toSeq.filterNot(_.isEmpty).flatMap(hierarchyRepository.findBySlugName)
        val projectId = projectParam.split(">")(1)
        val project = projectRepository.findById(projectId)
        val projectName = project.map(_.name).getOrElse("")

        val branchName = projectParam.split(">").lift(2).getOrElse(project.map(_.stableBranch).getOrElse("master"))

        hierarchy.lastOption.flatMap { hierarchyNode =>
          criteriaMap.get(hierarchyNode.id).flatten.map { criteria =>
              buildDocumentation(criteria.hierarchy, Seq(documentationRepository.buildProjectDocumentation(ProjectCriteria(projectId, projectName, branchName))))
          }
        }
      }

      val nodeDocumentations = request.queryString.getOrElse("node", Seq()).flatMap { nodeParam =>
        val hierarchy = nodeParam.split("_").toSeq.filterNot(_.isEmpty).flatMap(hierarchyRepository.findBySlugName)

        hierarchy.lastOption.map(_.id).flatMap(criteriaMap.get).flatten.map(CriteriaService.mergeChildrenHierarchy).getOrElse(Seq()).flatMap { hierarchyNode =>
          criteriaMap.get(hierarchyNode.id).flatten.map { criteria =>
            criteria.projects.map { project =>
              buildDocumentation(criteria.hierarchy, Seq(documentationRepository.buildProjectDocumentation(ProjectCriteria(project.id, project.name, project.stableBranch))))
            }
          }
        }.flatten
      }

      val documentations = projectDocumentations ++ nodeDocumentations

      if (documentations.nonEmpty) {
        Ok(Json.toJson(documentations.reduceLeft((acc: Documentation, current: Documentation) => acc.merge(current).getOrElse(acc))))

      } else {
        NotFound
      }
    } catch {
      case e: Exception =>
        Logger.error(e.getMessage, e)
        BadRequest
    }
  }

  def buildDocumentation(hierarchy: Seq[HierarchyNode], projects: Seq[ProjectDocumentationDTO]): Documentation = {
    hierarchy.toList match {
      case Nil => Documentation(hierarchyRepository.findAll().headOption.getOrElse(HierarchyNode("", "", "", "", "")), projects, Seq())
      case h :: Nil => Documentation(h, projects, Seq())
      case h :: tail => Documentation(h, Seq(), Seq(buildDocumentation(tail, projects)))
    }

  }

}
