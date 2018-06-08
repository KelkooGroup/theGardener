package services

import org.scalatest.{MustMatchers, WordSpec}

class ProjectServiceTest extends WordSpec with MustMatchers {

  "ComponentService" should {
    "parse a feature file" in {
      val feature = new ProjectService().parseFeatureFile("test", "test/features/show_features/show_a_feature.feature")

      feature.name mustBe "Show a feature"
      feature.description must include("So that my project feature is shared with all users")
      feature.comments mustBe Seq()
    }
  }
}
