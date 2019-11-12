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

class PageServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with MockitoSugar with ScalaFutures {


  val projectRepository = mock[ProjectRepository]
  val directoryRepository = mock[DirectoryRepository]
  val pageRepository = mock[PageRepository]
  val featureService = mock[FeatureService]
  val cache = mock[SyncCacheApi]
  val gherkinRepository = mock[GherkinRepository]
  val config = mock[Configuration]


  when(config.getOptional[String]("application.baseUrl")).thenReturn(None)

  val pageService = new PageService(config, projectRepository, directoryRepository, pageRepository, gherkinRepository, cache)

  val variables = Seq(Variable(s"$${name1}", "value"),Variable(s"$${name2}", "value2"))
  val contentWithMarkdown = Seq(PageFragment("markdown", PageFragmentContent(Some(s"$${name1}"))))
  val contentWithExternalPage = Seq(PageFragment("includeExternalPage", PageFragmentContent(None,None,Some(s"$${name1}"))))
  val contentWithTwoFragment = contentWithMarkdown ++ contentWithExternalPage

  before {
    Mockito.reset(pageRepository)
  }

  "PageService" should {
    "Replace Variable in Markdown" in {
      pageService.replaceVariablesInMarkdown(contentWithMarkdown, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))))
    }

    "Replace Variables in Markdown with external Link" in {
      pageService.replaceVariablesInMarkdown(contentWithTwoFragment, variables) must contain theSameElementsAs Seq(PageFragment("markdown", PageFragmentContent(Some(s"value"))),PageFragment("includeExternalPage", PageFragmentContent(None,None,Some(s"value"))))
    }
  }




}
