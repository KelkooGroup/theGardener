package services

import org.scalatest.{MustMatchers, WordSpec}

class FeatureServiceTest extends WordSpec with MustMatchers {

  "FeatureService" should {
    "parse a feature file" in {
      val feature = new FeatureService().parseFeatureFile("test", "test/features/show_features/show_a_feature.feature")

      feature.name mustBe "Show a feature"
      feature.description must include("So that my project feature is shared with all users")
      feature.comments mustBe Seq()
    }
  }
}
