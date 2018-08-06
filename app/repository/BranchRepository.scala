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

  def findAllBranch(): Seq[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch".as(parser.*)
    }
  }

  def findAllBranchByProjectId(projectId: String): Seq[Branch] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM branch WHERE projectId = $projectId".as(parser.*)
    }
  }

  def save(branch: Branch): Branch = {
    db.withConnection { implicit connection =>
      SQL"""REPLACE INTO branch (id, name, isStable, projectId)
           VALUES (${branch.id}, ${branch.name}, ${branch.isStable}, ${branch.projectId})"""
        .executeUpdate()
      SQL"SELECT * FROM branch WHERE id = ${branch.id}".as(parser.single)
    }
  }

  def findBranchByProjectId(branch: Int, projectId: String): Seq[Branch] = {
    db.withConnection { implicit connection =>
      SQL" SELECT * FROM branch WHERE id = $branch AND projectId = $projectId".as(parser.*)
    }
  }

  def existsByProjectId(branch: Int, projectId: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM branch WHERE id = $branch AND projectId = $projectId".as(scalar[Long].single) > 0
    }
  }

  def saveAll(branches: Seq[Branch]): Seq[Branch] = {
    branches.map(save)
  }
}

