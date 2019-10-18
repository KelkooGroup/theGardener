package models

case class HierarchyNode(id: String, slugName: String, name: String, childrenLabel: String, childLabel: String, directoryPath: Option[String]= None)

object HierarchyNode {
  val idSeparator = "."
}
