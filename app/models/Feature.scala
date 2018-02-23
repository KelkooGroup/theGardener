package models

import models.Feature.Example
import play.api.libs.json.Json

case class Feature(name: String, description: String, background: Background, scenarios: Seq[Scenario], scenarioOutlines: Seq[ScenarioOutline], branch: String)

case class Background(givenSteps: Seq[Given])

case class Scenario(tags: Seq[String], name: String, givenSteps: Seq[Given], whenSteps: Seq[When], thenSteps: Seq[Then])
case class ScenarioOutline(tags: Seq[String], name: String, givenSteps: Seq[Given], whenSteps: Seq[When], thenSteps: Seq[Then], examples: Seq[Example])

sealed trait Step
case class Given(name: String) extends Step
case class When(name: String) extends Step
case class Then(name: String) extends Step

object Feature {
  type Example = Map[String, String]

  implicit val givenFormat = Json.format[Given]
  implicit val whenFormat = Json.format[When]
  implicit val thenFormat = Json.format[Then]

  implicit val backgroundFormat = Json.format[Background]
  implicit val scenarioFormat = Json.format[Scenario]
  implicit val scenarioOutlineFormat = Json.format[ScenarioOutline]

  implicit val featureFormat = Json.format[Feature]
}