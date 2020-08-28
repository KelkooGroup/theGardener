package repositories

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
    childrenLabel <- str("childrenLabel")
    childLabel <- str("childLabel")
    directoryPath <- get[String]("directoryPath").?
    shortcut <- get[String]("shortcut").?
  } yield HierarchyNode(id, slugName, name, childrenLabel, childLabel, directoryPath, shortcut)

  def save(hierarchy: HierarchyNode): HierarchyNode = {
    db.withConnection { implicit connection =>
      SQL"REPLACE INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath, shortcut) VALUES (${hierarchy.id}, ${hierarchy.slugName}, ${hierarchy.name}, ${hierarchy.childrenLabel}, ${hierarchy.childLabel}, ${hierarchy.directoryPath}, ${hierarchy.shortcut})".executeUpdate()
      SQL"SELECT * FROM hierarchyNode WHERE id = ${hierarchy.id}".as(parser.single)
    }
  }

  def existsById(id: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM hierarchyNode WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE hierarchyNode".executeUpdate()
      ()
    }
  }

  def findAll(): Seq[HierarchyNode] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM hierarchyNode".as(parser.*)
    }
  }

  def saveAll(hierarchies: Seq[HierarchyNode]): Seq[HierarchyNode] = {
    hierarchies.map(save)
  }

  def deleteById(id: String): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM hierarchyNode WHERE id = $id".executeUpdate()
      ()
    }
  }

  def findById(id: String): Option[HierarchyNode] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM hierarchyNode WHERE id = $id".as(parser.*).headOption
    }
  }

  def findBySlugName(slugName: String): Option[HierarchyNode] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM hierarchyNode WHERE slugName = $slugName".as(parser.*).headOption
    }
  }

  def findByDirectoryPath(directoryPath: String): Option[HierarchyNode] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM hierarchyNode WHERE directoryPath = $directoryPath".as(parser.*).headOption
    }
  }

  def findAllByProjectId(projectId: String): Seq[HierarchyNode] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM project_hierarchyNode INNER JOIN hierarchyNode on (id = hierarchyId) WHERE projectId = $projectId".as(parser.*)
    }
  }
}


