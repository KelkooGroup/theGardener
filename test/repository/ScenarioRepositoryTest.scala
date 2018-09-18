package repository

import anorm.SqlParser.scalar
import anorm._
import models._
import models.Feature.stepFormat
import models.Feature.examplesFormat
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.libs.json.Json
import play.api.test.Injecting

class ScenarioRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {
  val db = inject[Database]
  val scenarioRepository = inject[ScenarioRepository]

  val steps = Seq(Step(1, "keyword1", "text1", Seq(Seq("argument1"))))

  val scenario1 = Scenario(1, Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", steps)
  val examples = Seq(Examples(1, Seq(), "keyword1", "description1", Seq("header1"), Seq(Seq("body1, body2"))),
    Examples(2, Seq(), "keyword2", "description2", Seq("header2"), Seq(Seq("body3, body4"))))
  val scenario2 = ScenarioOutline(2, Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", steps, examples)
  val scenarios = Seq(scenario1, scenario2)

  override def beforeEach(): Unit = {
    db.withConnection { implicit connection =>
      scenarios.foreach {
        case scenario: Scenario =>
        val featureId = scenario.id
        SQL"""INSERT INTO scenario(description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId)
              VALUES(${scenario.description}, ${scenario.workflowStep}, ${scenario.caseType}, ${scenario.abstractionLevel}, ${Json.toJson(scenario.steps).toString()}, ${scenario.keyword}, ${scenario.name}, $featureId)"""
          .executeInsert()
        scenario.tags.foreach { tag =>
          SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
          SQL"REPLACE INTO scenario_tag(scenarioId, name) VALUES(${scenario.id}, $tag)".executeUpdate()
        }
        case scenarioOutline: ScenarioOutline =>
          val featureId = scenarioOutline.id
          SQL"""INSERT INTO scenario(description, workflowStep, caseType, abstractionLevel, stepsAsJson, keyword, name, featureId, examplesAsJson)
              VALUES(${scenarioOutline.description}, ${scenarioOutline.workflowStep}, ${scenarioOutline.caseType}, ${scenarioOutline.abstractionLevel}, ${Json.toJson(scenarioOutline.steps).toString()}, ${scenarioOutline.keyword}, ${scenarioOutline.name}, $featureId, ${Json.toJson(scenarioOutline.examples).toString()})"""
            .executeInsert()
          scenarioOutline.tags.foreach { tag =>
            SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
            SQL"REPLACE INTO scenario_tag(scenarioId, name) VALUES(${scenarioOutline.id}, $tag)".executeUpdate()
          }
      }
    }
  }

  override def afterEach() {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE scenario".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"TRUNCATE TABLE scenario_tag".executeUpdate()
      SQL"ALTER TABLE scenario ALTER COLUMN id RESTART WITH 1".executeUpdate()
    }
  }

  "ScenarioRepository" should {
    "count the number of scenarios" in {
      scenarioRepository.count() mustBe 2
    }

    "delete a scenario" in {
      scenarioRepository.delete(scenario1)
      db.withConnection { implicit connection =>
        scenarioRepository.findAll() must contain theSameElementsAs Seq(scenario2)
      }
    }

    "delete all scenarios" in {
      scenarioRepository.deleteAll()
      db.withConnection { implicit connection =>
        scenarioRepository.findAll() mustBe Seq()
      }
    }

    "delete all scenarios by feature id" in {
      scenarioRepository.deleteAllByFeatureId(1)
      db.withConnection { implicit connection =>
        scenarioRepository.findAll() must contain theSameElementsAs Seq(scenario2)
      }
    }

    "delete a scenario by id" in {
      scenarioRepository.deleteById(scenario1.id)
      db.withConnection { implicit connection =>
        scenarioRepository.findAll() must contain theSameElementsAs Seq(scenario2)
      }
    }

    "check if a scenario exist by id" in {
      scenarioRepository.existsById(scenario1.id) mustBe true
    }

    "find all scenarios" in {
      scenarioRepository.findAll() must contain theSameElementsAs scenarios
    }

    "find all scenarios by id" in {
      scenarioRepository.findAllById(scenarios.tail.map(_.id)) must contain theSameElementsAs scenarios.tail
    }

    "find by id" in {
      scenarioRepository.findById(scenario1.id) mustBe Some(scenario1)
    }

    "save a new scenario" in {
      val scenario3 = Scenario(-1, Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", steps)
      scenarioRepository.save(1, scenario3)
      scenarioRepository.findAll() must contain theSameElementsAs (scenarios :+ scenario3.copy(id = 3))
    }

    "save new scenarios" in {
      val scenario3 = Scenario(-1, Seq("tag1", "tag2", "tag3"), "abstractionLevel3", "caseType3", "workflowStep3", "keyword3", "name3", "description3", steps)
      val scenario4 = ScenarioOutline(-1, Seq("tag1", "tag2", "tag3"), "abstractionLevel4", "caseType4", "workflowStep4", "keyword4", "name4", "description4", steps, examples)
      scenarioRepository.saveAll(1, Seq(scenario3, scenario4))
      scenarioRepository.findAll() must contain theSameElementsAs (scenarios :+ scenario3.copy(id = 3) :+ scenario4.copy(id = 4))
    }
  }
}
