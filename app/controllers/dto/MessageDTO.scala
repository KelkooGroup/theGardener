package controllers.dto

import play.api.libs.json.Json

case class MessageDTO(message : String, elements: Option[Seq[String]]= None)

object MessageDTO {
  implicit val pageFormat = Json.format[MessageDTO]
}