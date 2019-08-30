package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import play.api.db.Database

class TagRepository @Inject()(db: Database) {
  private val parser = scalar[String]

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM tag".as(scalar[Long].single)
    }
  }

  private def deleteIfEmpty(tag: String) = {
    db.withConnection { implicit connection =>
      val featureTagCount = SQL"SELECT COUNT(*) FROM feature_tag WHERE name = $tag".as(scalar[Long].single)
      val scenarioTagCount = SQL"SELECT COUNT(*) FROM scenario_tag WHERE name = $tag".as(scalar[Long].single)

      if (featureTagCount == 0 && scenarioTagCount == 0) {
        SQL"DELETE FROM tag WHERE name = $tag".executeUpdate()
      }
    }
  }

  def deleteByFeatureId(featureId: Long, tags: Seq[String]): Unit = {
    db.withConnection { implicit connection =>
      tags.foreach { tag =>
        SQL"DELETE FROM feature_tag WHERE featureId = $featureId AND name = $tag".executeUpdate()
        deleteIfEmpty(tag)
      }
    }
  }

  def deleteAllByFeatureId(featureId: Long): Unit = deleteByFeatureId(featureId, findAllByFeatureId(featureId))

  def deleteAllByScenarioId(scenarioId: Long): Unit = deleteByScenarioId(scenarioId, findAllByScenarioId(scenarioId))

  def deleteByScenarioId(scenarioId: Long, tags: Seq[String]): Unit = {
    db.withConnection { implicit connection =>
      tags.foreach { tag =>
        SQL"DELETE FROM scenario_tag WHERE scenarioId = $scenarioId AND name = $tag".executeUpdate()
        deleteIfEmpty(tag)
      }
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"TRUNCATE TABLE feature_tag".executeUpdate()
      SQL"TRUNCATE TABLE scenario_tag".executeUpdate()
      ()
    }
  }

  def deleteAllFeatureTag(): Unit = {
    db.withConnection { implicit connection =>
      val tags = findAll()
      SQL"TRUNCATE TABLE feature_tag".executeUpdate()
      tags.foreach(deleteIfEmpty)
    }
  }

  def deleteAllScenarioTag(): Unit = {
    db.withConnection { implicit connection =>
      val tags = findAll()
      SQL"TRUNCATE TABLE scenario_tag".executeUpdate()
      tags.foreach(deleteIfEmpty)
    }
  }

  def findAll(): Seq[String] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM tag".as(parser.*)
    }
  }

  def findAllByFeatureId(featureId: Long): Seq[String] = {
    db.withConnection { implicit connection =>
      SQL"SELECT name FROM feature_tag WHERE featureId = $featureId".as(parser.*)
    }
  }

  def findAllByScenarioId(scenarioId: Long): Seq[String] = {
    db.withConnection { implicit connection =>
      SQL"SELECT name FROM scenario_tag WHERE scenarioId = $scenarioId".as(parser.*)
    }
  }

  def saveAllByFeatureId(featureId: Long, tags: Seq[String]): Seq[String] = {
    deleteAllByFeatureId(featureId)

    db.withConnection { implicit connection =>
      tags.foreach { tag =>
        SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
        SQL"REPLACE INTO feature_tag(featureId, name) VALUES($featureId, $tag)".executeUpdate()
      }
    }

    findAllByFeatureId(featureId)
  }


  def saveAllByScenarioId(scenarioId: Long, tags: Seq[String]): Seq[String] = {
    deleteAllByScenarioId(scenarioId)

    db.withConnection { implicit connection =>
      tags.foreach { tag =>
        SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
        SQL"REPLACE INTO scenario_tag(scenarioId, name) VALUES($scenarioId, $tag)".executeUpdate()
      }
    }

    findAllByScenarioId(scenarioId)
  }
}
