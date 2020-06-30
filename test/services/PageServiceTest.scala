package services


import controllers.dto.{PageFragment, PageFragmentContent}
import models._
import org.mockito.Mockito.when
import org.mockito._
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatestplus.mockito._
import repositories._
import play.api.Configuration
import play.api.cache.SyncCacheApi
import services.clients.OpenApiClient

import scala.concurrent.ExecutionContext

class PageServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with MockitoSugar with ScalaFutures {


  val projectRepository = mock[ProjectRepository]
  val directoryRepository = mock[DirectoryRepository]
  val pageRepository = mock[PageRepository]
  val featureService = mock[FeatureService]
  val cache = new PageServiceCache(mock[SyncCacheApi])
  val gherkinRepository = mock[GherkinRepository]
  val config = mock[Configuration]
  val openApiClient = mock[OpenApiClient]
  implicit val ec = mock[ExecutionContext]


  when(config.getOptional[String]("application.baseUrl")).thenReturn(None)

  val pageService = new PageService(config, projectRepository, directoryRepository, pageRepository, gherkinRepository, openApiClient, cache)

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


    "Replace Variable in Markdown" in {
      pageService.replaceVariablesInMarkdown(contentWithMarkdown, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))))
    }

    "Replace Variables in Markdown with external Link" in {
      pageService.replaceVariablesInMarkdown(contentWithTwoFragment, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))), PageFragment("includeExternalPage", PageFragmentContent(None, None, Some(s"value"))))
    }

    "index a few documents" in {
      pageService.luceneSearchIndex.doc().fields(pageService.hierarchy("hierarchy1"), pageService.path("path1"), pageService.branch("branch1"), pageService.label("Doeco"), pageService.description("this is a test for markdown"), pageService.pageContent("")).index()
      pageService.luceneSearchIndex.doc().fields(pageService.hierarchy("hierarchy2"), pageService.path("path2"), pageService.branch("branch2"), pageService.label("Superstore"), pageService.description(""), pageService.pageContent("")).index()
      pageService.luceneSearchIndex.doc().fields(pageService.hierarchy("hierarchy3"), pageService.path("path3"), pageService.branch("branch3"), pageService.label("Doe co"), pageService.description("this is a text for markdown doe"), pageService.pageContent("")).index()
      pageService.luceneSearchIndex.doc().fields(pageService.hierarchy("hierarchy4"), pageService.path("path4"), pageService.branch("branch4"), pageService.label("co"), pageService.description("doe"), pageService.pageContent("")).index()
      pageService.luceneSearchIndex.doc().fields(pageService.hierarchy("hierarchy5"), pageService.path("path5"), pageService.branch("branch5"), pageService.label("Buymore"), pageService.description("this is a test for markdown this is a text for markdown"), pageService.pageContent("")).index()
      pageService.luceneSearchIndex.doc().fields(pageService.hierarchy("hierarchy6"), pageService.path("path6"), pageService.branch("branch6"), pageService.label("Do-Lots-Co"), pageService.description(""), pageService.pageContent("")).index()
    }

    "search in lucene" in {
      when(pageRepository.findByPath("path1")) thenReturn Option(page1)
      when(pageRepository.findByPath("path2")) thenReturn Option(page2)
      when(pageRepository.findByPath("path3")) thenReturn Option(page3)
      when(pageRepository.findByPath("path4")) thenReturn Option(page4)
      when(pageRepository.findByPath("path5")) thenReturn Option(page5)
      when(pageRepository.findByPath("path6")) thenReturn Option(page6)
      pageService.searchForPage("branch test") must contain theSameElementsAs Seq(Option(page1), Option(page5))
      pageService.searchForPage("branch1") must contain theSameElementsAs Seq(Option(page1))
      pageService.searchForPage(" text") must contain theSameElementsAs Seq(Option(page3), Option(page5))
      pageService.searchForPage("doe") must contain theSameElementsAs Seq(Option(page3), Option(page1), Option(page4))
    }


  }


}
