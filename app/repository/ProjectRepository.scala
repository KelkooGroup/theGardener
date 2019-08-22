package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database

class ProjectRepository @Inject()(db: Database) {

  private val parser = for {
    id <- str("id")
    name <- str("name")
    repositoryUrl <- str("repositoryUrl")
    stableBranch <- str("stableBranch")
    displayedBranches <- str("displayedBranches").?
    featuresRootPath <- str("featuresRootPath").?
    documentationRootPath <- str("documentationRootPath").?
    variables <- str("variables").?
  } yield Project(id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath,variables)

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
      SQL"DELETE FROM project_hierarchyNode WHERE projectId = $id".executeUpdate()
    }
  }

  def existsById(id: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM project WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def findAll(): Seq[Project] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project".as(parser.*)
    }
  }

  def findAllById(ids: Seq[String]): Seq[Project] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project WHERE id IN ($ids)".as(parser.*)
    }
  }

  def findById(id: String): Option[Project] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project WHERE id = $id".as(parser.singleOpt)
    }
  }

  def save(project: Project): Project = {
    db.withConnection { implicit connection =>
      SQL"""REPLACE INTO project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath)
           VALUES (${project.id}, ${project.name}, ${project.repositoryUrl}, ${project.stableBranch}, ${project.displayedBranches}, ${project.featuresRootPath}, ${project.documentationRootPath})"""
        .executeUpdate()

      SQL"SELECT * FROM project WHERE id = ${project.id}".as(parser.single)
    }
  }

  def saveAll(projects: Seq[Project]): Seq[Project] = {
    projects.map(save)
  }

  def findAllByHierarchyId(hierarchyId: String): Seq[Project] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project_hierarchyNode LEFT OUTER JOIN project ON (projectId = id) WHERE hierarchyId = $hierarchyId".as(parser.*)
    }
  }

  def linkHierarchy(projectId: String, hierarchyId: String): String = {
    db.withConnection { implicit connection =>
      SQL"INSERT INTO project_hierarchyNode (projectId, hierarchyId) VALUES ($projectId, $hierarchyId)".executeInsert()
    }
    hierarchyId
  }

  def unlinkHierarchy(projectId: String, hierarchyId: String): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM project_hierarchyNode WHERE projectId = $projectId AND hierarchyId = $hierarchyId".executeUpdate()
    }
  }

  def existsLinkByIds(projectId: String, hierarchyId: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM project_hierarchyNode WHERE projectId = $projectId AND hierarchyId = $hierarchyId".as(scalar[Long].single) > 0
    }
  }
}
