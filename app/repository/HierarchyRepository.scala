package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database

class HierarchyRepository @Inject()(db: Database) {
  private val parser = for {
    id <- str("id")
    slugName <- str("slugName")
    name <- str("name")
  } yield Hierarchy(id, slugName, name)


  def save(hierarchy: Hierarchy): Hierarchy = {
    db.withConnection { implicit connection =>
      SQL"""REPLACE INTO hierarchy (id, slugName, name)
           VALUES (${hierarchy.id}, ${hierarchy.slugName}, ${hierarchy.name})"""
        .executeUpdate()
      SQL"SELECT * FROM hierarchy WHERE id = ${hierarchy.id}".as(parser.single)
    }
  }

  def existsById(id: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM hierarchy WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE hierarchy".executeUpdate()
    }
  }
  def findAll(): Seq[Hierarchy] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM hierarchy".as(parser.*)
    }
  }

}


