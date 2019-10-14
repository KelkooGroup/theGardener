package repositories

import anorm.SqlParser.scalar
import anorm._
import models.Feature.backgroundFormat
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
  val argument1 = Seq(Seq("key1", "val1"), Seq("key2", "val2"))
  val step1 = Step(1, "keyword1", "text1", argument1)
  val step2 = Step(2, "keyword2", "text2", argument1)
  val steps = Seq(step1, step2)
  val background1 = Background(1, "keyword1", "name1", "description1", Seq(step1, step2))

  val feature1 = Feature(1, 1, "path1", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language1"), "keyword1", "name1", "description1", Seq(), Seq("comments1"))
  val feature2 = Feature(2, 1, "path2", Some(background1), Seq("tag1", "tag2"), Some("language2"), "keyword2", "name2", "description2", Seq(), Seq("comments2"))
  val features = Seq(feature1, feature2)

  override def beforeEach(): Unit = {
    db.withConnection { implicit connection =>
      features.foreach { feature =>
        SQL"""INSERT INTO feature(branchId, path, backgroundAsJson, language, keyword, name, description, comments)
          VALUES(${feature.branchId}, ${feature.path}, ${Json.toJson(feature.background).toString()}, ${feature.language.map(_.toString)}, ${feature.keyword},  ${feature.name}, ${feature.description}, ${feature.comments.mkString("\n")})"""
          .executeInsert()
        feature.tags.foreach { tag =>
          SQL"REPLACE INTO tag(name) VALUES ($tag)".executeUpdate()
          SQL"REPLACE INTO feature_tag(featureId, name) VALUES(${feature.id}, $tag)".executeUpdate()
        }
      }
    }
  }

  override def afterEach(): Unit = {
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE feature".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"TRUNCATE TABLE feature_tag".executeUpdate()
      SQL"ALTER TABLE feature ALTER COLUMN id RESTART WITH 1".executeUpdate()
      ()
    }
  }

  "FeatureRepository" should {

    "count the number of features" in {
      featureRepository.count() mustBe 2
    }

    "get feature by id" in {
      featureRepository.findById(feature1.id) mustBe Some(feature1)
    }

    "get feature by branchId and path" in {
      featureRepository.findByBranchIdAndPath(1, "path1") mustBe Some(feature1)
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
      val feature3 = Feature(-1, 1, "path3", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language3"), "keyword3", "name3", "description3", Seq(), Seq("comments3"))
      featureRepository.save(feature3)
      featureRepository.findById(3) mustBe Some(feature3.copy(id = 3))
    }

    "save all" in {
      val feature3 = Feature(-1, 1, "path3", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language3"), "keyword3", "name3", "description3", Seq(), Seq("comments3"))
      val feature4 = Feature(-1, 1, "path4", Some(background1), Seq("tag1", "tag2", "tag3"), Some("language4"), "keyword4", "name4", "description4", Seq(), Seq("comments4"))
      val newFeatures = Seq(feature3, feature4)
      featureRepository.saveAll(newFeatures)
      featureRepository.findAll() must contain theSameElementsAs (features :+ feature3.copy(id = 3) :+ feature4.copy(id = 4))
    }
  }
}
