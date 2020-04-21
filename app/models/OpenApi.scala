package models

import play.api.libs.json._

case class OpenApiModel(modelName: String, required: Option[Seq[String]], openApiRows: Seq[OpenApiRow], childrenModels: Seq[OpenApiModel] = Seq(), errors: Seq[String] = Seq())

case class OpenApiRow(title: String, openApiType: String, default: String, description: String, example: String)

object OpenApiModel {
  implicit val openApiRowFormat = Json.format[OpenApiRow]
  implicit val openApiFormat = Json.format[OpenApiModel]
}


case class OpenApiPath(openApiSpec: JsValue, protocol: String, errors: Seq[String] = Seq())


object OpenApiPath {
  implicit val openApiPathFormat = Json.format[OpenApiPath]
}
