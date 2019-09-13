package controllers.dto

import models.{Feature, Page}
import play.api.libs.json.Json
import services.{PageFragmentUnderProcessing}


case class PageFragmentContent(markdown: Option[String] = None, scenarios: Option[Feature] = None)

object PageFragmentContent {
  implicit val pageFormat = Json.format[PageFragmentContent]
}

case class PageFragment(`type`: String, data: PageFragmentContent)

object PageFragment {
  implicit val pageFormat = Json.format[PageFragment]

  def apply(pageFragmentUnderProcessing: PageFragmentUnderProcessing): PageFragment = {
    pageFragmentUnderProcessing.markdown match{
      case Some(markdown) => new PageFragment("markdown", new PageFragmentContent(markdown = Some(markdown)))
      case None => pageFragmentUnderProcessing.scenarios match{
        case Some(scenarios) => new PageFragment("scenarios", new PageFragmentContent(scenarios = Some(scenarios)))
        case None => new PageFragment("unknown", new PageFragmentContent())
      }
    }
  }
}

case class PageDTO(path: String, relativePath: String, name: String, label: String, description: String, order: Int, content: Seq[PageFragment])

object PageDTO {
  implicit val pageFormat = Json.format[PageDTO]

  def apply(page: Page, content: Seq[PageFragment]): PageDTO = {
    PageDTO(page.path, page.relativePath, page.name, page.label, page.description, page.order, content)
  }

}
