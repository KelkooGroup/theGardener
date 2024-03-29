package services


import controllers.dto.{PageFragment, PageFragmentContent}
import models._
import org.mockito.Mockito.when
import org.mockito._
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito._
import play.api.Configuration
import play.api.cache.SyncCacheApi
import repositories._
import services.clients.OpenApiClient

import scala.concurrent.ExecutionContext

class PageServiceTest extends AnyWordSpec with Matchers with BeforeAndAfter with MockitoSugar with ScalaFutures {


  val config = mock[Configuration]
  val projectRepository = mock[ProjectRepository]
  val directoryRepository = mock[DirectoryRepository]
  val pageRepository = mock[PageRepository]
  val featureService = mock[FeatureService]
  val gherkinRepository = mock[GherkinRepository]
  val openApiClient = mock[OpenApiClient]
  val hierarchyRepository = mock[HierarchyRepository]
  val searchService = mock[SearchService]
  val hierarchyService = mock[HierarchyService]
  implicit val ec = mock[ExecutionContext]

  when(config.getOptional[String]("application.baseUrl")).thenReturn(None)
  when(config.getOptional[String]("lucene.index.path")).thenReturn(None)
  when(config.getOptional[Long]("cache.ttl")).thenReturn(Some(5L))

  val cache = new PageServiceCache(config, mock[SyncCacheApi])

  val pageIndex = new IndexService(config)
  val pageService = new PageService(config, projectRepository, directoryRepository, pageRepository, gherkinRepository, openApiClient, cache, pageIndex, hierarchyService)


  val variables = Seq(Variable(s"$${name1}", "value"), Variable(s"$${name2}", "value2"))
  val contentWithMarkdown = Seq(PageFragment("markdown", PageFragmentContent(Some(s"$${name1}"))))
  val contentWithExternalPage = Seq(PageFragment("includeExternalPage", PageFragmentContent(None, None, Some(s"$${name1}"))))
  val contentWithTwoFragment = contentWithMarkdown ++ contentWithExternalPage

  val page1 = Page(1, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)
  val page2 = Page(2, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)
  val page3 = Page(3, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)
  val page4 = Page(4, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)
  val page5 = Page(5, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)
  val page6 = Page(6, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)

  before {
    Mockito.reset(pageRepository)
  }

  "PageService" should {

    "replace variables in markdown" in {
    pageService.replaceVariablesInMarkdown(contentWithMarkdown, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))))

    pageService.replaceVariablesInMarkdown(contentWithTwoFragment, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))), PageFragment("includeExternalPage", PageFragmentContent(None, None, Some(s"value"))))

   }

  }


}
