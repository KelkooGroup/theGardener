package repository

import anorm._
import models._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.libs.json.Json
import play.api.test.Injecting

class TagRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {
  val db = inject[Database]
  val scenarioRepository = inject[ScenarioRepository]
  val featureRepository = inject[FeatureRepository]
  val tagRepository = inject[TagRepository]

  val argument1 = Seq(Seq("argument1"))
  val steps = Step(1, "keyword1", "text1", argument1)
  val step = Seq(steps)
  val background1 = Background(1, "keyword1", "name1", "description1", Seq(steps))
  val scenarios1 = Background(1, "keyword1", "name1", "description1", Seq(steps))

  val scenario1 = Scenario(1, "1", Seq("tag1"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", step)
  val scenario2 = Scenario(2, "1", Seq("tag1", "tag2", "tag3"), "abstractionLevel1", "caseType1", "workflowStep1", "keyword1", "name1", "description1", step)
  val scenario = Seq(scenario1)

  val feature1 = Feature("1", "1", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), Some("keyword1"), "name1", "description1", Seq(), Seq("comments1"))
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

    db.withConnection { implicit connection =>
      features.foreach { feature =>
        SQL"""REPLACE INTO feature(id, branchId, path, backgroundAsJson, language, keyword, name, description, comments)
          VALUES(${feature.id}, ${feature.branchId}, ${feature.path}, ${Json.toJson(feature.background).toString()}, ${feature.language.map(_.toString)}, ${feature.keyword.map(_.toString)},  ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"""
          .executeUpdate()
        feature.tags.foreach { tag =>
          SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
          SQL"REPLACE INTO feature_tag(featureId, name) VALUES(${feature.id}, $tag)".executeUpdate()
        }
      }
    }
  }

  override def afterEach() {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE scenario".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"TRUNCATE TABLE feature".executeUpdate()
    }
  }
}
