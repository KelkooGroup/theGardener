package steps

import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
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
import scala.collection.mutable
import scala.concurrent.Future

object CommonSteps extends PlaySpec with GuiceOneAppPerSuite with MockitoSugar {

  var response: Future[Result] = _

  var components: Map[String, Project] = _

  implicit class DataTableAsScala(dataTable: DataTable) {
    def asScala: Seq[Map[String, String]] = dataTable.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala.toMap)
  }
}

class CommonSteps extends PlaySpec with ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^the project settings are setup in theGardener$""") { (dataTable: DataTable) =>
    components = dataTable.asScala.map { line =>
      val id = line("id")
      (id, Project(id, line.getOrElse("group", ""), line.getOrElse("system", ""), line("name"), line("repository_url"), line("stable_branch"), line("features_root_path")))
    }.toMap
  }

  When("""^I perform a GET on following URL "([^"]*)"$""") { (url: String) =>
    response = route(app, FakeRequest(GET, url)).get
  }

  Then("""^I get a response with status "([^"]*)"$""") { (expectedStatus: String) =>
    status(response) mustBe expectedStatus.toInt
  }

  Then("""^I get the following json response body$""") { (expectedJson: DataTable) =>
    contentAsString(response) mustBe expectedJson.asScala.head.get("json")
  }
}
