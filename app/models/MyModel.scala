package models

import play.api.libs.json.Json

case class MyModel(field1: String, field2: Int)

object MyModel {
  implicit val myModelFormat = Json.format[MyModel]
}