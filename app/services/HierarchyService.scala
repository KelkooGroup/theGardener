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
    val projectHierarchy = hierarchyRepository.findAllByProjectId(pageJoinProject.project.id).headOption match {
      case Some(h) => Some(h)
      case None => hierarchyRepository.findByDirectoryPath(pageJoinProject.directory.path)
    }
    projectHierarchy match {
      case Some(h) =>
        val hierarchyId = h.id
        val spitedId = hierarchyId.split('.')
        var hierarchyPath = ""
        var currentHierarchyId = "."
        for (id <- spitedId) {
          if (id.nonEmpty) {
            currentHierarchyId += id + "."
            hierarchyPath += "/" + hierarchyRepository.findById(currentHierarchyId).map(_.slugName).getOrElse("Not Found")
          }
        }
        Some(hierarchyPath)
      case None => None
    }
  }

  def getBreadcrumb(pageJoinProject: PageJoinProject): String = {
    val projectHierarchy = hierarchyRepository.findAllByProjectId(pageJoinProject.project.id).headOption match {
      case Some(h) => Some(h)
      case None => hierarchyRepository.findByDirectoryPath(pageJoinProject.directory.path)
    }
    projectHierarchy match {
      case Some(h) =>
        val hierarchyId = h.id
        val spitedId = hierarchyId.split('.')
        var hierarchyPath = ""
        var currentHierarchyId = "."
        for (id <- spitedId) {
          if (id.nonEmpty) {
            currentHierarchyId += id + "."
            hierarchyPath += "/" + hierarchyRepository.findById(currentHierarchyId).map(_.name).getOrElse("Not Found")
          }
        }
        hierarchyPath.replaceFirst("/", "") + " / " + pageJoinProject.project.name + pageJoinProject.directory.relativePath.replaceAll("/", " / ") + pageJoinProject.page.label
      case None => ""
    }
  }

}


