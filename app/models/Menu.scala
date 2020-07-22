package models

case class Menu(id: String, var  hierarchy: Seq[HierarchyNode], var projects: Seq[Project] = Seq(), var children: Seq[Menu] = Seq(), var directory: Option[Directory] = None, var shortcut: Option[String] = None)

