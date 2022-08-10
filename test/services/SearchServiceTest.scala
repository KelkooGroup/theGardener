package services

import org.mockito.Mockito
import org.scalatest.BeforeAndAfter
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar
import repositories.{HierarchyRepository, PageRepository}

class SearchServiceTest extends AnyWordSpec with Matchers with BeforeAndAfter with MockitoSugar with ScalaFutures {

  val pageRepository = mock[PageRepository]
  val hierarchyRepository = mock[HierarchyRepository]

  val pageIndex = new IndexService()
  val searchService = new SearchService(pageIndex)

  val pageIndex1 = PageIndexDocument("id1", "hierarchy1", "path1", "breadcrum1", "project1", "branch1", "Doeco", "this is a test for markdown", "")
  val pageIndex2 = PageIndexDocument("id2", "hierarchy2", "path2", "breadcrum2", "project2", "branch2", "Superstore", "page two", "")
  val pageIndex3 = PageIndexDocument("id3", "hierarchy3", "path3", "breadcrum3", "project3", "branch3", "Doe co", "this is a text for markdown doe", "")
  val pageIndex4 = PageIndexDocument("id4", "hierarchy4", "path4", "breadcrum4", "project4", "branch4", "co", "doe", "")
  val pageIndex5 = PageIndexDocument("id5", "hierarchy5", "path5", "breadcrum5", "project5", "branch5", "Buymore", "this is a test for markdown this is a text for markdown", "")
  val pageIndex6 = PageIndexDocument("id6", "hierarchy6", "path6", "breadcrum6", "project6", "branch6", "Do-Lots-Co", "", "")

  before {
    Mockito.reset(pageRepository)

    pageIndex.reset()
    pageIndex.insertOrUpdateDocument(PageIndexDocument("id1", "hierarchy1", "path1", "breadcrum1", "project1", "branch1", "Doeco", "this is a test for markdown", ""))
    pageIndex.insertOrUpdateDocument(PageIndexDocument("id2", "hierarchy2", "path2", "breadcrum2", "project2", "branch2", "Superstore", "page two", ""))
    pageIndex.insertOrUpdateDocument(PageIndexDocument("id3", "hierarchy3", "path3", "breadcrum3", "project3", "branch3", "Doe co", "this is a text for markdown doe", ""))
    pageIndex.insertOrUpdateDocument(PageIndexDocument("id4", "hierarchy4", "path4", "breadcrum4", "project4", "branch4", "co", "doe", ""))
    pageIndex.insertOrUpdateDocument(PageIndexDocument("id5", "hierarchy5", "path5", "breadcrum5", "project5", "branch5", "Buymore", "this is a test for markdown this is a text for markdown", ""))
    pageIndex.insertOrUpdateDocument(PageIndexDocument("id6", "hierarchy6", "path6", "breadcrum6", "project6", "branch6", "Do-Lots-Co", "", ""))
  }

  "PageService" should {

    "search in lucene" in {
      var items = searchService.searchForPage("branch test").items
      items.length must equal(2)
      items(0).page.id must equal("id1")
      items(1).page.id must equal("id5")

      items = searchService.searchForPage("branch1").items
      items.length must equal(1)
      items(0).page.id must equal("id1")

      items = searchService.searchForPage(" text").items
      items.length must equal(2)
      items(0).page.id must equal("id3")
      items(1).page.id must equal("id5")

      items = searchService.searchForPage("doe").items
      items.length must equal(3)
      items(0).page.id must equal("id3")
      items(1).page.id must equal("id1")
      items(2).page.id must equal("id4")
    }

    "insert or update in lucene" in {
      var result = searchService.searchForPage("Superstore").items

      result.size must equal(1)
      result.head.page.id must equal("id2")
      result.head.page.description must equal("page two")

      for (pageNumber <- 1 to 10) {
        pageIndex.insertOrUpdateDocument(PageIndexDocument("id2", "hierarchy2", "path2", "breadcrum2", "branch2", "project2", "Superstore", s"page $pageNumber", ""))
      }

      result = searchService.searchForPage("Superstore").items

      result.size must equal(1)
      result.head.page.id must equal("id2")
      result.head.page.description must equal(s"page 10")
    }

  }

}
