package models

case class Project(id: String, name: String, repositoryUrl: String, stableBranch: String, featuresRootPath: String, hierarchy: Option[Seq[HierarchyNode]] = None, branch : Option[Seq[Branch]] = None)