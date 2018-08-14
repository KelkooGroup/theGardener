package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database


class BranchRepository @Inject()(db: Database) {


  private val parser = for {
    id <- int("id")
    name <- str("name")
    isStable <- bool("isStable")
    projectId <- str("projectId")

  } yield Branch(id, name, isStable, projectId)

  def findAll(): Seq[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch".as(parser.*)
    }
  }

  def findAllByProjectId(projectId: String): Seq[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch WHERE projectId = $projectId".as(parser.*)
    }
  }

  def save(branch: Branch): Option[Branch] = {
    db.withConnection { implicit connection =>
      if (existsById(branch.id)) {
        SQL"""REPLACE INTO branch (id, name, isStable, projectId)
           VALUES (${branch.id}, ${branch.name}, ${branch.isStable}, ${branch.projectId})"""
          .executeUpdate()
      }
      else {
        SQL"""INSERT INTO branch (id, name, isStable, projectId)
           VALUES (${branch.id}, ${branch.name}, ${branch.isStable}, ${branch.projectId})"""
          .executeInsert()
      }
      SQL"SELECT * FROM branch WHERE id = ${branch.id}".as(parser.singleOpt)
    }
  }

  def existsByProjectId(id: Int, projectId: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM branch WHERE id = $id AND projectId = $projectId".as(scalar[Long].single) > 0
    }
  }

  def saveAll(branches: Seq[Branch]): Seq[Option[Branch]] = {
    branches.map(save)
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM branch".as(scalar[Long].single)
    }
  }

  def deleteById(id: Int): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM branch WHERE id = $id".executeUpdate()
    }
  }

  def deleteAll(branches: Seq[Branch]): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM branch WHERE id IN (${branches.map(_.id)})".executeUpdate()
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE branch".executeUpdate()
    }
  }

  def delete(branch: Branch): Unit = {
    deleteById(branch.id)
  }

  def findAllById(ids: Seq[Int]): Seq[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch WHERE id IN ($ids)".as(parser.*)
    }
  }

  def findById(id: Int): Option[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch WHERE id = $id".as(parser.singleOpt)
    }
  }

  def existsById(id: Int): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM branch WHERE id = $id".as(scalar[Long].single) > 0
    }
  }
}