package models

import play.api.libs.json._

case class Branch(id: Long, name: String, isStable: Boolean, projectId: String)

object Branch {
  implicit val format: Format[Branch] = Json.format[Branch]
}
