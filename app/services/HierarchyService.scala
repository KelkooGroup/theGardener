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

}


