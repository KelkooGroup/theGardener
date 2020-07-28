package services

import com.outr.lucene4s.{DirectLucene, parse}
import com.outr.lucene4s.query.Sort
import javax.inject.{Inject, Singleton}


case class PageIndexDocument(hierarchy: String, path: String, branch: String, label: String, description: String, pageContent: String)

@Singleton
class IndexService {

  val luceneSearchIndex = new DirectLucene(uniqueFields = List("path"), defaultFullTextSearchable = true)

  private val hierarchy = luceneSearchIndex.create.field[String]("hierarchy")
  private val path = luceneSearchIndex.create.field[String]("path", fullTextSearchable = false)
  private val branch = luceneSearchIndex.create.field[String]("branch")
  private val label = luceneSearchIndex.create.field[String]("label")
  private val description = luceneSearchIndex.create.field[String]("description")
  private val pageContent = luceneSearchIndex.create.field[String]("pageContent", sortable = false)

  def addDocument(document: PageIndexDocument): Unit = {
    luceneSearchIndex.doc().fields(hierarchy(document.hierarchy), description(document.description), label(document.label), this.path(document.path), branch(document.branch), pageContent(document.pageContent)).index()
  }

  def query(keywords: String): Seq[PageIndexDocument] = {
    val results = luceneSearchIndex.query().sort(Sort.Score).filter(parse("label:" + keywords + "*^100 | pageContent:" + keywords + "*^30 | description:" + keywords + "*^50 | branch:" + keywords + "*^30")).highlight().search()
    results.results.map {
      result => PageIndexDocument(result(hierarchy), result(path), result(branch), result(label), result(description), result(pageContent))
    }
  }

  def reset(): Unit = {
    luceneSearchIndex.deleteAll()
  }

}

class SearchService @Inject()(pageIndex: IndexService) {

  def searchForPage(keywords: String): Seq[PageIndexDocument] = {
    pageIndex.query(keywords)
  }

}
