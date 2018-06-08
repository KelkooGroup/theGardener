package steps


import anorm.SQL
import net.ruippeixotog.scalascraper.browser.HtmlUnitBrowser
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import services.ProjectService
import play.api.Mode
import play.api.db.Database
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder

import scala.reflect.ClassTag
import org.scalatest.mockito.MockitoSugar
import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models._

import scala.collection.JavaConverters._
import scala.concurrent.Future
import java.nio.file.{Files, Paths}
import repository.ProjectRepository


object Injector {
  lazy val injector = (new GuiceApplicationBuilder).injector()

  def inject[T: ClassTag]: T = injector.instanceOf[T]
}


object CommonSteps extends PlaySpec with GuiceOneServerPerSuite with BeforeAndAfterAll with MockitoSugar with Injecting {


  var response: Future[Result] = _

  var projects: Map[String, Project] = _

  val projectService = Injector.inject[ProjectService]

  implicit val db = Injector.inject[Database]

  override def fakeApplication(): Application = new GuiceApplicationBuilder().overrides(bind[ProjectService].toInstance(projectService)).in(Mode.Test).build()

  val projectRepository = Injector.inject[ProjectRepository]

  val server = TestServer(port, app)

  val browser = HtmlUnitBrowser.typed()

  override def beforeAll() = server.start()

  override def afterAll() = server.stop()

  def cleanHtmlWhitespaces(content: String): String = content.replace("\t", " ").replace("\n", " ").replace("<br/>", " ").trim().replaceAll(" +", " ")

  def cleanDatabase(): Unit = {
    db.withConnection { implicit connection =>
      SQL("TRUNCATE TABLE project").executeUpdate()
    }
  }

}

class CommonSteps extends ScalaDsl with EN with MockitoSugar {


  import CommonSteps._

  Given("""^the project settings are setup in theGardener$""") { (dataTable: DataTable) =>
    projects = dataTable.asScala.map { line =>
      val id = line("id")
      (id, Project(id, line("name"), line("repository_url"), line("stable_branch"), line("features_root_path")))
    }.toMap

    projectService.projects ++= projects
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

  Given("""^we have the following projects$""") { (data: DataTable) =>
    cleanDatabase()

    val projects = data.asList(classOf[Project]).asScala.map(project => Project(project.id, project.name, project.repositoryUrl, project.stableBranch, project.featuresRootPath))

    projects.foreach(projectRepository.insertOne)

    CommonSteps.projects = projects.map(p => (p.id, p)).toMap

  }


  When("^we got in a browser to url \"([^\"]*)\"$") { () =>
    response = route(app, FakeRequest(GET, s"""/feature/suggestionsWS/provide_book_suggestions.feature""")).get
  }

  When("""^I perform a POST on following URL "([^"]*)"$""") { (url: String, body: String) =>
    response = route(app, FakeRequest(POST, url).withJsonBody(Json.parse(body))).get
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
