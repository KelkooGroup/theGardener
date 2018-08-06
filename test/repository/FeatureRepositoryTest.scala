package repository

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

  val feature1 = Feature("1", "1", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), Some("keyword1"), "name1", "description1", Seq(), Seq("comments1"))
  val feature2 = Feature("2", "2", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), Some("keyword1"), "name1", "description1", Seq(), Seq("comments1"))
  val features = Seq(feature1)

  override def beforeEach() {
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
      SQL"TRUNCATE TABLE feature".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"TRUNCATE TABLE feature_tag".executeUpdate()
    }
  }

  "FeatureRepository" should {

    "get feature by id" in {
      featureRepository.findById(feature1.id) mustBe Some(feature1)
    }

    "get all" in {
      featureRepository.findAll() must contain theSameElementsAs features
    }

    "save a feature" in {
      val feature4 = Feature("4", "2", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), Some("keyword1"), "name1", "description1", Seq(), Seq("comments1"))
      featureRepository.save(feature4)
      featureRepository.findById(feature4.id) mustBe Some(feature4)
    }

    "save all" in {
      val newFeatures = Seq(Feature("8", "9", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), Some("keyword1"), "name1", "description1", Seq(), Seq("comments1")),
        Feature("9", "10", "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), Some("keyword1"), "name1", "description1", Seq(), Seq("comments1")))
      featureRepository.saveAll(newFeatures)
      featureRepository.findAll() must contain theSameElementsAs (feature1 +: newFeatures)
    }
  }
}
