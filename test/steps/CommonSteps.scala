package steps

import controllers.MyDataSource
import cucumber.api.scala.{EN, ScalaDsl}
import cucumber.api.{DataTable, PendingException}
import models._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.collection.JavaConverters._
import scala.concurrent.Future

object CommonSteps extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar {
  var mockDataSource: MyDataSource = mock[MyDataSource]
  var response: Future[Result] = _

  // Override app if you need an Application with other than default parameters.
  implicit override lazy val app = new GuiceApplicationBuilder().overrides(bind[MyDataSource].toInstance(mockDataSource)).build()

  var serverSettings: Seq[ServerSettings] = _
  var projectSettings: Seq[ComponentSettings] = _

}

class CommonSteps extends PlaySpec with ScalaDsl with EN with MockitoSugar {
  import CommonSteps._

  Given("""^the projects server settings$""") { (dataTable: DataTable) =>
    serverSettings = dataTable.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala).map { line =>
       ServerSettings(line("id"), line("type"), line("root_path"), line("local_copy_root_path"))
    }
  }

  Given("""^the project settings$""") { (dataTable: DataTable) =>
    projectSettings = dataTable.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala).map { line =>
      ComponentSettings(line("id"), line("name"), serverSettings.find(_.id == line("id_host")).get, line("project_path"), line("stable_branch"), line("features_root_path"))
    }
  }

  Given("""^my project is setup in theGardener$""") { () =>
    //TODO: Setup project with data in serverSettings and projectSettings
  }

  When("""^I perform a GET on following URL "([^"]*)"$""") { (url: String) =>
    response = route(app, FakeRequest(GET, url)).get
  }

  Then("""^I get a response with status "([^"]*)"$""") { (expectedStatus: String) =>
    status(response) mustBe expectedStatus.toInt
  }

  Then("""^I get the following json response body$""") { (expectedJson: DataTable) =>
    contentAsString(response) mustBe expectedJson.asMaps(classOf[String], classOf[String]).asScala.head.get("json")
  }
}
