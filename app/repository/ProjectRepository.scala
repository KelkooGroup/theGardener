package repository

import javax.inject.Inject

import anorm.SqlParser._
import anorm._
import models.Project
import play.api.db.Database

import scala.language.postfixOps


class ProjectRepository @Inject()(db: Database) {

  val parser: RowParser[Project] = {
    str("id") ~ str("name") ~ str("repositoryUrl") ~ str("stableBranch") ~ str("featuresRootPath") map {
      case id ~ name ~ repositoryUrl ~ stableBranch ~ featuresRootPath => Project(id, name, repositoryUrl, stableBranch, featuresRootPath)
    }
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM project".as(scalar[Long].single)
    }
  }

  def delete(project: Project): Unit = {
    deleteById(project.id)
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE project".executeUpdate()
    }
  }

  def deleteAll(projects: Seq[Project]): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM project WHERE id IN (${projects.map(_.id)})".executeUpdate()
    }
  }

  def deleteById(id: String): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM project WHERE id = $id".executeUpdate()
    }
  }

  def existsById(id: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM project WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def findAll(): Seq[Project] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project".as(parser *)
    }
  }

  def findAllById(ids: Seq[String]): Seq[Project] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project WHERE id IN ($ids)".as(parser *)
    }
  }

  def findById(id: String): Option[Project] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project WHERE id = $id".as(parser.singleOpt)
    }
  }

  def save(project: Project): Project = {
    db.withConnection { implicit connection =>
      SQL"""REPLACE INTO project (id, name, repositoryUrl, stableBranch,featuresRootPath)
           VALUES (${project.id}, ${project.name}, ${project.repositoryUrl},${project.stableBranch}, ${project.featuresRootPath})"""
         .executeUpdate()

      SQL"SELECT * FROM project WHERE id = ${project.id}".as(parser.single)
    }
  }

  def saveAll(projects: Seq[Project]): Seq[Project] = {
    projects.map(save)
  }
}
