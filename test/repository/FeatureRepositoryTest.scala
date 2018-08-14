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

class FeatureRepositoryTest extends PlaySpec with GuiceOneServerPerSuite with Injecting with BeforeAndAfterEach {

  val db = inject[Database]
  val featureRepository = inject[FeatureRepository]
  val argument1 = Seq(Seq("argument1"))
  val steps = Step(1, "keyword1", "text1", argument1)
  val background1 = Background(1, "keyword1", "name1", "description1", Seq(steps))
  val scenarios1 = Background(1, "keyword1", "name1", "description1", Seq(steps))

  val feature1 = Feature("1", "1", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), "keyword1", "name1", "description1", Seq(), Seq("comments1"))
  val features = Seq(feature1)

  override def beforeEach() {
    db.withConnection { implicit connection =>
      features.foreach { feature =>
        SQL"""REPLACE INTO feature(id, branchId, path, backgroundAsJson, language, keyword, name, description, comments)
          VALUES(${feature.id}, ${feature.branchId}, ${feature.path}, ${Json.toJson(feature.background).toString()}, ${feature.language.map(_.toString)}, ${feature.keyword},  ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"""
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
      SQL"TRUNCATE TABLE feature".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"TRUNCATE TABLE feature_tag".executeUpdate()
    }
  }

  "FeatureRepository" should {

    "count the number of features" in {
      featureRepository.count() mustBe 1
    }

    "get feature by id" in {
      featureRepository.findById(feature1.id) mustBe Some(feature1)
    }

    "get all" in {
      featureRepository.findAll() must contain theSameElementsAs features
    }

    "check if a branch exits by id" in {
      featureRepository.existsById(feature1.id) mustBe true
    }

    "delete a feature by id" in {
      featureRepository.deleteById(feature1.id)
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM feature WHERE id = ${feature1.id}".as(scalar[Long].single) mustBe 0
      }
    }

    "delete a feature" in {
      featureRepository.delete(feature1)
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM project WHERE id = ${feature1.id}".as(scalar[Long].single) mustBe 0
      }
    }

    "delete all features" in {
      featureRepository.deleteAll()
      db.withConnection { implicit connection =>
        SQL"SELECT COUNT(*) FROM feature".as(scalar[Long].single) mustBe 0
      }
    }


    "save a feature" in {
      val feature4 = Feature("4", "2", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), "keyword1", "name1", "description1", Seq(), Seq("comments1"))
      featureRepository.save(feature4)
      featureRepository.findById(feature4.id) mustBe Some(feature4)
    }

    "save all" in {
      val newFeatures = Seq(Feature("8", "9", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), "keyword1", "name1", "description1", Seq(), Seq("comments1")),
        Feature("9", "10", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), "keyword1", "name1", "description1", Seq(), Seq("comments1")))
      featureRepository.saveAll(newFeatures)
      featureRepository.findAll() must contain theSameElementsAs (feature1 +: newFeatures)
    }
  }
}