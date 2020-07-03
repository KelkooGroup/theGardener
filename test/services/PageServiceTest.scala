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

  val pageIndex = new PageIndex()
  val pageService = new PageService(config, projectRepository, directoryRepository, pageRepository, gherkinRepository, openApiClient, cache, pageIndex)

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

    // TODO : remove this comment : GERALD : Moved here : it's initialisation of your tests
    pageService.replaceVariablesInMarkdown(contentWithMarkdown, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))))

    pageService.replaceVariablesInMarkdown(contentWithTwoFragment, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))), PageFragment("includeExternalPage", PageFragmentContent(None, None, Some(s"value"))))

    pageIndex.addDocument( PageIndexDocument("hierarchy1","path1","branch1","Doeco", "this is a test for markdown","") )
    pageIndex.addDocument( PageIndexDocument("hierarchy2","path2","branch2","Superstore", "","") )
    pageIndex.addDocument( PageIndexDocument("hierarchy3","path3","branch3","Doe co", "this is a text for markdown doe","") )
    pageIndex.addDocument( PageIndexDocument("hierarchy4","path4","branch4","co", "doe","") )
    pageIndex.addDocument( PageIndexDocument("hierarchy5","path5","branch5","Buymore", "this is a test for markdown this is a text for markdown","") )
    pageIndex.addDocument( PageIndexDocument("hierarchy6","path6","branch6","Do-Lots-Co", "","") )
  }

  "PageService" should {

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
