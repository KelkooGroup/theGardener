package steps


import java.io._
import java.net._
import java.nio.file._
import java.util

import anorm.SQL
import com.typesafe.config._
import cucumber.api.DataTable
import cucumber.api.scala._
import models._
import net.ruippeixotog.scalascraper.browser._
import org.apache.commons.io._
import org.eclipse.jgit.api._
import org.scalatest._
import org.scalatest.mockito._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api._
import play.api.db._
import play.api.inject._
import play.api.inject.guice._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import repository._
import services._

import scala.collection.JavaConverters._
import scala.concurrent._
import scala.io._
import scala.reflect._


object Injector {
  lazy val injector = (new GuiceApplicationBuilder).injector()

  def inject[T: ClassTag]: T = injector.instanceOf[T]
}


object CommonSteps extends PlaySpec with GuiceOneServerPerSuite with BeforeAndAfterAll with MockitoSugar with Injecting {

  implicit val hierarchyFormat = Json.format[HierarchyNode]
  implicit val projectFormat = Json.format[Project]

  var response: Future[Result] = _
  var page: String = _

  var projects: Map[String, Project] = _

  val applicationBuilder = new GuiceApplicationBuilder().in(Mode.Test)
  override def fakeApplication(): Application = applicationBuilder.build()

  val db = Injector.inject[Database]
  val hierarchyRepository = Injector.inject[HierarchyRepository]
  val projectRepository = Injector.inject[ProjectRepository]
  val featureService = Injector.inject[FeatureService]
  val config = Injector.inject[Config]

  val projectsRootDirectory = config.getString("projects.root.directory")

  var server = TestServer(port, app)

  val browser = HtmlUnitBrowser.typed()

  override def beforeAll() = server.start()

  override def afterAll() = server.stop()

  def cleanHtmlWhitespaces(content: String): String = content.split('\n').map(_.trim.filter(_ >= ' ')).mkString.replaceAll(" +", " ")

  def initRemoteRepository(branchName: String, projectRepositoryPath: String): Git = {
    FileUtils.deleteDirectory(new File(projectRepositoryPath))

    val git = Git.init().setDirectory(new File(projectRepositoryPath)).call()

    if (branchName != "master") git.checkout().setCreateBranch(true).setName(branchName).call()

    git
  }

  def addFile(git: Git, projectRepositoryPath: String, file: String, content: String): Any = {
    val filePath = Paths.get(s"$projectRepositoryPath/$file")

    Files.createDirectories(filePath.getParent)
    Files.write(filePath, content.getBytes("UTF-8"))

    git.add().addFilepattern(".").call()

    git.commit().setMessage(s"Add file $file").call()
  }
}

case class Configuration(path: String, value: String)

class CommonSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^we have the following configuration$""") { configs: util.List[Configuration] =>
    server.stop()

    val newConfig = configs.asScala.foldLeft(config)((acc: Config, conf: Configuration) => acc.withValue(conf.path, ConfigValueFactory.fromAnyRef(conf.value)))

    val newApp = applicationBuilder.overrides(bind[Config].toInstance(newConfig)).build()

    server = TestServer(port, newApp)

    server.start()
  }

  Given("""^the database is empty$""") { () =>
    db.withConnection { implicit connection =>
      SQL("TRUNCATE TABLE project").executeUpdate()
      SQL("TRUNCATE TABLE project_hierarchyNode").executeUpdate()
      SQL("TRUNCATE TABLE hierarchyNode").executeUpdate()
    }
  }

  Given("""^No project is checkout$""") { () =>
    FileUtils.deleteDirectory(new File(projectsRootDirectory))
    Files.createDirectories(Paths.get(projectsRootDirectory))
  }

  Given("""^a git server that host a project$""") { () =>
    //nothing to do here
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

  Given("""^we have the following projects$""") { projects: util.List[Project] =>
    val projectsWithAbsoluteUrl = projects.asScala.map { project =>
      if (project.repositoryUrl.contains("target/")) project.copy(repositoryUrl = new URL(new URL("file:"), new File(project.repositoryUrl).getAbsolutePath).toURI.toString)
      else project
    }

    projectRepository.saveAll(projectsWithAbsoluteUrl)

    CommonSteps.projects = projectsWithAbsoluteUrl.map(p => (p.id, p)).toMap
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

  Then("""^the file system store now the file "([^"]*)"$""") { (file: String, content: String) =>
    Source.fromFile(file).mkString mustBe content
  }

  Then("""^the file system store now the files$""") { files: DataTable =>
    files.asScala.map { line =>
      val file = line("file")
      val content = line("content")

      Source.fromFile(file).mkString mustBe content
    }

  }
}
