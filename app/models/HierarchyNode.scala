package models

case class HierarchyNode(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String)

object HierarchyNode {
  val idSeparator = "."
}
