package controllers.dto

import models._
import play.api.libs.json.Json


case class DirectoryMenuItemDTO(id: Long, path: String, name: String, label: String, description: String, order: Long, children: Seq[DirectoryMenuItemDTO])

object DirectoryMenuItemDTO {
  implicit val directoryMenuFormat = Json.format[DirectoryMenuItemDTO]

  def apply(directory: Directory): DirectoryMenuItemDTO = {
    DirectoryMenuItemDTO(directory.id, directory.path, directory.name, directory.label, directory.description, directory.order, directory.children.map(DirectoryMenuItemDTO(_)))
  }
}

case class BranchMenuItemDTO(name: String, path: String, rootDirectory: Option[DirectoryMenuItemDTO])

object BranchMenuItemDTO {
  implicit val branchMenuFormat = Json.format[BranchMenuItemDTO]

  def apply(branch: Branch): BranchMenuItemDTO = {
    BranchMenuItemDTO(branch.name, s"${branch.projectId}>${branch.name}", branch.rootDirectory.map(DirectoryMenuItemDTO(_)))
  }
}

case class ProjectMenuItemDTO(id: String, path: String, label: String, stableBranch: String, branches: Seq[BranchMenuItemDTO])

object ProjectMenuItemDTO {
  implicit val projectMenuFormat = Json.format[ProjectMenuItemDTO]

  def apply(project: Project): ProjectMenuItemDTO = {
    ProjectMenuItemDTO(project.id, project.id, project.name, project.stableBranch, project.branches.getOrElse(Seq()).map(BranchMenuItemDTO(_)))
  }
}

case class MenuDTO(id: String, hierarchy: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Option[Seq[ProjectMenuItemDTO]], children: Option[Seq[MenuDTO]])

object MenuDTO {
  implicit val menuFormat = Json.format[MenuDTO]

  def getHierarchyNode(menu: Menu) = menu.hierarchy.lastOption.getOrElse(HierarchyNode("", "", "", "", ""))
  def getHierarchy(menu: Menu) = menu.hierarchy.map(_.slugName).filterNot(_ == "root").mkString("_", "_", "")

  def apply(menu: Menu): MenuDTO = {

    val hierarchyNode = getHierarchyNode(menu)

    MenuDTO(menu.id, getHierarchy(menu), hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, Some(menu.projects.map(ProjectMenuItemDTO(_))), Some(menu.children.map(MenuDTO(_))))
  }

  def header(menu: Menu) = {
    val hierarchyNode = getHierarchyNode(menu)

    MenuDTO(menu.id, getHierarchy(menu), hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, None, None)
  }
}


