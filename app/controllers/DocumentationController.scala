package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import repository._
import services.MenuService

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
class DocumentationController @Inject()(documentationRepository: DocumentationRepository, criteriaService: MenuService, hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository) extends InjectedController with Logging {

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
      val criteriasTree = criteriaService.getMenuTree()
      val criteriaMap = criteriaService.getMenu().map(c => c.id -> MenuService.findMenuSubtree(c.id)(criteriasTree)).toMap

      val projectDocumentations = request.queryString.getOrElse("project", Seq()).flatMap { projectParam =>
        val params = projectParam.split(">")
        val hierarchy = params(0).split("_").toSeq.filterNot(_.isEmpty).flatMap(hierarchyRepository.findBySlugName)
        val projectId = params(1)
        val project = projectRepository.findById(projectId)
        val projectName = project.map(_.name).getOrElse("")
        val branchName = params.lift(2) match {
          case Some(branchParam) if branchParam.nonEmpty => branchParam
          case _ => project.map(_.stableBranch).getOrElse("master")
        }

        val featureFilter = params.lift(3)
        val tagsFilter = params.lift(4).map(tagsAsString => tagsAsString.split(",").toSeq)

        hierarchy.lastOption.flatMap { hierarchyNode =>
          criteriaMap.get(hierarchyNode.id).flatten.map { criteria =>
            buildDocumentation(criteria.hierarchy, Seq(documentationRepository.buildProjectDocumentation(ProjectMenuItem(projectId, projectName, branchName, featureFilter, tagsFilter))))
          }
        }
      }

      val nodeDocumentations = request.queryString.getOrElse("node", Seq()).flatMap { nodeParam =>
        val hierarchy = nodeParam.split("_").toSeq.filterNot(_.isEmpty).flatMap(hierarchyRepository.findBySlugName)

        hierarchy.lastOption.map(_.id).flatMap(criteriaMap.get).flatten.map(MenuService.mergeChildrenHierarchy).getOrElse(Seq()).flatMap { hierarchyNode =>
          criteriaMap.get(hierarchyNode.id).flatten.map { criteria =>
            criteria.projects.map { project =>
              buildDocumentation(criteria.hierarchy, Seq(documentationRepository.buildProjectDocumentation(ProjectMenuItem(project.id, project.name, project.stableBranch))))
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
        logger.error(e.getMessage, e)
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
