package services

import com.typesafe.config.ConfigFactory
import models.Branch
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito._
import repositories.FeatureRepository

class FeatureServiceTest extends AnyWordSpec with Matchers with MockitoSugar {

  val featureRepository = mock[FeatureRepository]

  "FeatureService" should {
    "parse a feature file" in {
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      val branch = Branch(1, "master", isStable = true, "test")
      val feature = new FeatureService(ConfigFactory.load(), featureRepository).parseFeatureFile("test", branch, "test/features/documentation/gherkin/show_scenarios.feature").get

      feature.name mustBe "Generate documentation"
      feature.description must include("As a user,")
      feature.comments mustBe Seq()
    }
  }
}
