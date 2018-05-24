package steps

import java.nio.file.{Files, Paths}

import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models._
import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import services.ComponentService

import scala.concurrent.Future

object CommonSteps extends PlaySpec with GuiceOneServerPerSuite with BeforeAndAfterAll with MockitoSugar with Injecting {

  var response: Future[Result] = _

  var components: Map[String, Project] = _

  val componentService = new ComponentService()

  override def fakeApplication(): Application = new GuiceApplicationBuilder()
    .overrides(bind[ComponentService].toInstance(componentService))
    .build()

  val server = TestServer(port, app)

  val browser = HtmlUnitBrowser.typed()

  override def beforeAll() = server.start()

  override def afterAll() = server.stop()

  def cleanHtmlWhitespaces(content: String): String = content.replace("\t", " ").replace("\n", " ").replace("<br/>", " ").trim().replaceAll(" +", " ")
}

class CommonSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^the project settings are setup in theGardener$""") { (dataTable: DataTable) =>
    components = dataTable.asScala.map { line =>
      val id = line("id")
      (id, Project(id, line.getOrElse("group", ""), line.getOrElse("system", ""), line("name"), line("repository_url"), line("stable_branch"), line("features_root_path")))
    }.toMap

    componentService.projects ++= components
  }

  Given("""^a simple feature is available in my project$""") { () =>
    val fullPath = Paths.get("target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature")
    Files.createDirectories(fullPath.getParent)

    val content =
      """
Feature: Provide some book suggestions
  As a user,
  I want some book suggestions
  So that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

    Files.write(fullPath, content.getBytes())
  }

  Given("""^the file "([^"]*)"$""") { (path: String, content: String) =>
    val fullPath = Paths.get("target/" + path)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }

  When("""^I perform a GET on following URL "([^"]*)"$""") { (url: String) =>
    response = route(app, FakeRequest(GET, url)).get
  }

  Then("""^I get a response with status "([^"]*)"$""") { (expectedStatus: String) =>
    status(response) mustBe expectedStatus.toInt
  }

  Then("""^I get the following json response body$""") { (expectedJson: String) =>
    contentType(response) mustBe Some(JSON)
    contentAsJson(response) mustBe Json.parse(expectedJson)
  }

  Then("""^the page contains$""") { (expectedPageContentPart: String) =>
    val content = contentAsString(response)

    cleanHtmlWhitespaces(content) must include(cleanHtmlWhitespaces(expectedPageContentPart))
  }
}
