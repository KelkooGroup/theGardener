package services

import com.typesafe.config.{Config, ConfigFactory}
import models.Branch
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest._
import org.scalatest.mockito._
import repository.FeatureRepository

class FeatureServiceTest extends WordSpec with MustMatchers with MockitoSugar {

  val featureRepository = mock[FeatureRepository]

  "FeatureService" should {
    "parse a feature file" in {
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      val branch = Branch(1, "master", isStable = true, "test")
      val feature = new FeatureService(ConfigFactory.load(), featureRepository).parseFeatureFile("test", branch, "test/features/documentation/gerkin/show_scenarios.feature").get

      feature.name mustBe "Generate documentation"
      feature.description must include("As a user,")
      feature.comments mustBe Seq()
    }
  }
}
