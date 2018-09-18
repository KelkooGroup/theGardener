package models

import julienrf.json.derived
import play.api.libs.json._

case class Feature(id: Long, branchId: Long, path: String, background: Option[Background], tags: Seq[String] = Seq(), language: Option[String] = None, keyword: String, name: String, description: String, scenarios: Seq[ScenarioDefinition] = Seq(), comments: Seq[String] = Seq())

sealed trait ScenarioDefinition {
  val id: Long
  val keyword: String
  val name: String
  val description: String
  val steps: Seq[Step]
}

case class Background(id: Long, keyword: String, name: String, description: String, steps: Seq[Step]) extends ScenarioDefinition

case class Scenario(id: Long, tags: Seq[String], abstractionLevel: String, caseType: String, workflowStep: String, keyword: String, name: String, description: String, steps: Seq[Step]) extends ScenarioDefinition

case class ScenarioOutline(id: Long, tags: Seq[String], abstractionLevel: String, caseType: String, workflowStep: String, keyword: String, name: String, description: String, steps: Seq[Step], examples: Seq[Examples]) extends ScenarioDefinition

case class Examples(id: Long, tags: Seq[String], keyword: String, description: String, tableHeader: Seq[String], tableBody: Seq[Seq[String]])

case class Step(id: Long, keyword: String, text: String, argument: Seq[Seq[String]])

object Feature {
  implicit val stepFormat = Json.format[Step]
  implicit val examplesFormat = Json.format[Examples]
  implicit val scenarioFormat = derived.flat.oformat[ScenarioDefinition]((__ \ "keyword").format[String])
  implicit val backgroundFormat = Json.format[Background]
  implicit val featureFormat = Json.format[Feature]

  val abstractionLevels = Map("level_0_high_level" -> Set("level0", "l0"), "level_1_specification" -> Set("level1", "l1"), "level_2_technical_details" -> Set("level2", "l2"))
  val caseTypes = Map("nominal_case" -> "nominal", "limit_case" -> "limit", "error_case" -> "error")
  val workflowSteps = Set("draft", "ready", "ongoing", "valid")

}