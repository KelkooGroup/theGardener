package controllers.dto

import models._
import play.api.libs.json.Json

case class BranchMenuItemDTO(name: String, features: Seq[String])

object BranchMenuItemDTO {
  implicit val branchMenuFormat = Json.format[BranchMenuItemDTO]

  def apply(branch: Branch): BranchMenuItemDTO = {
    BranchMenuItemDTO(branch.name, branch.features)
  }
}

case class ProjectMenuItemDTO(id: String, label: String, stableBranch: String, branches: Seq[BranchMenuItemDTO])

object ProjectMenuItemDTO {
  implicit val projectMenuFormat = Json.format[ProjectMenuItemDTO]

  def apply(project: Project): ProjectMenuItemDTO = {
    ProjectMenuItemDTO(project.id, project.name, project.stableBranch, project.branches.getOrElse(Seq()).map(BranchMenuItemDTO(_)))
  }
}

case class MenuDTO(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectMenuItemDTO], children: Option[Seq[MenuDTO]])

object MenuDTO {
  implicit val menuFormat = Json.format[MenuDTO]

  def apply(menu: Menu): MenuDTO = {

    val hierarchyNode = menu.hierarchy.lastOption.getOrElse(HierarchyNode("", "", "", "", ""))

    MenuDTO(menu.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel,
      menu.projects.map(ProjectMenuItemDTO(_)), Some(menu.children.map(MenuDTO(_))))
  }
}


