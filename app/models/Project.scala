package models

case class Variable(name: String, value: String)

case class Project(id: String,
                   name: String,
                   repositoryUrl: String,
                   stableBranch: String,
                   displayedBranches: Option[String] = None,
                   featuresRootPath: Option[String],
                   documentationRootPath: Option[String] = None,
                   variables: Option[Seq[Variable]] = None,
                   hierarchy: Option[Seq[HierarchyNode]] = None,
                   branches: Option[Seq[Branch]] = None)
