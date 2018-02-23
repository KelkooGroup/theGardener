package models

import play.api.libs.json.Json

case class Project(id: String, group: String, system: String, name: String, repositoryUrl: String, stableBranch: String, featuresRootPath: String, features: Seq[Feature] = Seq())

object Project {
  implicit val componentFormat = Json.format[Project]
}
