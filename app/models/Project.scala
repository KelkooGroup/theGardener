package models

import play.api.libs.json.Json

case class Group(systems: Seq[System])

case class System(components: Seq[Component])

case class Component(serverSettings: ServerSettings, componentSettings: ComponentSettings, features: Seq[Feature])

object Project {
  implicit val componentFormat = Json.format[Component]
  implicit val systemFormat = Json.format[System]
  implicit val groupFormat = Json.format[Group]
}
