package controllers.dto

import models._
import play.api.libs.json.Json

case class BranchDocumentationDTO(id: Long, name: String, isStable: Boolean, features: Seq[Feature])

object BranchDocumentationDTO {
  implicit val branchDocumentationFormat = Json.format[BranchDocumentationDTO]

  def apply(branch: Branch, features: Seq[Feature]): BranchDocumentationDTO = {
    BranchDocumentationDTO(branch.id, branch.name, branch.isStable, features)
  }
}

case class ProjectDocumentationDTO(id: String, name: String, branches: Seq[BranchDocumentationDTO])

object ProjectDocumentationDTO {
  implicit val projectDocumentationFormat = Json.format[ProjectDocumentationDTO]

  def apply(project: Project, branches: Seq[BranchDocumentationDTO]): ProjectDocumentationDTO = {
    ProjectDocumentationDTO(project.id, project.name, branches)
  }
}


case class DocumentationDTO(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, projects: Seq[ProjectDocumentationDTO], children: Seq[DocumentationDTO]) {

  def merge(other: DocumentationDTO): Option[DocumentationDTO] = {
    if (id == other.id) {
      val (childrenCommonLeft, childrenOnlyLeft) = children.partition(c => other.children.exists(_.id == c.id))
      val (childrenCommonRight, childrenOnlyRight) = other.children.partition(c => children.exists(_.id == c.id))
      val childrenCommonMerged = childrenCommonLeft.flatMap(c => childrenCommonRight.flatMap(c.merge)).distinct

      Some(this.copy(projects = (projects ++ other.projects).distinct, children = childrenOnlyLeft ++ childrenOnlyRight ++ childrenCommonMerged))

    } else None
  }
}

object DocumentationDTO {
  implicit val documentationFormat = Json.format[DocumentationDTO]

  def apply(hierarchyNode: HierarchyNode, projects: Seq[ProjectDocumentationDTO], children: Seq[DocumentationDTO]): DocumentationDTO = {
    DocumentationDTO(hierarchyNode.id, hierarchyNode.slugName, hierarchyNode.name, hierarchyNode.childrenLabel, hierarchyNode.childLabel, projects, children)
  }
}
