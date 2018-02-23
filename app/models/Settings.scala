package models

import play.api.libs.json.Json

case class ServerSettings(id: String, `type`: String, rootPath: String, localRootPath: String)

object ServerSettings {
  implicit val serverSettingsFormat = Json.format[ServerSettings]
}

case class ComponentSettings(id: String, name: String, host: ServerSettings, projectPath: String, stableBranch: String, featuresRootPath: String)

//case class ComponentSettings(id: String, name: String, repositoryUrl: String, stableBranch: String, featuresRootPath: String)

object ComponentSettings {
  implicit val componentSettingsFormat = Json.format[ComponentSettings]
}