package models

case class Menu(id: String, hierarchy: Seq[HierarchyNode], projects: Seq[Project] = Seq(), children: Seq[Menu] = Seq(), directory: Option[Directory] = None)

