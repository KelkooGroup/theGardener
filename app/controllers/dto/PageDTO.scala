package controllers.dto

import models.{Feature, OpenApiModel, OpenApiPath, Page}
import play.api.libs.json.Json
import services.PageFragmentUnderProcessing


case class PageFragmentContent(markdown: Option[String] = None, scenarios: Option[Feature] = None, includeExternalPage: Option[String] = None, openApi: Option[OpenApiModel] = None, openApiPath: Option[OpenApiPath] = None)

object PageFragmentContent {
  implicit val pageFormat = Json.format[PageFragmentContent]
}

case class PageFragment(`type`: String, data: PageFragmentContent)

object PageFragment {
  implicit val pageFormat = Json.format[PageFragment]

  val TypeMarkdown = "markdown"
  val TypeScenarios = "scenarios"
  val TypeIncludeExternalPage = "includeExternalPage"
  val TypeOpenApi = "openApi"
  val TypeOpenApiPath = "openApiPath"
  val TypeUnknown = "unknown"

  // scalastyle:off cyclomatic.complexity
  def apply(pageFragmentUnderProcessing: PageFragmentUnderProcessing): PageFragment = {
    pageFragmentUnderProcessing.markdown match {
      case Some(markdown) => new PageFragment(TypeMarkdown, new PageFragmentContent(markdown = Some(markdown)))
      case None => pageFragmentUnderProcessing.scenarios match {
        case Some(scenarios) => new PageFragment(TypeScenarios, new PageFragmentContent(scenarios = Some(scenarios)))
        case _ => pageFragmentUnderProcessing.includeExternalPage match {
          case Some(includeExternalPage) =>
            new PageFragment(TypeIncludeExternalPage, new PageFragmentContent(includeExternalPage = Some(includeExternalPage.url)))
          case _ => pageFragmentUnderProcessing.openApi match {
            case Some(openApi) =>
              new PageFragment(TypeOpenApi, new PageFragmentContent(openApi = Some(openApi)))
            case _ => pageFragmentUnderProcessing.openApiPath match {
              case Some(openApiPath) =>
                new PageFragment(TypeOpenApiPath, new PageFragmentContent(openApiPath = Some(openApiPath)))
              case _ =>
                new PageFragment(TypeUnknown, new PageFragmentContent())
            }
          }
        }
      }
    }
  }
}

case class PageDTO(path: String, relativePath: String, name: String, label: String, description: String, order: Int, content: Seq[PageFragment], sourceUrl: Option[String])

object PageDTO {
  implicit val pageFormat = Json.format[PageDTO]

  def apply(page: Page, content: Seq[PageFragment], sourceUrl: Option[String]): PageDTO = {
    PageDTO(page.path, page.relativePath, page.name, page.label, page.description, page.order, content, sourceUrl)
  }

}
