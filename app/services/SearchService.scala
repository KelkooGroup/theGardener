package services

import com.outr.lucene4s.field.Field
import com.outr.lucene4s.mapper.Searchable
import com.outr.lucene4s.query.{SearchTerm, Sort}
import com.outr.lucene4s.{DirectLucene, parse, _}
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Json

case class SearchResult(items: Seq[SearchResultItem])

case class SearchResultItem(page: PageIndexDocument, highlights: Seq[HighlightedFragment])

case class PageIndexDocument(id: String, hierarchy: String, path: String, breadcrumb: String, project: String, branch: String, label: String, description: String, pageContent: String)

case class HighlightedFragment(fragment: String, word: String)

case class PageIndexLucene(id: Id[PageIndexLucene], hierarchy: String, path: String, breadcrumb: String, project: String, branch: String, label: String, description: String, pageContent: String)

case class Id[T](value: String) {
  override def toString: String = value
}

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


trait SearchablePageIndexLucene extends Searchable[PageIndexLucene] {
  // We must implement the criteria for updating and deleting
  override def idSearchTerms(t: PageIndexLucene): List[SearchTerm] = List(exact(id(t.id)))

  implicit def stringifyId[T]: Stringify[Id[T]] = Stringify[Id[T]]((s: String) => Id[T](s))

  // We can create custom / explicit configurations for each field
  val id: Field[Id[PageIndexLucene]] = lucene.create.stringifiedField[Id[PageIndexLucene]]("id", fullTextSearchable = false)

  def hierarchy: Field[String]

  def path: Field[String]

  def breadcrumb: Field[String]

  def project: Field[String]

  def branch: Field[String]

  def label: Field[String]

  def description: Field[String]

  def pageContent: Field[String]

}


object lucene extends DirectLucene(uniqueFields = List("id"), defaultFullTextSearchable = true, appendIfExists = false, autoCommit = true) {
  val page: SearchablePageIndexLucene = create.searchable[SearchablePageIndexLucene]
}


@Singleton
class IndexService {

  def insertOrUpdateDocument(document: PageIndexDocument): Unit = {
    val docLucene = PageIndexLucene(Id[PageIndexLucene](document.id), document.hierarchy, document.path, document.breadcrumb, document.project, document.branch, document.label, document.description, document.pageContent)
    lucene.page.delete(docLucene)
    lucene.page.insert(docLucene)
    ()
  }

  def query(keywords: String): SearchResult = {
    val results = lucene.page.query().sort(Sort.Score).filter(parse("id:" + keywords + "*^20 " + " | "
      + "breadcrumb:" + keywords + "*^50 " + " | "
      + "project:" + keywords + "*^30 " + " | "
      + "branch:" + keywords + "*^30" + " | "
      + "label:" + keywords + "*^100 " + " | "
      + "description:" + keywords + "*^100 " + " | "
      + "pageContent:" + keywords + "*^30"
    )).highlight().limit(50).search()
    SearchResult(results.results.map {
      result =>
        val highlighting = result.highlighting(lucene.page.breadcrumb).map(frg => HighlightedFragment(frg.fragment, frg.word)) +:
          result.highlighting(lucene.page.label).map(frg => HighlightedFragment(frg.fragment, frg.word)) +:
          result.highlighting(lucene.page.description).map(frg => HighlightedFragment(frg.fragment, frg.word)) +:
          result.highlighting(lucene.page.pageContent).map(frg => HighlightedFragment(frg.fragment, frg.word)) +: Nil
        SearchResultItem(PageIndexDocument(result(lucene.page.id).value,
          result(lucene.page.hierarchy),
          result(lucene.page.path),
          result(lucene.page.breadcrumb),
          result(lucene.page.project),
          result(lucene.page.branch),
          result(lucene.page.label),
          result(lucene.page.description),
          result(lucene.page.pageContent)), highlighting.flatten)
    })
  }

  def reset(): Unit = {
    lucene.deleteAll()
  }

}

class SearchService @Inject()(pageIndex: IndexService) {

  def searchForPage(keywords: String): SearchResult = {
    pageIndex.query(keywords)
  }

}
