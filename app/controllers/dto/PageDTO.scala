package controllers.dto

import models.Page
import play.api.libs.json.Json

case class PageDTO(path: String, relativePath: String, name: String, label: String, description: String, order: Int, markdown: Option[String])

object PageDTO {
  implicit val pageFormat = Json.format[PageDTO]

  def apply(page: Page): PageDTO = {
    PageDTO(page.path, page.relativePath, page.name, page.label, page.description, page.order, page.markdown)
  }

}
