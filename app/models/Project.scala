package models

import play.api.libs.json.Json

case class Project(id: String, name: String, repositoryUrl: String, stableBranch: String, featuresRootPath: String)

object Project {
  implicit val projectFormat = Json.format[Project]
}
