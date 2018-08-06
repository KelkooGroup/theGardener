package models

import play.api.libs.json._

case class Feature(id: String, branchId: String, path: String, background: Option[Background], tags: Seq[String] = Seq(), language: Option[String] = None, keyword: Option[String] = None, name: String, description: String, scenarios: Seq[ScenarioDefinition] = Seq(), comments: Seq[String] = Seq())


sealed trait ScenarioDefinition {
  val id: Int
  val keyword: String
  val name: String
  val description: String
  val steps: Seq[Step]
}

object ScenarioDefinition {
  implicit val format: Format[ScenarioDefinition] = Json.format[ScenarioDefinition]
}


object Examples {
  implicit val format: Format[Examples] = Json.format[Examples]
}


object ScenarioOutline {
  implicit val format: Format[ScenarioOutline] = Json.format[ScenarioOutline]
}


object Scenario {
  implicit val format: Format[Scenario] = Json.format[Scenario]
}


object Background {

  implicit val format: Format[Background] = Json.format[Background]
}


object Step {
  implicit val format: Format[Step] = Json.format[Step]
}


case class Background(id: Int, keyword: String, name: String, description: String, steps: Seq[Step]) extends ScenarioDefinition

case class Scenario(id: Int, featureId: String, tags: Seq[String], abstractionLevel: String, caseType: String, workflowStep: String, keyword: String, name: String, description: String, steps: Seq[Step]) extends ScenarioDefinition

case class ScenarioOutline(id: Int, tags: Seq[String], abstractionLevel: String, caseType: String, workflowStep: String, keyword: String, name: String, description: String, steps: Seq[Step], examples: Seq[Examples]) extends ScenarioDefinition

case class Examples(id: Int, tags: Seq[String], keyword: String, description: String, tableHeader: Seq[String], tableBody: Seq[Seq[String]])

case class Step(id: Int, keyword: String, text: String, argument: Seq[Seq[String]])

object Feature {
  val abstractionLevels = Map("level_0_high_level" -> Set("level0", "l0"), "level_1_specification" -> Set("level1", "l1"), "level_2_technical_details" -> Set("level2", "l2"))
  val caseTypes = Map("nominal_case" -> "nominal", "limit_case" -> "limit", "error_case" -> "error")
  val workflowSteps = Set("draft", "ready", "ongoing", "valid")
}