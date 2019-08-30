package repository

import anorm._
import anorm.SqlParser.scalar
import models._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.test.Injecting

class PageRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {
  val db = inject[Database]
  val pageRepository = inject[PageRepository]

  val page1 = Page(1, "app", "page1", "description1", 0, Some("appMarkdown"), "team1>project_id1>branch1>", "branch1", 1)
  val page2 = Page(2, "front", "page2", "description2", 1, Some("frontMarkdown"), "", "project_id4>branch3>", 1)
  val page3 = Page(3, "back", "page3", "description3", 2, Some("backMarkdown"), "", "project_id2>branch1>", 3)
  val pages = Seq(page1, page2, page3)

  override def beforeEach(): Unit = {
    db.withConnection { implicit connection =>
      pages.foreach { page =>
        SQL"""INSERT INTO page (id, name, label, description, `order`,markdown, relativePath, path, directoryId)
           VALUES (${page.id},${page.name},${page.label},${page.description},${page.order},${page.markdown},${page.relativePath},${page.path},${page.directoryId})"""
          .executeInsert()
      }
    }
  }

  override def afterEach(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE page".executeUpdate()
      SQL"ALTER TABLE page ALTER COLUMN id RESTART WITH 1".executeUpdate()
      ()
    }
  }

  "PageRepository" should {
    "count the number of pages" in {
      pageRepository.count() mustBe 3
    }

    "delete all pages" in {
      pageRepository.deleteAll()
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM page".as(scalar[Long].single) mustBe 0
      }
    }

    "delete a page" in {
      pageRepository.delete(page1)
    }

    "delete a page by id" in {
      pageRepository.deleteById(page1.id)
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM page WHERE id = ${page1.id}".as(scalar[Long].single) mustBe 0
      }
    }

    "find all by id" in {
      pageRepository.findAllById(pages.tail.map(_.id)) must contain theSameElementsAs pages.tail
    }

    "find a page by id" in {
      pageRepository.findById(page1.id) mustBe Some(page1)
    }

    "get all pages with content" in {
      pageRepository.findAllWithContent() must contain theSameElementsAs pages
    }

    "get all pages" in {
      pageRepository.findAll() must contain theSameElementsAs pages.map(_.copy(markdown = None))
    }

    "get all pages by projectId" in {
      pageRepository.findAllByDirectoryId(1) must contain theSameElementsAs Seq(page1, page2).map(_.copy(markdown = None))
    }

    "check if a page exist by id" in {
      pageRepository.existsById(page1.id) mustBe true
    }

    "save a page" in {
      val newPage = Page(-1, "assets", "page1", "description4", 3, Some("assetsMarkdown"), "", "project_id1>branch1>", 1)
      pageRepository.save(newPage) mustBe newPage.copy(id = 4)
    }

    "update a page" in {
      val updatedPage = page1.copy(description = "description11")
      pageRepository.save(updatedPage) mustBe updatedPage
      pageRepository.findAllWithContent() must contain theSameElementsAs Seq(updatedPage, page2, page3)
    }

    "save all pages by projectId" in {
      val page4 = Page(-1, "conf", "page4", "description5", 4, Some("confMarkdown"), "", "project_id2>branch1>", 3)
      val page5 = Page(-1, "test", "page5", "description6", 5, Some("testMarkdown"), "", "project_id3>branch1>", 3)
      val expectedPages = Seq(page4.copy(id = 4), page5.copy(id = 5))

      pageRepository.saveAll(Seq(page4, page5)) must contain theSameElementsAs expectedPages
      pageRepository.findAllWithContent() must contain theSameElementsAs pages ++ expectedPages
    }

    "get pages by path" in {
      pageRepository.findByPath(page1.path) mustBe Some(page1)
    }
  }
}
