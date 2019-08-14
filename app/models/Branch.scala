package models

case class Branch(id: Long, name: String, isStable: Boolean, projectId: String, features: Seq[String] = Seq(), rootDirectory: Option[Directory] = None)

