package services

import com.outr.lucene4s.{DirectLucene, parse}
import com.outr.lucene4s.query.Sort
import javax.inject.{Inject, Singleton}
import models.PageJoinProject
import repositories.HierarchyRepository


case class PageIndexDocument(hierarchy: String, path: String, branch: String, label: String, description: String, pageContent: String)

@Singleton
class PageIndex {

  val luceneSearchIndex = new DirectLucene(uniqueFields = List(), defaultFullTextSearchable = true)

  val hierarchy = luceneSearchIndex.create.field[String]("hierarchy")
  val path = luceneSearchIndex.create.field[String]("path", fullTextSearchable = false)
  val branch = luceneSearchIndex.create.field[String]("branch")
  val label = luceneSearchIndex.create.field[String]("label")
  val description = luceneSearchIndex.create.field[String]("description")
  val pageContent = luceneSearchIndex.create.field[String]("pageContent", sortable = false)

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

class SearchService @Inject()(pageIndex: PageIndex, hierarchyRepository: HierarchyRepository) {

  def searchForPage(keywords: String): Seq[PageIndexDocument] = {
    pageIndex.query(keywords)
  }

  def getHierarchyPath(pageJoinProject: PageJoinProject): String = {
    val projectHierarchies = hierarchyRepository.findAllByProjectId(pageJoinProject.project.id)
    if (projectHierarchies.nonEmpty) {
      val hierarchyId = projectHierarchies.head.id
      val splitedId = hierarchyId.split('.')
      var hierarchyPath = ""
      var currentHierarchyId = "."
      for (id <- splitedId) {
        if (id != "") {
          currentHierarchyId += id + "."
          hierarchyPath += "/" + hierarchyRepository.findById(currentHierarchyId).map(_.name).getOrElse("Not Found")
        }
      }
      hierarchyPath + "/" + pageJoinProject.branch.name + pageJoinProject.page.relativePath
    } else {
      pageJoinProject.page.path
    }
  }
}
