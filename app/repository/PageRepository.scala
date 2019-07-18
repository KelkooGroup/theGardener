package repository

import anorm._
import anorm.SqlParser.{int, long, scalar, str}
import javax.inject.Inject
import models.Page
import play.api.db.Database

class PageRepository @Inject()(db: Database) {


  private val parser = for {
    id <- long("id")
    name <- str("name")
    label <- str("label")
    description <- str("description")
    order <- int("order")
    markdown <- str("markdown")
    relativePath <- str("relativePath")
    path <- str("path")
    directoryId <- long("directoryId")
  } yield Page(id, name, label, description, order, markdown, relativePath, path, directoryId)

  def findAll(): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page".as(parser.*)
    }
  }

  def findAllByDirectoryId(directoryId: Long): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE directoryId = $directoryId".as(parser.*)
    }
  }

  def save(page: Page): Page = {
    db.withConnection { implicit connection =>
      val id: Option[Long] = if (existsById(page.id) || existsByDirectoryIdAndName(page.directoryId, page.name)) {
        SQL"""REPLACE INTO page (id, name, label, description, `order`,markdown, relativePath, path, directoryId)
           VALUES (${page.id},${page.name},${page.label},${page.description},${page.order},${page.markdown},${page.relativePath},${page.path},${page.directoryId})"""
          .executeUpdate()

        Some(page.id)

      } else {
        SQL"""INSERT INTO page (name, label, description, `order`, markdown, relativePath, path, directoryId)
           VALUES (${page.name},${page.label},${page.description},${page.order},${page.markdown},${page.relativePath},${page.path},${page.directoryId})"""
          .executeInsert()
      }
      SQL"SELECT * FROM page WHERE id = $id".as(parser.single)
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
      SQL"DELETE FROM page WHERE id = $id".executeUpdate()
    }
  }

  def deleteAll(pages: Seq[Page]): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM page WHERE id IN (${pages.map(_.id)})".executeUpdate()
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE page".executeUpdate()
    }
  }

  def delete(page: Page): Unit = {
    deleteById(page.id)
  }

  def findAllById(ids: Seq[Long]): Seq[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE id IN ($ids)".as(parser.*)
    }
  }


  def findById(id: Long): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE id = $id".as(parser.singleOpt)
    }
  }

  def findByDirectoryIdAndName(directoryId: Long, name: String): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE directoryId = $directoryId AND name = $name".as(parser.singleOpt)
    }
  }

  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM page WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def findByPath(path: String): Option[Page] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM page WHERE path = $path".as(parser.singleOpt)
    }
  }

  def existsByDirectoryIdAndName(directoryId: Long, name: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM page WHERE directoryId = $directoryId AND name = $name".as(scalar[Long].single) > 0
    }
  }
}
