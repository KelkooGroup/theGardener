package models

case class Project(id: String, name: String, repositoryUrl: String, stableBranch: String, displayedBranches: Option[String] = None, featuresRootPath: Option[String], documentationRootPath: Option[String] = None, hierarchy: Option[Seq[HierarchyNode]] = None, branches: Option[Seq[Branch]] = None)
