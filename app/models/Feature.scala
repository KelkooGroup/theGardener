package models

case class Feature(id: String, branch: String, tags: Seq[String], language: String, keyword: String, name: String, description: String, scenarios: Seq[ScenarioDefinition], comments: Seq[String] = Seq())

sealed trait ScenarioDefinition {
  val id: Int
  val keyword: String
  val name: String
  val description: String
  val steps: Seq[Step]
}

case class Background(id: Int, keyword: String, name: String, description: String, steps: Seq[Step]) extends ScenarioDefinition

case class Scenario(id: Int, tags: Seq[String], abstractionLevel: String, caseType: String, workflowStep: String, keyword: String, name: String, description: String, steps: Seq[Step]) extends ScenarioDefinition

case class ScenarioOutline(id: Int, tags: Seq[String], abstractionLevel: String, caseType: String, workflowStep: String, keyword: String, name: String, description: String, steps: Seq[Step], examples: Seq[Examples]) extends ScenarioDefinition

case class Examples(id: Int, tags: Seq[String], keyword: String, description: String, tableHeader: Seq[String], tableBody: Seq[Seq[String]])

case class Step(id: Int, keyword: String, text: String, argument: Seq[Seq[String]])

object Feature {
  val abstractionLevels = Map("level_0_high_level" -> Set("level0", "l0"), "level_1_specification" -> Set("level1", "l1"), "level_2_technical_details" -> Set("level2", "l2"))
  val caseTypes = Map("nominal_case" -> "nominal", "limit_case" -> "limit", "error_case" -> "error")
  val workflowSteps = Set("draft", "ready", "ongoing", "valid")
}