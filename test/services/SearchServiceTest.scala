package services

import controllers.dto.{PageFragment, PageFragmentContent}
import models.{Page, Variable}
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.scalatest.{BeforeAndAfter, MustMatchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import repositories.{HierarchyRepository, PageRepository}

class SearchServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with MockitoSugar with ScalaFutures {

  val pageRepository = mock[PageRepository]
  val hierarchyRepository = mock[HierarchyRepository]

  val pageIndex = new PageIndex()
  val searchService = new SearchService(pageIndex, hierarchyRepository)


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

    pageIndex.addDocument(PageIndexDocument("hierarchy1", "path1", "branch1", "Doeco", "this is a test for markdown", ""))
    pageIndex.addDocument(PageIndexDocument("hierarchy2", "path2", "branch2", "Superstore", "", ""))
    pageIndex.addDocument(PageIndexDocument("hierarchy3", "path3", "branch3", "Doe co", "this is a text for markdown doe", ""))
    pageIndex.addDocument(PageIndexDocument("hierarchy4", "path4", "branch4", "co", "doe", ""))
    pageIndex.addDocument(PageIndexDocument("hierarchy5", "path5", "branch5", "Buymore", "this is a test for markdown this is a text for markdown", ""))
    pageIndex.addDocument(PageIndexDocument("hierarchy6", "path6", "branch6", "Do-Lots-Co", "", ""))
  }

  "PageService" should {

    "search in lucene" in {
      when(pageRepository.findByPath("path1")) thenReturn Option(page1)
      when(pageRepository.findByPath("path2")) thenReturn Option(page2)
      when(pageRepository.findByPath("path3")) thenReturn Option(page3)
      when(pageRepository.findByPath("path4")) thenReturn Option(page4)
      when(pageRepository.findByPath("path5")) thenReturn Option(page5)
      when(pageRepository.findByPath("path6")) thenReturn Option(page6)
      searchService.searchForPage("branch test") must contain theSameElementsAs Seq(Option(page1), Option(page5))
      searchService.searchForPage("branch1") must contain theSameElementsAs Seq(Option(page1))
      searchService.searchForPage(" text") must contain theSameElementsAs Seq(Option(page3), Option(page5))
      searchService.searchForPage("doe") must contain theSameElementsAs Seq(Option(page3), Option(page1), Option(page4))
    }


  }


}
