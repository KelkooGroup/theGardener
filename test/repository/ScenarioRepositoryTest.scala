package repository

import anorm.SqlParser.scalar
import anorm._
import models._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.libs.json.Json
import play.api.test.Injecting

class ScenarioRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {
  val db = inject[Database]
  val scenarioRepository = inject[ScenarioRepository]

  val argument1 = Seq(Seq("argument1"))
  val steps = Step(1, "keyword1", "text1", argument1)
  val step = Seq(steps)
  val background1 = Background(1, "keyword1", "name1", "description1", Seq(steps))
  val scenarios1 = Background(1, "keyword1", "name1", "description1", Seq(steps))

  val scenario1 = Scenario(1, "1", Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", step)
  val scenario2 = Scenario(2, "1", Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", step)
  val scenario = Seq(scenario1, scenario2)

  val feature1 = Feature("1", "1", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), "keyword1", "name1", "description1", Seq(), Seq("comments1"))
  val features = Seq(feature1)

  override def beforeEach(): Unit = {
    db.withConnection { implicit connection =>
      scenario.foreach { scenario =>
        SQL"""REPLACE INTO scenario(id, description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId)
              VALUES(${scenario.id}, ${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, ${scenario.featureId})"""
          .executeUpdate()
        scenario.tags.foreach { tag =>
          SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
          SQL"REPLACE INTO scenario_tag(scenarioId, name) VALUES(${scenario.id}, $tag)".executeUpdate()
        }
      }
    }
  }

  override def afterEach() {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE scenario".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"TRUNCATE TABLE scenario_tag".executeUpdate()
    }
  }

  "ScenarioRepository" should {
    "count the number of scenarios" in {
      scenarioRepository.count() mustBe 2
    }

    "get all" in {
      scenarioRepository.findAll() must contain theSameElementsAs scenario
    }

    "find by id" in {
      scenarioRepository.findById(scenario1.id) mustBe Some(scenario1)
    }

    "find all by id" in {
      scenarioRepository.findAllById(scenario.tail.map(_.id)) must contain theSameElementsAs scenario.tail
    }

    "delete all scenarios" in {
      scenarioRepository.deleteAll()
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM scenario".as(scalar[Long].single) mustBe 0
      }
    }

    "delete a scenario by id" in {
      scenarioRepository.deleteById(scenario1.id)
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM branch WHERE id = ${scenario1.id}".as(scalar[Long].single) mustBe 0
      }
    }

    "delete a scenario" in {
      scenarioRepository.delete(scenario1)
    }

    "exists a scenario by id" in {
      scenarioRepository.existsById(scenario1.id) mustBe true
    }

    "save a scenario" in {
      val scenario3 = Scenario(3, "1", Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", step)
      scenarioRepository.save(scenario3)
      scenarioRepository.findById(scenario3.id) mustBe Some(scenario3)
    }

    "save all scenarios" in {
      val newScenario = Seq(Scenario(5, "1", Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", step),
        Scenario(6, "1", Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", step))
      scenarioRepository.saveAll(newScenario)
      scenarioRepository.findAll() must contain theSameElementsAs (scenario1 +: scenario2 +: newScenario)
    }
  }
}
