package repository

import anorm._
import anorm.SqlParser.{int, long, scalar, str}
import javax.inject.Inject
import models.Page
import play.api.db.Database

class PageRepository @Inject()(db: Database) {


  private val parser = for {
    pageId <- long("pageId")
    name <- str("name")
    label <- str("label")
    description <- str("description")
    order <- int("order")
    markdown <- str("markdown")
    relativePath <- str("relativePath")
    path <- str("path")
    directoryId <- long("directoryId")
  } yield Page(pageId, name, label, description, order, markdown, relativePath, path, directoryId)

  def findAll(): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page".as(parser.*)
    }
  }

  def findAllByBranchId(directoryId: Long): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE directoryId = $directoryId".as(parser.*)
    }
  }

  def save(page: Page): Page = {
    db.withConnection { implicit connection =>
      val pageId: Option[Long] = if (existsById(page.pageId)) {
        SQL"""REPLACE INTO page (pageId, name, label, description, `order`,markdown
              , relativePath, path, directoryId)
           VALUES (
          ${page.pageId},
          ${page.name},
          ${page.label},
          ${page.description},
          ${page.order},
          ${page.markdown},
          ${page.relativePath},
          ${page.path},
          ${page.directoryId})"""
          .executeUpdate()

        Some(page.pageId)

      } else {
        findByBranchIdAndName(page.directoryId, page.name) match {
          case Some(existingPage) =>
            SQL"""REPLACE INTO page (pageId, name, label, description, `order`,markdown
              , relativePath, path, directoryId)
           VALUES (
          ${page.pageId},
          ${page.name},
          ${page.label},
          ${page.description},
          ${page.order},
          ${page.markdown},
          ${page.relativePath},
          ${page.path},
          ${page.directoryId})"""
              .executeUpdate()

            Some(existingPage.pageId)


          case None => SQL"""INSERT INTO page ( name, label, description, `order`,markdown
              , relativePath, path, directoryId)
           VALUES (
          ${page.name},
          ${page.label},
          ${page.description},
          ${page.order},
          ${page.markdown},
          ${page.relativePath},
          ${page.path},
          ${page.directoryId})"""
            .executeInsert()
        }
      }

      SQL"SELECT * FROM page WHERE pageId = $pageId".as(parser.single)
    }
  }

  def saveAll(pages: Seq[Page]): Seq[Page] = {
    pages.map(save)
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM page".as(scalar[Long].single)
    }
  }

  def deleteById(id: Long): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM page WHERE pageId = $id".executeUpdate()
    }
  }

  def deleteAll(pages: Seq[Page]): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM page WHERE pageId IN (${pages.map(_.pageId)})".executeUpdate()
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE page".executeUpdate()
    }
  }

  def delete(page: Page): Unit = {
    deleteById(page.pageId)
  }

  def findAllById(ids: Seq[Long]): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE pageId IN ($ids)".as(parser.*)
    }
  }


  def findById(id: Long): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE pageId = $id".as(parser.singleOpt)
    }
  }

  def findByBranchIdAndName(directoryId: Long, name: String): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE directoryId = $directoryId AND name = $name".as(parser.singleOpt)
    }
  }

  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM page WHERE pageId = $id".as(scalar[Long].single) > 0
    }
  }
}
