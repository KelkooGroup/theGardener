package steps

import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.scalatest.mockito.MockitoSugar
import play.api.test.Helpers._
import play.api.test._


class ShowFeaturesSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  When("""^a user access to this feature in theGardener$""") { () =>
    response = route(app, FakeRequest(GET, s"/feature/suggestionsWS/provide_book_suggestions.feature")).get
  }

  When("""^a user access to the feature "([^"]*)" of the project "([^"]*)"$""") { (feature: String, project: String) =>
    response = route(app, FakeRequest(GET, s"/feature/$project/$feature")).get
  }

  Then("""^this feature is displayed properly$""") { () =>
    val id = "suggestionsWS_master_provide_book_suggestions_feature"

    contentAsString(response) must include(id)

    val featureText = browser.parseString(contentAsString(response)) >> text("#" + id)

    featureText must include("Provide some book suggestions")
    featureText must include("As a user")
    featureText must include("I want some book suggestions")
    featureText must include("So that I can do some discovery")
    featureText must include("Project: Suggestions WebServices")
  }

  Then("""^the following feature is displayed$""") { (dataTable: DataTable) =>
    dataTable.asScala.map { feature =>
      val id = feature("id").replace("/", "_").replace(".", "_")

      contentAsString(response) must include(id)

      val featureText = browser.parseString(contentAsString(response).replace("<br/>", " ")) >> text("#" + id)

      featureText must include(feature("name"))
      featureText must include(feature("description"))
      featureText must include("Project: " + feature("project"))
    }
  }

  Then("""^the following scenarios are displayed$""") { (dataTable: DataTable) =>
    dataTable.asScala.map { scenario =>
      val scenarioText = browser.parseString(contentAsString(response)) >> text("#Scenario_" + scenario("id"))

      if (scenario.contains("scenario")) scenarioText must include(scenario("scenario"))
      if (scenario.contains("scenario_type")) scenarioText must include(scenario("scenario_type"))
      scenarioText must include(scenario("abstraction_level"))
      scenarioText must include(scenario("case_type"))
      scenarioText must include(scenario("workflow_step"))
    }
  }

  Then("""^the scenario "([^"]*)" is displayed$""") { (id: String, dataTable: DataTable) =>
    dataTable.asScala.map { step =>
      val stepText = browser.parseString(contentAsString(response)) >> "#Scenario_" + id >> text("#Step_" + step("id"))

      stepText must include(step("step"))
      stepText must include(step("value"))
    }
  }
}
