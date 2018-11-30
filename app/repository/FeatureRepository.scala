package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models.Feature.backgroundFormat
import models._
import play.api.Logger
import play.api.db.Database
import play.api.libs.json.Json

class FeatureRepository @Inject()(db: Database, tagRepository: TagRepository, scenarioRepository: ScenarioRepository) {

  val parser = for {
    id <- long("id")
    branchId <- int("branchId")
    path <- str("path")
    backgroundAsJson <- get[Option[String]]("backgroundAsJson")
    language <- get[Option[String]]("language")
    keyword <- str("keyword")
    name <- str("name")
    description <- str("description")
    comments <- str("comments")
  } yield Feature(id, branchId, path, backgroundAsJson.map(Json.parse(_).as[Background]),
    tagRepository.findAllByFeatureId(id), language, keyword, name, description,
    scenarioRepository.findAllByFeatureId(id), comments.split("\n").filterNot(_.isEmpty))

  val parserFeaturePath = for {
    branchId <- long("branchId")
    path <- str("path")
  } yield FeaturePath(branchId, path)

  def findAll(): Seq[Feature] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM feature".as(parser.*)
        .map(feature => feature.copy(tags = SQL"SELECT name FROM feature_tag WHERE featureId = ${feature.id}".as(scalar[String].*)))
    }
  }

  def findAllFeaturePaths(): Seq[FeaturePath] = {
    db.withConnection { implicit connection =>
      SQL"SELECT branchId, path FROM feature".as(parserFeaturePath.*)
    }
  }


  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM feature WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def save(feature: Feature): Option[Feature] = {
    try {
      db.withConnection { implicit connection =>
        val idOpt: Option[Long] = if (existsById(feature.id)) {
          SQL"""REPLACE INTO feature(id, branchId, path, backgroundAsJson, language, keyword, name, description, comments)
             VALUES (${feature.id}, ${feature.branchId}, ${feature.path}, ${feature.background.map(Json.toJson(_).toString)}, ${feature.language}, ${feature.keyword}, ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"""
            .executeUpdate()
          Some(feature.id)

        } else {
          SQL"""INSERT INTO feature(branchId, path, backgroundAsJson, language, keyword, name, description, comments)
             VALUES (${feature.branchId}, ${feature.path}, ${feature.background.map(Json.toJson(_).toString())}, ${feature.language}, ${feature.keyword}, ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"""
            .executeInsert()
        }

        idOpt.foreach { id =>
          scenarioRepository.deleteAllByFeatureId(id)
          scenarioRepository.saveAll(id, feature.scenarios)

          tagRepository.deleteAllByFeatureId(id)
          tagRepository.saveAllByFeatureId(id, feature.tags)
        }

        SQL"SELECT * FROM feature WHERE id = $idOpt".as(parser.singleOpt)
      }
    } catch {
      case e: Exception => Logger.error(s"Error while saving feature ${feature.name}", e)
        None
    }
  }

  def findById(id: Long): Option[Feature] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM feature WHERE id = $id".as(parser.singleOpt)
    }
  }

  def findByBranchIdAndPath(branchId: Long, path: String): Option[Feature] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM feature WHERE branchId = $branchId AND path = $path".as(parser.singleOpt)
    }
  }

  def findAllByBranchId(branchId: Long): Seq[Feature] = {
    val s =  db.withConnection { implicit connection =>
      SQL"SELECT * FROM feature WHERE branchId = $branchId".as(parser.*)
    }
    s
  }

  def saveAll(features: Seq[Feature]): Seq[Option[Feature]] = {
    features.map(save)
  }

  def deleteById(id: Long): Unit = {
    db.withConnection { implicit connection =>
      scenarioRepository.deleteAllByFeatureId(id)
      tagRepository.deleteAllByFeatureId(id)
      SQL"DELETE FROM feature WHERE id = $id".executeUpdate()
    }
  }

  def delete(feature: Feature): Unit = {
    deleteById(feature.id)
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      scenarioRepository.deleteAll()
      tagRepository.deleteAll()
      SQL"TRUNCATE TABLE feature".executeUpdate()
    }
  }

  def deleteAllByBranchId(branchId: Long): Unit = {
    findAllByBranchId(branchId).foreach(delete)
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM feature".as(scalar[Long].single)
    }
  }
}
