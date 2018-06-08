package repository

import javax.inject.Inject
import models.Project
import play.api.db.Database
import anorm._
import anorm.SqlParser.get


class ProjectRepository @Inject()(db: Database) {

  val parser: RowParser[Project] = {
    get[String]("id") ~ get[String]("name") ~ get[String]("repositoryUrl") ~ get[String]("stableBranch") ~ get[String]("featuresRootPath") map { case id ~ name ~ repositoryUrl ~ stableBranch ~ featuresRootPath => Project(id, name, repositoryUrl, stableBranch, featuresRootPath)
    }
  }

  def insertOne(project: Project)(implicit db: Database): Unit = {
    db.withConnection { implicit connection =>
      SQL(
        s"""INSERT INTO project (id, name, repositoryUrl, stableBranch,featuresRootPath) VALUES ({id}, {name}, {repositoryUrl},{stableBranch}, {featuresRootPath})""").on('id -> project.id, 'name -> project.name, 'repositoryUrl -> project.repositoryUrl, 'stableBranch -> project.stableBranch, 'featuresRootPath -> project.featuresRootPath).executeInsert()
    }
  }


  def getOneById(id: String): Option[Project] = {
    db.withConnection { implicit connection =>
      SQL("SELECT * FROM project WHERE id = {id}").on('id -> id).as(parser.singleOpt)
    }
  }

  def getAll()(implicit db: Database): Seq[Project] = {
    db.withConnection { implicit connection =>
      SQL("SELECT * FROM project").as(parser *)
    }
  }
}
