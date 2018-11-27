package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc._
import services.CriteriaService

case class BranchCriteriasDTO(name: String, features: Seq[String])

object BranchCriteriasDTO {
  def apply(branch: Branch): BranchCriteriasDTO = {
    BranchCriteriasDTO(branch.name, branch.features)
  }
}

case class ProjectCriteriasDTO(id: String, label: String, stableBranch: String, branches: Seq[BranchCriteriasDTO])

object ProjectCriteriasDTO {
  def apply(project: Project): ProjectCriteriasDTO = {
    ProjectCriteriasDTO(project.id, project.name, project.stableBranch, project.branches.getOrElse(Seq()).map(BranchCriteriasDTO(_)))
  }
}

case class CriteriaDTO(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectCriteriasDTO], children: Option[Seq[CriteriaDTO]])

object CriteriaDTO {
  def apply(criteria: Criteria): CriteriaDTO = {

    val hierarchyNode = criteria.hierarchy.lastOption.getOrElse(HierarchyNode("", "", "", "", ""))

    CriteriaDTO(criteria.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel,
      criteria.projects.map(ProjectCriteriasDTO(_)), Some(criteria.children.map(CriteriaDTO(_))))
  }
}

@Api(value = "CriteriasController", produces = "application/json")
class CriteriasController @Inject()(criteriaService: CriteriaService) extends InjectedController {

  implicit val branchFormat = Json.format[BranchCriteriasDTO]
  implicit val projectFormat = Json.format[ProjectCriteriasDTO]
  implicit val criteriaFormat = Json.format[CriteriaDTO]

  @ApiOperation(value = "Get all criterias", response = classOf[CriteriaDTO], responseContainer = "list")
  def getCriterias(): Action[AnyContent] = Action {

    Ok(Json.toJson(criteriaService.getCriterias(true).map(CriteriaDTO(_).copy(children = None))))
  }

  @ApiOperation(value = "Get all criterias in a tree", response = classOf[CriteriaDTO])
  def getCriteriasTree(): Action[AnyContent] = Action {

    Ok(Json.toJson(CriteriaDTO(criteriaService.getCriteriasTree(true))))
  }
}
