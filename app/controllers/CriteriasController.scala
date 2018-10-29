package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc._
import repository._

case class ProjectCriteriasDTO(id: String, label: String, stableBranch: String, branches: Seq [String])

object ProjectCriteriasDTO {
  def apply(project: Project, branches: Seq[String]): ProjectCriteriasDTO = {
    ProjectCriteriasDTO(project.id, project.name, project.stableBranch, branches)
  }
}

case class CriteriaDTO(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectCriteriasDTO])

object CriteriaDTO {
  def apply(hierarchyNode: HierarchyNode, projects: Seq[ProjectCriteriasDTO]): CriteriaDTO = {
    CriteriaDTO(hierarchyNode.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, projects)
  }
}

@Api(value = "CriteriasController", produces = "application/json")
class CriteriasController @Inject()(hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository, branchRepository: BranchRepository) extends InjectedController {

  implicit val projectFormat = Json.format[ProjectCriteriasDTO]
  implicit val criteriaFormat = Json.format[CriteriaDTO]

  @ApiOperation(value = "Get all criterias", response = classOf[CriteriaDTO])
  def getCriterias(): Action[AnyContent] = Action {

    val hierarchyNodes = hierarchyRepository.findAll().map { hierarchyNode =>
      val projects = projectRepository.findAllByHierarchyId(hierarchyNode.id).map { project =>
        ProjectCriteriasDTO(project, branchRepository.findAllByProjectId(project.id).map(_.name))
      }

      CriteriaDTO(hierarchyNode, projects.sortBy(_.id))
    }

    Ok(Json.toJson(hierarchyNodes.sortBy(_.id)))
  }
}
