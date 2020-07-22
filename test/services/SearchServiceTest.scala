package services

import org.mockito.Mockito
import org.scalatest.{BeforeAndAfter, MustMatchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import repositories.{HierarchyRepository, PageRepository}

class SearchServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with MockitoSugar with ScalaFutures {

  val pageRepository = mock[PageRepository]
  val hierarchyRepository = mock[HierarchyRepository]

  val pageIndex = new IndexService()
  val searchService = new SearchService(pageIndex)

  val pageIndex1 = PageIndexDocument("hierarchy1", "path1", "branch1", "Doeco", "this is a test for markdown", "")
  val pageIndex2 = PageIndexDocument("hierarchy2", "path2", "branch2", "Superstore", "", "")
  val pageIndex3 = PageIndexDocument("hierarchy3", "path3", "branch3", "Doe co", "this is a text for markdown doe", "")
  val pageIndex4 = PageIndexDocument("hierarchy4", "path4", "branch4", "co", "doe", "")
  val pageIndex5 = PageIndexDocument("hierarchy5", "path5", "branch5", "Buymore", "this is a test for markdown this is a text for markdown", "")
  val pageIndex6 = PageIndexDocument("hierarchy6", "path6", "branch6", "Do-Lots-Co", "", "")

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
      searchService.searchForPage("branch test") must contain theSameElementsAs Seq(pageIndex1, pageIndex5)
      searchService.searchForPage("branch1") must contain theSameElementsAs Seq(pageIndex1)
      searchService.searchForPage(" text") must contain theSameElementsAs Seq(pageIndex3, pageIndex5)
      searchService.searchForPage("doe") must contain theSameElementsAs Seq(pageIndex3, pageIndex1, pageIndex4)
    }


  }


}
