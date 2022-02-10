package services

import com.typesafe.config.ConfigFactory
import models.{Branch, Scenario, ScenarioOutline}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito._
import repositories.FeatureRepository

class FeatureServiceTest extends AnyWordSpec with Matchers with MockitoSugar {

  val featureRepository = mock[FeatureRepository]

  "FeatureService" should {
    "parse a feature file with only scenario" in {
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      val branch = Branch(1, "master", isStable = true, "test")
      val feature = new FeatureService(ConfigFactory.load(), featureRepository).parseFeatureFile("test", branch, "test/features/documentation/gherkin/show_scenarios.feature").get

      feature.name mustBe "Generate documentation"
      feature.description must include("As a user,")
      feature.comments mustBe Seq()

      feature.background.isDefined mustBe true
      feature.background.flatMap(_.steps.headOption).map(_.text) mustBe Some("the database is empty")
      feature.background.flatMap(_.steps.lastOption).map(_.argument) mustBe Some(Seq(Seq("projectId", "hierarchyId"), Seq("suggestionsWS", ".01.")))

      val firstScenario = feature.scenarios.find(_.name.startsWith("generate documentation")).map(_.asInstanceOf[Scenario])
      firstScenario.map(_.tags) mustBe Some(Seq("level_2_technical_details", "nominal_case", "valid"))
      firstScenario.flatMap(_.steps.headOption).flatMap(_.argument.headOption.flatMap(_.headOption)).getOrElse("") must include("Feature: As a user Tim,")
      firstScenario.flatMap(_.steps.lastOption).flatMap(_.argumentTextType) mustBe Some("json")
    }
  }

  "parse a feature file with scenario outline" in {
    when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

    val branch = Branch(1, "master", isStable = true, "test")
    val feature = new FeatureService(ConfigFactory.load(), featureRepository).parseFeatureFile("test", branch, "test/features/synchronization/store_features.feature").get
    val scenario = feature.scenarios.find(_.name.startsWith("show the different")).map(_.asInstanceOf[ScenarioOutline])
    scenario.flatMap(_.examples.headOption).map(_.tableHeader) mustBe Some(Seq("annotation1", "annotation2", "annotation3", "considered_abstraction_level", "considered_case_type", "considered_workflow_step"))

  }

}
