package repository

import anorm._
import anorm.SqlParser.{bool, int, long, scalar, str}
import javax.inject.Inject
import models._
import play.api.db.Database

class DirectoryRepository @Inject()(db: Database) {


  private val parser = for {
    directoryId <- long("directoryId")
    name <- str("name")
    label <- str("label")
    description <- str("description")
    order <- int("order")
    relativePath <- str("relativePath")
    path <- str("path")
    branchId <- long("branchId")
  } yield Directory(directoryId, name, label, description, order, relativePath, path, branchId)

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
      val directoryId: Option[Long] = if (existsById(directory.directoryId)) {
        SQL"""REPLACE INTO directory (directoryId, name, label, description, `order`, relativePath, path, branchId)
           VALUES (
          ${directory.directoryId},
          ${directory.name},
          ${directory.label},
          ${directory.description},
          ${directory.order},
          ${directory.relativePath},
          ${directory.path},
          ${directory.branchId})"""
          .executeUpdate()

        Some(directory.directoryId)

      } else {
        findByBranchIdAndName(directory.branchId, directory.name) match {
          case Some(existingDirectory) =>
            SQL"""REPLACE INTO directory (directoryId, name, label, description, `order`, relativePath, path, branchId)
                                            VALUES (
          ${directory.directoryId},
          ${directory.name},
          ${directory.label},
          ${directory.description},
          ${directory.order},
          ${directory.relativePath},
          ${directory.path},
          ${directory.branchId})"""
          .executeUpdate()

            Some(existingDirectory.directoryId)


          case None => SQL"""INSERT INTO directory (name, label, description, `order`, relativePath, path, branchId)
           VALUES (
          ${directory.name},
          ${directory.label},
          ${directory.description},
          ${directory.order},
          ${directory.relativePath},
          ${directory.path},
          ${directory.branchId})"""
            .executeInsert()
        }
      }

      SQL"SELECT * FROM directory WHERE directoryId = $directoryId".as(parser.single)
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
      SQL"DELETE FROM directory WHERE directoryId = $id".executeUpdate()
    }
  }

  def deleteAll(directories: Seq[Directory]): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM directory WHERE directoryId IN (${directories.map(_.directoryId)})".executeUpdate()
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE directory".executeUpdate()
    }
  }

  def delete(directory: Directory): Unit = {
    deleteById(directory.directoryId)
  }

  def findAllById(ids: Seq[Long]): Seq[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE directoryId IN ($ids)".as(parser.*)
    }
  }


  def findById(id: Long): Option[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE directoryId = $id".as(parser.singleOpt)
    }
  }

  def findByBranchIdAndName(branchId: Long, name: String): Option[Directory] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM directory WHERE branchId = $branchId AND name = $name".as(parser.singleOpt)
    }
  }

  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM directory WHERE directoryId = $id".as(scalar[Long].single) > 0
    }
  }
}
