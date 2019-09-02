package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models.Feature.{examplesFormat, stepFormat}
import models._
import play.api.db.Database
import play.api.libs.json.Json

class ScenarioRepository @Inject()(db: Database, tagRepository: TagRepository) {

  val parser = for {
    id <- long("id")
    abstractionLevel <- str("abstractionLevel")
    caseType <- str("caseType")
    workflowStep <- str("workflowStep")
    keyword <- str("keyword")
    name <- str("name")
    description <- str("description")
    stepsAsJson <- str("stepsAsJson")
    examplesAsJson <- get[Option[String]]("examplesAsJson")
  } yield if (examplesAsJson.isEmpty) Scenario(id, tagRepository.findAllByScenarioId(id), abstractionLevel, caseType, workflowStep, keyword, name, description, Json.parse(stepsAsJson).as[Seq[Step]])
  else ScenarioOutline(id, tagRepository.findAllByScenarioId(id), abstractionLevel, caseType, workflowStep, keyword, name, description, Json.parse(stepsAsJson).as[Seq[Step]], examplesAsJson.map(Json.parse(_).as[Seq[Examples]]).getOrElse(Seq()))

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM scenario".as(scalar[Long].single)
    }
  }

  def delete(scenario: ScenarioDefinition): Unit = {
    deleteById(scenario.id)
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      tagRepository.deleteAllScenarioTag()
      SQL"TRUNCATE TABLE scenario".executeUpdate()
      ()
    }
  }

  def deleteById(scenarioId: Long): Unit = {
    db.withConnection { implicit connection =>
      tagRepository.deleteAllByScenarioId(scenarioId)
      SQL"DELETE FROM scenario WHERE id = $scenarioId".executeUpdate()
      ()
    }
  }

  def deleteAllByFeatureId(featureId: Long): Unit = {
    findAllByFeatureId(featureId).foreach(delete)
  }

  def existsById(id: Long): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM scenario WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def findAll(): Seq[ScenarioDefinition] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM scenario".as(parser.*)
    }
  }

  def findAllById(ids: Seq[Long]): Seq[ScenarioDefinition] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM scenario WHERE id IN ($ids)".as(parser.*)
    }
  }

  def findById(scenarioId: Long): Option[ScenarioDefinition] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM scenario WHERE id = $scenarioId".as(parser.*).headOption
    }
  }

  def findAllByFeatureId(featureId: Long): Seq[ScenarioDefinition] = {
    db.withConnection { implicit connection =>
      SQL"SELECT * FROM scenario WHERE featureId = $featureId".as(parser.*)
    }
  }

  def save(featureId: Long, scenario: ScenarioDefinition): Option[ScenarioDefinition] = {
    scenario match {
      case s: Scenario => saveScenario(featureId, s)
      case so: ScenarioOutline => saveScenarioOutline(featureId, so)
      case _ => None
    }
  }

  private def saveScenario(featureId: Long, scenario: Scenario): Option[ScenarioDefinition] = {

    db.withConnection { implicit connection =>
      val id: Option[Long] = if (existsById(scenario.id)) {
        SQL"""REPLACE INTO scenario(id, description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId)
              VALUES(${scenario.id}, ${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, $featureId)"""
          .executeUpdate()
        Some(scenario.id)

      } else {
        SQL"""INSERT INTO scenario(description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId)
              VALUES(${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, $featureId)"""
          .executeInsert()
      }

      id.map(tagRepository.saveAllByScenarioId(_, scenario.tags))

      SQL"SELECT * FROM scenario WHERE id = $id".as(parser.*).headOption
    }
  }

  private def saveScenarioOutline(featureId: Long, scenario: ScenarioOutline): Option[ScenarioDefinition] = {

    db.withConnection { implicit connection =>
      val id: Option[Long] = if (existsById(scenario.id)) {
        SQL"""REPLACE INTO scenario(id, description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId, examplesAsJson)
              VALUES(${scenario.id}, ${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, $featureId, ${Json.toJson(scenario.examples).toString()})"""
          .executeUpdate()
        Some(scenario.id)

      } else {
        SQL"""INSERT INTO scenario(description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId, examplesAsJson)
              VALUES(${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, $featureId, ${Json.toJson(scenario.examples).toString()})"""
          .executeInsert()
      }

      id.map(tagRepository.saveAllByScenarioId(_, scenario.tags))

      SQL"SELECT * FROM scenario WHERE id = $id".as(parser.*).headOption
    }
  }

  def saveAll(featureId: Long, scenarios: Seq[ScenarioDefinition]): Seq[ScenarioDefinition] = {
    scenarios.flatMap(save(featureId, _))
  }
}
