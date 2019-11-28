package repositories

import anorm.SqlParser.{int, long, scalar, str}
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database

class DirectoryRepository @Inject()(db: Database, pageRepository: PageRepository) {



  private val parser = for {
    id <- long("id")
    name <- str("name")
    label <- str("label")
    description <- str("description")
    order <- int("order")
    relativePath <- str("relativePath")
    path <- str("path")
    branchId <- long("branchId")
  } yield Directory(id, name, label, description, order, relativePath, path, branchId)

  def findAll(): Seq[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory".as(parser.*)
    }
  }

  def findAllByBranchId(branchId: Long): Seq[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE branchId = $branchId".as(parser.*)
    }
  }

  def save(directory: Directory): Directory = {
    db.withConnection { implicit connection =>
      val id: Option[Long] = findById(directory.id).orElse(findByBranchIdAndRelativePath(directory.branchId, directory.relativePath)) match {
        case Some(existingDirectory) =>
          SQL"""REPLACE INTO directory (id, name, label, description, `order`, relativePath, path, branchId)
               VALUES (${existingDirectory.id},${directory.name},${directory.label},${directory.description},${directory.order},${directory.relativePath},${directory.path},${directory.branchId})"""
            .executeUpdate()

          Some(existingDirectory.id)

        case _ =>
          SQL"""INSERT INTO directory (name, label, description, `order`, relativePath, path, branchId)
               VALUES (${directory.name},${directory.label},${directory.description},${directory.order},${directory.relativePath},${directory.path},${directory.branchId})"""
            .executeInsert()
      }

      SQL"SELECT * FROM directory WHERE id = $id".as(parser.single)
    }
  }

  def saveAll(directories: Seq[Directory]): Seq[Directory] = {
    directories.map(save)
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM directory".as(scalar[Long].single)
    }
  }

  def deleteById(id: Long): Unit = {
    db.withConnection { implicit connection =>
      pageRepository.deleteAllByDirectoryId(id)
      SQL"DELETE FROM directory WHERE id = $id".executeUpdate()
      ()
    }
  }

  def deleteAll(directories: Seq[Directory]): Unit = {
    db.withConnection { implicit connection =>
      val ids = directories.map(_.id)
      ids.foreach(pageRepository.deleteAllByDirectoryId)
      if (ids.nonEmpty){
         SQL"DELETE FROM directory WHERE id IN ($ids)".executeUpdate()
      }
      ()
    }
  }

  def deleteAllByBranchId(branchId: Long): Unit = {
    deleteAll(findAllByBranchId(branchId))
  }


  def deleteAllByStartingPath(basePath: String): Unit = {
    db.withConnection { implicit connection =>
      SQL(s"DELETE FROM directory WHERE path like '$basePath%' ") .executeUpdate()
      ()
    }
  }


  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      pageRepository.deleteAll()
      SQL"TRUNCATE TABLE directory".executeUpdate()
      ()
    }
  }

  def delete(directory: Directory): Unit = {
    deleteById(directory.id)
  }

  def findAllById(ids: Seq[Long]): Seq[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE id IN ($ids)".as(parser.*)
    }
  }


  def findById(id: Long): Option[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE id = $id".as(parser.*).headOption
    }
  }

  def findByPath(path: String): Option[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE path = $path  limit 1".as(parser.*).headOption
    }
  }

  def findByBranchIdAndRelativePath(branchId: Long, path: String): Option[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE branchId = $branchId AND relativePath = $path".as(parser.*).headOption
    }
  }

  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM directory WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def existsByBranchIdAndName(branchId: Long, name: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM directory WHERE branchId = $branchId AND name = $name".as(scalar[Long].single) > 0
    }
  }
}
