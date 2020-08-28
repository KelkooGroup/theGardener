package services

import com.outr.lucene4s.query.{Sort, TermSearchTerm}
import com.outr.lucene4s.{DirectLucene, parse}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json

case class SearchResult(items: Seq[SearchResultItem])

case class SearchResultItem(page: PageIndexDocument, highlights: Seq[HighlightedFragment])

case class PageIndexDocument(id: String, hierarchy: String, path: String, breadcrumb: String, project: String, branch: String, label: String, description: String, pageContent: String)

case class HighlightedFragment(fragment: String, word: String)

object HighlightedFragment {
  implicit val format = Json.format[HighlightedFragment]
}

object PageIndexDocument {
  implicit val format = Json.format[PageIndexDocument]
}

object SearchResultItem {
  implicit val format = Json.format[SearchResultItem]
}

object SearchResult {
  implicit val format = Json.format[SearchResult]
}


@Singleton
class IndexService {

  val luceneSearchIndex = new DirectLucene(uniqueFields = List("id"), defaultFullTextSearchable = true, appendIfExists = false, autoCommit = false)

  private val id = luceneSearchIndex.create.field[String]("id")
  private val hierarchy = luceneSearchIndex.create.field[String]("hierarchy")
  private val path = luceneSearchIndex.create.field[String]("path", fullTextSearchable = false)
  private val breadcrumb = luceneSearchIndex.create.field[String]("breadcrumb")
  private val project = luceneSearchIndex.create.field[String]("project")
  private val branch = luceneSearchIndex.create.field[String]("branch")
  private val label = luceneSearchIndex.create.field[String]("label")
  private val description = luceneSearchIndex.create.field[String]("description")
  private val pageContent = luceneSearchIndex.create.field[String]("pageContent", sortable = false)

  def insertOrUpdateDocument(document: PageIndexDocument): Unit = {
    luceneSearchIndex.delete(new TermSearchTerm(Some(id), document.id))
    luceneSearchIndex.commit()
    luceneSearchIndex.doc().fields(id(document.id.trim),
      hierarchy(document.hierarchy.trim),
      path(document.path.trim),
      breadcrumb(document.breadcrumb.trim),
      project(document.project.trim),
      branch(document.branch.trim),
      label(document.label.trim),
      description(document.description.trim),
      pageContent(document.pageContent.trim)
    ).index()
    luceneSearchIndex.commit()
  }

  def query(keywords: String): SearchResult = {
    val results = luceneSearchIndex.query().sort(Sort.Score).filter(parse("id:" + keywords + "*^20 " + " | "
      + "breadcrumb:" + keywords + "*^50 " + " | "
      + "project:" + keywords + "*^30 " + " | "
      + "branch:" + keywords + "*^30" + " | "
      + "label:" + keywords + "*^100 " + " | "
      + "description:" + keywords + "*^100 " + " | "
      + "pageContent:" + keywords + "*^30"
    )).highlight().limit(50).search()

    SearchResult(results.results.map { result =>
      val highlighting = result.highlighting(breadcrumb).map(frg => HighlightedFragment(frg.fragment, frg.word)) +:
        result.highlighting(label).map(frg => HighlightedFragment(frg.fragment, frg.word)) +:
        result.highlighting(description).map(frg => HighlightedFragment(frg.fragment, frg.word)) +:
        result.highlighting(pageContent).map(frg => HighlightedFragment(frg.fragment, frg.word)) +: Nil
      SearchResultItem(PageIndexDocument(result(id), result(hierarchy), result(path), result(breadcrumb), result(project), result(branch), result(label), result(description), result(pageContent)), highlighting.flatten)
    })
  }

  def reset(): Unit = {
    luceneSearchIndex.deleteAll()
  }

}

class SearchService @Inject()(pageIndex: IndexService) {

  def searchForPage(keywords: String): SearchResult = {
    pageIndex.query(keywords)
  }

}
