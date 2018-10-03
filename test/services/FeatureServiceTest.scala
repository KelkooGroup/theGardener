package services

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito._
import org.scalatest._
import repository.FeatureRepository

class FeatureServiceTest extends WordSpec with MustMatchers with MockitoSugar {

  val featureRepository = mock[FeatureRepository]

  "FeatureService" should {
    "parse a feature file" in {
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      val feature = new FeatureService(featureRepository).parseFeatureFile("test", 1, "test/features/show_features/show_a_feature.feature")

      feature.name mustBe "Show a feature"
      feature.description must include("So that my project feature is shared with all users")
      feature.comments mustBe Seq()
    }
  }
}
