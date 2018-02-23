package steps

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.{DataTable, PendingException}
import org.scalatest.MustMatchers
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, PlaySpec}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import steps.CommonSteps._


class ShowFeaturesSteps extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with ScalaDsl with EN with MustMatchers with MockitoSugar {

  Given("""^the file "([^"]*)"$""") { (path: String, content: String) =>
    val fullPath = Paths.get("target/" + path)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }

  When("""^a user access to the feature "([^"]*)" of the project "([^"]*)"$""") { (feature: String, project: String) =>
    go to s"http://localhost:$port/feature/$project/$feature"
  }

  Then("""^the following feature is displayed$""") { (dataTable: DataTable) =>
    dataTable.asScala.map { feature =>
      pageSource must include("Feature_" + feature("id"))
      pageSource must include(feature("name"))
      pageSource must include(feature("description"))
      pageSource must include("Project:" + feature("project"))
    }
  }


  Then("""^the following scenarios are displayed$""") { (dataTable: DataTable) =>
    dataTable.asScala.map { scenario =>
      pageSource must include(scenario("id"))
      pageSource must include(scenario("scenario"))
      pageSource must include(scenario("scenario_type"))
      pageSource must include(scenario("abstraction_level"))
      pageSource must include(scenario("case_type"))
      pageSource must include(scenario("workflow_step"))
    }
  }


  Then("""^the scenario "([^"]*)" is displayed$""") { (id: String, dataTable: DataTable) =>
    dataTable.asScala.map { scenario =>
      pageSource must include(scenario("id"))
      pageSource must include(scenario("step"))
      pageSource must include(scenario("type"))
      pageSource must include(scenario("value"))
    }
  }

}
