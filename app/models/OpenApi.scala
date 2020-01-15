package models

case class OpenApi(openApiRows: Seq[OpenApiRow])

case class OpenApiRow(title:String, openApiType:String, default: String, description: String, example:String)




