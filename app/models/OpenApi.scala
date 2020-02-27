package models

import play.api.libs.json._

case class OpenApi(modelName:String, required:Option[Seq[String]],openApiRows: Seq[OpenApiRow], childrenModels: Seq[OpenApi] = Seq())

case class OpenApiRow(title: String, openApiType: String, default: String, description: String, example: String)

object OpenApi {
  implicit val openApiRowFormat = Json.format[OpenApiRow]
  implicit val openApiFormat = Json.format[OpenApi]
}


case class OpenApiPath(openApiSpec: JsValue)
