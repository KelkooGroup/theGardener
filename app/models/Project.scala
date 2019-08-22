package models

case class Project(id: String, name: String, repositoryUrl: String, stableBranch: String, featuresRootPath: String, documentationRootPath: Option[String] = None, variables: Option[String] = None,hierarchy: Option[Seq[HierarchyNode]] = None, branches: Option[Seq[Branch]] = None)
