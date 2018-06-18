package steps


import java.nio.file.{Files, Paths}

import anorm.SQL
import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models._
import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.db.Database
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import play.api.{Application, Mode}
import repository.ProjectRepository
import services.FeatureService

import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.reflect.ClassTag


object Injector {
  lazy val injector = (new GuiceApplicationBuilder).injector()

  def inject[T: ClassTag]: T = injector.instanceOf[T]
}


object CommonSteps extends PlaySpec with GuiceOneServerPerSuite with BeforeAndAfterAll with MockitoSugar with Injecting {

  implicit val projectFormat = Json.format[Project]

  var response: Future[Result] = _
  var page: String = _

  var projects: Map[String, Project] = _

  override def fakeApplication(): Application = new GuiceApplicationBuilder().in(Mode.Test).build()

  val db = Injector.inject[Database]
  val projectRepository = Injector.inject[ProjectRepository]
  val featureService = Injector.inject[FeatureService]

  val server = TestServer(port, app)

  val browser = HtmlUnitBrowser.typed()

  override def beforeAll() = server.start()

  override def afterAll() = server.stop()

  def cleanHtmlWhitespaces(content: String): String = content.split('\n').map(_.trim.filter(_ >= ' ')).mkString.replaceAll(" +", " ")

  def cleanDatabase(): Unit = {
    db.withConnection { implicit connection =>
      SQL("TRUNCATE TABLE project").executeUpdate()
    }
  }
}

class CommonSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^a git server that host a project$""") { () =>
    // nothing to do here
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

  Given("""^we have the following projects$""") { data: DataTable =>
    val projects = data.asList(classOf[Project]).asScala

    projectRepository.saveAll(projects)

    CommonSteps.projects = projects.map(p => (p.id, p)).toMap
  }

  When("^we go in a browser to url \"([^\"]*)\"$") { url: String =>
    page = browser.get(url).toHtml
  }

  When("""^I perform a "([^"]*)" on following URL "([^"]*)"$""") { (method: String, url: String) =>
    response = route(app, FakeRequest(method, url)).get
    await(response)
  }

  When("""^I perform a "([^"]*)" on following URL "([^"]*)" with json body$""") { (method: String, url: String, body: String) =>
    response = route(app, FakeRequest(method, url).withJsonBody(Json.parse(body))).get
    await(response)
  }

  Then("""^I get a response with status "([^"]*)"$""") { expectedStatus: String =>
    status(response) mustBe expectedStatus.toInt
  }

  Then("""^I get the following json response body$""") { expectedJson: String =>
    contentType(response) mustBe Some(JSON)
    contentAsJson(response) mustBe Json.parse(expectedJson)
  }

  Then("""^the page contains$""") { expectedPageContentPart: String =>
    val content = contentAsString(response)
    cleanHtmlWhitespaces(content) must include(cleanHtmlWhitespaces(expectedPageContentPart))
  }


}
