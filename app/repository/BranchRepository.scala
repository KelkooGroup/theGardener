package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database


class BranchRepository @Inject()(db: Database) {


  private val parser = for {
    id <- long("id")
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

  def save(branch: Branch): Branch = {
    db.withConnection { implicit connection =>
      val id: Option[Long] = if (existsById(branch.id)) {
        SQL"""REPLACE INTO branch (id, name, isStable, projectId)
           VALUES (${branch.id}, ${branch.name}, ${branch.isStable}, ${branch.projectId})"""
          .executeUpdate()

        Some(branch.id)

      } else {
        SQL"""INSERT INTO branch (name, isStable, projectId)
           VALUES (${branch.name}, ${branch.isStable}, ${branch.projectId})"""
          .executeInsert()
      }

      SQL"SELECT * FROM branch WHERE id = $id".as(parser.single)
    }
  }

  def saveAll(branches: Seq[Branch]): Seq[Branch] = {
    branches.map(save)
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM branch".as(scalar[Long].single)
    }
  }

  def deleteById(id: Long): Unit = {
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

  def findAllById(ids: Seq[Long]): Seq[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch WHERE id IN ($ids)".as(parser.*)
    }
  }

  def findById(id: Long): Option[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch WHERE id = $id".as(parser.singleOpt)
    }
  }

  def findByProjectIdAndName(projectId: String, name: String): Option[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch WHERE projectId = $projectId AND name = $name".as(parser.singleOpt)
    }
  }

  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM branch WHERE id = $id".as(scalar[Long].single) > 0
    }
  }
}