package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database
import play.api.libs.json.Json

class FeatureRepository @Inject()(db: Database) {


  val parser = for {
    id <- int("id")
    branchId <- int("branchId")
    path <- str("path")
    backgroundAsJson <- get[Option[String]]("backgroundAsJson")
    language <- get[Option[String]]("language")
    keyword <- str("keyword")
    name <- str("name")
    description <- str("description")
    comments <- str("comments")
  } yield Feature(id.toString, branchId.toString, path, backgroundAsJson.map(Json.parse(_).as[Background]), Seq(), language, keyword, name, description, Seq(), Seq(comments))

  def findAll(): Seq[Feature] = {
    db.withConnection { implicit connection =>
      val feature = SQL"SELECT * FROM feature".as(parser.*)
      feature.map(f => f.copy(tags = SQL"SELECT name FROM feature_tag WHERE featureId = ${f.id}".as(scalar[String].*)))
    }
  }

  def existsById(id: String): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM feature WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def save(feature: Feature): Option[Feature] = {
    db.withConnection { implicit connection =>
      if (existsById(feature.id)) {
        SQL"REPLACE INTO feature(id, branchId, path, backgroundAsJson, language, keyword, name, description, comments) VALUES ( ${feature.id}, ${feature.branchId}, ${feature.path}, ${feature.background.map(Json.toJson(_).toString())}, ${feature.language.map(_.toString)}, ${feature.keyword},  ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"
          .executeUpdate()
      } else {
        SQL"INSERT INTO feature(id, branchId, path, backgroundAsJson, language, keyword, name, description, comments) VALUES ( ${feature.id}, ${feature.branchId}, ${feature.path}, ${feature.background.map(Json.toJson(_).toString())}, ${feature.language.map(_.toString)}, ${feature.keyword},  ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"
          .executeInsert()
      }

      feature.tags.foreach { tag =>
        if (feature.tags.isEmpty) {
          SQL"INSERT INTO tag(name) VALUES ($tag)".executeInsert()
          SQL"INSERT INTO feature_tag(featureId, name) VALUES(${feature.id}, $tag)".executeInsert()
        } else {
          SQL"DELETE FROM tag".executeUpdate()
          SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
          SQL"REPLACE INTO feature_tag(featureId, name) VALUES(${feature.id}, $tag)".executeUpdate()
        }
      }
      val features = SQL"SELECT * FROM feature WHERE id = ${feature.id} ".as(parser.singleOpt)
      features.map(_.copy(tags = SQL"SELECT name FROM feature_tag WHERE featureId = ${feature.id}".as(scalar[String].*)))
    }
  }

  def findById(id: String): Option[Feature] = {
    db.withConnection { implicit connection =>
      val feature = SQL"SELECT * FROM feature WHERE id = $id".as(parser.singleOpt)
      feature.map(_.copy(tags = SQL"SELECT name FROM feature_tag WHERE featureId = $id".as(scalar[String].*)))
    }
  }

  def saveAll(features: Seq[Feature]): Seq[Option[Feature]] = {
    features.map(save)
  }

  def deleteById(id: String): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM feature WHERE id = $id".executeUpdate()
    }
  }

  def delete(feature: Feature): Unit = {
    deleteById(feature.id)
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE feature".executeUpdate()
    }
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM feature".as(scalar[Long].single)
    }
  }
}