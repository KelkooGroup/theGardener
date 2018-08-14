package repository

import anorm.SqlParser._
import anorm._
import javax.inject.Inject
import models._
import play.api.db.Database
import play.api.libs.json.Json

class ScenarioRepository @Inject()(db: Database) {


  val parser = for {
    id <- int("id")
    featureId <- int("featureId")
    abstractionLevel <- str("abstractionLevel")
    caseType <- str("caseType")
    workflowStep <- str("workflowStep")
    keyword <- str("keyword")
    name <- str("name")
    description <- str("description")
    stepsAsJson <- str("stepsAsJson")
  } yield Scenario(id, featureId.toString, Seq(), abstractionLevel, caseType, workflowStep, keyword, name, description, Json.parse(stepsAsJson).as[Seq[Step]])

  def findAll(): Seq[Scenario] = {
    db.withConnection { implicit connection =>
      val scenario = SQL"SELECT * FROM scenario".as(parser.*)
      scenario.map(s => s.copy(tags = SQL"SELECT name FROM scenario_tag where scenarioId = ${s.id}".as(scalar[String].*)))
    }
  }

  def existsById(id: Int): Boolean = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM scenario WHERE id = $id".as(scalar[Long].single) > 0
    }
  }

  def save(scenario: Scenario): Option[Scenario] = {

    db.withConnection { implicit connection =>
      if (existsById(scenario.id)) {
        SQL"""REPLACE INTO scenario(id, description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId)
              VALUES(${scenario.id}, ${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, ${scenario.featureId})"""
          .executeUpdate()
      } else {
        SQL"""INSERT INTO scenario(id, description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId)
              VALUES(${scenario.id}, ${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, ${scenario.featureId})"""
          .executeInsert()
      }
      scenario.tags.foreach { tag =>
        SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
        SQL"REPLACE INTO scenario_tag(scenarioId, name) VALUES (${scenario.id}, $tag)".executeUpdate()
      }
      val scenarios = SQL"SELECT * FROM scenario WHERE id = ${scenario.id} ".as(parser.singleOpt)
      scenarios.map(_.copy(tags = SQL"SELECT name FROM scenario_tag WHERE scenarioId = ${scenario.id}".as(scalar[String].*)))
    }
  }

  def saveAll(scenarios: Seq[Scenario]): Seq[Option[Scenario]] = {
    scenarios.map(save)
  }

  def findById(scenarioId: Int): Option[Scenario] = {
    db.withConnection { implicit connection =>
      val scenarios = SQL"SELECT * FROM scenario WHERE id = $scenarioId ".as(parser.singleOpt)
      scenarios.map(_.copy(tags = SQL"SELECT name FROM scenario_tag WHERE scenarioId = $scenarioId".as(scalar[String].*)))
    }
  }

  def count(): Long = {
    db.withConnection { implicit connection =>
      SQL"SELECT COUNT(*) FROM scenario".as(scalar[Long].single)
    }
  }

  def deleteAll(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE scenario".executeUpdate()
    }
  }

  def delete(scenario: Scenario): Unit = {
    deleteById(scenario.id)
  }

  def deleteById(scenarioId: Int): Unit = {
    db.withConnection { implicit connection =>
      SQL"DELETE FROM scenario WHERE id = $scenarioId".executeUpdate()
    }
  }

  def findAllById(ids: Seq[Int]): Seq[Scenario] = {
    db.withConnection { implicit connection =>
      val scenario = SQL"SELECT * FROM scenario WHERE id IN ($ids)".as(parser.*)
      scenario.map(s => s.copy(tags = SQL"SELECT name FROM scenario_tag where scenarioId = ${s.id}".as(scalar[String].*)))
    }
  }
}
