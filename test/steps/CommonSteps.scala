package steps

import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import org.scalatest._
import Matchers._

import scala.concurrent.Future

object CommonSteps extends PlaySpec with GuiceOneServerPerSuite with ScalaDsl with MockitoSugar with Injecting {

  var response: Future[Result] = _

  var components: Map[String, Project] = _

  val server = TestServer(port, app)

  Before() { _ =>
    server.start()
  }

  After() { _ =>
    server.stop()
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

  Then("""^the page contains$"""){ (expectedPageContentPart:String) =>
    val content : String = contentAsString(response)

    // Maybe we need to remove all blank / tabs / \n\r before comparing Strings
    // Maybe we need to make sure that 2 different calls get sub string in order

    content  should include (expectedPageContentPart)
  }


}
