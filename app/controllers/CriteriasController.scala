package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc._
import repository._

case class ProjectDTO(id: String, label: String, stableBranch: String, branches: Seq [String])

object ProjectDTO {
  def apply(project: Project, branches: Seq[String]): ProjectDTO = {
    ProjectDTO(project.id, project.name, project.stableBranch, branches)
  }
}

case class CriteriaDTO(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectDTO])

object CriteriaDTO {
  def apply(hierarchyNode: HierarchyNode, projects: Seq[ProjectDTO]): CriteriaDTO = {
    CriteriaDTO(hierarchyNode.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, projects)
  }
}

@Api(value = "CriteriasController", produces = "application/json")
class CriteriasController @Inject()(hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository, branchRepository: BranchRepository) extends InjectedController {

  implicit val projectFormat = Json.format[ProjectDTO]
  implicit val criteriaFormat = Json.format[CriteriaDTO]

  @ApiOperation(value = "Get all criterias", response = classOf[CriteriaDTO])
  def getCriterias(): Action[AnyContent] = Action {

    val hierarchyNodes = hierarchyRepository.findAll().map { hierarchyNode =>
      val projects = projectRepository.findAllByHierarchyId(hierarchyNode.id).map { project =>
        ProjectDTO(project, branchRepository.findAllByProjectId(project.id).map(_.name))
      }

      CriteriaDTO(hierarchyNode, projects.sortBy(_.id))
    }

    Ok(Json.toJson(hierarchyNodes.sortBy(_.id)))
  }
}
