package services

import javax.inject._
import models._
import repositories._


class HierarchyService @Inject()(hierarchyRepository: HierarchyRepository) {

  def existsById(id: String): Boolean = hierarchyRepository.existsById(id)

  def findById(id: String): Option[HierarchyNode] = hierarchyRepository.findById(id)

  def save(hierarchy: HierarchyNode): HierarchyNode = hierarchyRepository.save(hierarchy)

  def deleteById(id: String): Unit = hierarchyRepository.deleteById(id)

  def findAll(): Seq[HierarchyNode] = hierarchyRepository.findAll()

  def wellFormedId(id: String): Boolean = id.matches("(\\.[0-9]+)*\\.")

  def wellFormedShortcut(hierarchy: HierarchyNode): Boolean = hierarchy.shortcut match {
    case None => true
    case Some(shortcut) => !hierarchy.id.startsWith(shortcut)
  }

  def getHierarchyPath(pageJoinProject: PageJoinProject): Option[String] = {
    val projectHierarchies = hierarchyRepository.findAllByProjectId(pageJoinProject.project.id)
    if (projectHierarchies.nonEmpty) {
      val hierarchyId = projectHierarchies.headOption.map(_.id).getOrElse("")
      val splitedId = hierarchyId.split('.')
      var hierarchyPath = ""
      var currentHierarchyId = "."
      for (id <- splitedId) {
        if (id.nonEmpty) {
          currentHierarchyId += id + "."
          hierarchyPath += "/" + hierarchyRepository.findById(currentHierarchyId).map(_.slugName).getOrElse("Not Found")
        }
      }
      Some(hierarchyPath)
    } else {
      None
    }
  }

  def getBreadcrumb(pageJoinProject: PageJoinProject): String = {
    val projectHierarchies = hierarchyRepository.findAllByProjectId(pageJoinProject.project.id)
    if (projectHierarchies.nonEmpty) {
      val hierarchyId = projectHierarchies.headOption.map(_.id).getOrElse("")
      val splitedId = hierarchyId.split('.')
      var hierarchyPath = ""
      var currentHierarchyId = "."
      for (id <- splitedId) {
        if (id.nonEmpty) {
          currentHierarchyId += id + "."
          hierarchyPath += " / " + hierarchyRepository.findById(currentHierarchyId).map(_.name).getOrElse("Not Found")
        }
      }
      hierarchyPath.replaceFirst("/", "") + " / " + pageJoinProject.project.name + pageJoinProject.directory.relativePath.replaceAll("/", " / ") + pageJoinProject.page.label
    } else {
      pageJoinProject.page.path
    }
  }

}


