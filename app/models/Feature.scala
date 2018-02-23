package models

import julienrf.json.derived
import play.api.libs.json._

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
  implicit val stepFormat = Json.format[Step]
  implicit val examplesFormat = Json.format[Examples]
  implicit val scenarioFormat = derived.flat.oformat[ScenarioDefinition]((__ \ "keyword").format[String])
  implicit val featureFormat = Json.format[Feature]

  val abstractionLevels = Set("level_0_high_level", "level_1_specification", "level_2_technical_details")
  val caseTypes = Set("nominal_case", "limit_case", "error_case")
  val workflowSteps = Set("draft", "ready", "ongoing", "valid")
}