package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database
import play.api.libs.json.Json

class FeatureRepository @Inject()(db: Database) {


  val parser = for {
    id <- str("id")
    branchId <- str("branchId")
    path <- str("path")
    backgroundAsJson <- get[Option[String]]("backgroundAsJson")
    language <- get[Option[String]]("language")
    keyword <- get[Option[String]]("keyword")
    name <- str("name")
    description <- str("description")
    comments <- str("comments")
  } yield Feature(id, branchId, path, backgroundAsJson.map(Json.parse(_).as[Background]), Seq(), language, keyword, name, description, Seq(), Seq(comments))

  def findAll(): Seq[Feature] = {
    db.withConnection { implicit connection =>
      val feature = SQL"SELECT * FROM feature".as(parser.*)
      feature.map(f => f.copy(tags = SQL"SELECT name FROM feature_tag WHERE featureId = ${f.id}".as(scalar[String].*)))
    }
  }

  def save(feature: Feature): Option[Feature] = {
    db.withConnection { implicit connection =>
      SQL"REPLACE INTO feature(id, branchId, path, backgroundAsJson, language, keyword, name, description, comments) VALUES ( ${feature.id}, ${feature.branchId}, ${feature.path}, ${feature.background.map(Json.toJson(_).toString())}, ${feature.language.map(_.toString)}, ${feature.keyword.map(_.toString)},  ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"
        .executeUpdate()

      feature.tags.foreach { tag =>
        SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
        SQL"REPLACE INTO feature_tag(featureId, name) VALUES(${feature.id}, $tag)".executeUpdate()
      }
      val features = SQL"SELECT * FROM feature WHERE id = ${feature.id} ".as(parser.singleOpt)
      features.map(_.copy(tags = SQL"SELECT name FROM feature_tag WHERE featureId = ${feature.id}".as(scalar[String].*)))
    }
  }

  def findById(featureId: String): Option[Feature] = {
    db.withConnection { implicit connection =>
      val feature = SQL"SELECT * FROM feature WHERE id = $featureId".as(parser.singleOpt)
      feature.map(_.copy(tags = SQL"SELECT name FROM feature_tag WHERE featureId = $featureId".as(scalar[String].*)))
    }
  }

  def saveAll(features: Seq[Feature]): Seq[Option[Feature]] = {
    features.map(save)
  }
}
