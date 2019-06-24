package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.libs.json.Json
import play.api.mvc._
import services.MenuService

case class BranchMenuItemDTO(name: String, features: Seq[String])

object BranchMenuItemDTO {
  def apply(branch: Branch): BranchMenuItemDTO = {
    BranchMenuItemDTO(branch.name, branch.features)
  }
}

case class ProjectMenuItemDTO(id: String, label: String, stableBranch: String, branches: Seq[BranchMenuItemDTO])

object ProjectMenuItemDTO {
  def apply(project: Project): ProjectMenuItemDTO = {
    ProjectMenuItemDTO(project.id, project.name, project.stableBranch, project.branches.getOrElse(Seq()).map(BranchMenuItemDTO(_)))
  }
}

case class MenuDTO(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectMenuItemDTO], children: Option[Seq[MenuDTO]])

object MenuDTO {
  def apply(menu: Menu): MenuDTO = {

    val hierarchyNode = menu.hierarchy.lastOption.getOrElse(HierarchyNode("", "", "", "", ""))

    MenuDTO(menu.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel,
      menu.projects.map(ProjectMenuItemDTO(_)), Some(menu.children.map(MenuDTO(_))))
  }
}

@Api(value = "MenuController", produces = "application/json")
class MenuController @Inject()(menuService: MenuService) extends InjectedController {

  implicit val branchFormat = Json.format[BranchMenuItemDTO]
  implicit val projectFormat = Json.format[ProjectMenuItemDTO]
  implicit val menuFormat = Json.format[MenuDTO]

  @ApiOperation(value = "Get all menu items", response = classOf[MenuDTO], responseContainer = "list")
  def getMenu(): Action[AnyContent] = Action {

    Ok(Json.toJson(MenuDTO(menuService.getMenuTree())))
  }
}
