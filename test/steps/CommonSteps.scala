package steps


import java.io._
import java.io.File.separator
import java.net._
import java.nio.file._
import java.util

import anorm._
import com.typesafe.config._
import controllers._
import cucumber.api.DataTable
import cucumber.api.scala._
import julienrf.json.derived
import models._
import models.Feature._
import net.ruippeixotog.scalascraper.browser._
import org.apache.commons.io._
import org.eclipse.jgit.api._
import org.scalatest._
import org.scalatest.mockito._
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api._
import play.api.cache.AsyncCacheApi
import play.api.db._
import play.api.inject._
import play.api.inject.guice._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import repository._
import resource._
import services.CriteriaService._
import services._
import utils._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.io.Source
import scala.reflect._


object Injector {
  lazy val injector = (new GuiceApplicationBuilder).injector()

  def inject[T: ClassTag]: T = injector.instanceOf[T]
}


object CommonSteps extends PlaySpec with GuiceOneServerPerSuite with BeforeAndAfterAll with MockitoSugar with Injecting {

  implicit val scenarioFormat = derived.flat.oformat[ScenarioDefinition]((__ \ "keyword").format[String])
  implicit val branchFormat = Json.format[Branch]
  implicit val hierarchyFormat = Json.format[HierarchyNode]
  implicit val projectFormat = Json.format[Project]

  var response: Future[Result] = _
  var page: String = _

  var projects: Map[String, Project] = _

  val cache = new InMemoryCache()

  val applicationBuilder = new GuiceApplicationBuilder().overrides(bind[AsyncCacheApi].toInstance(cache)).in(Mode.Test)

  override def fakeApplication(): play.api.Application = applicationBuilder.build()

  val db = Injector.inject[Database]
  val scenarioRepository = Injector.inject[ScenarioRepository]
  val featureRepository = Injector.inject[FeatureRepository]
  val branchRepository = Injector.inject[BranchRepository]
  val hierarchyRepository = Injector.inject[HierarchyRepository]
  val projectRepository = Injector.inject[ProjectRepository]
  val featureService = Injector.inject[FeatureService]
  val tagRepository = Injector.inject[TagRepository]
  val config = Injector.inject[Config]

  val projectsRootDirectory = config.getString("projects.root.directory").fixPathSeparator
  val remoteRootDirectory = "target/remote/data/".fixPathSeparator

  var server = TestServer(port, app)

  val browser = HtmlUnitBrowser.typed()

  override def beforeAll(): Unit = server.start()

  override def afterAll(): Unit = server.stop()

  def cleanHtmlWhitespaces(content: String): String = content.split('\n').map(_.trim.filter(_ >= ' ')).mkString.replaceAll(" +", " ")

  def initRemoteRepositoryIfNeeded(branchName: String, projectRepositoryPath: String): Git = {
    val projectRepositoryDir = new File(projectRepositoryPath)
    if (!projectRepositoryDir.exists()) {
      val git = Git.init().setDirectory(new File(projectRepositoryPath)).call()
      if (branchName != "master") git.checkout().setCreateBranch(true).setName(branchName).call()
      git
    } else {
      Git.open(projectRepositoryDir)
    }
  }

  def addFile(git: Git, projectRepositoryPath: String, file: String, content: String): Unit = {
    val filePath = Paths.get(s"$projectRepositoryPath/$file".fixPathSeparator)

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
      SQL"TRUNCATE TABLE project".executeUpdate()
      SQL"TRUNCATE TABLE project_hierarchyNode".executeUpdate()
      SQL"TRUNCATE TABLE hierarchyNode".executeUpdate()
      SQL"TRUNCATE TABLE branch".executeUpdate()
      SQL"TRUNCATE TABLE feature".executeUpdate()
      SQL"TRUNCATE TABLE scenario".executeUpdate()
      SQL"TRUNCATE TABLE feature_tag".executeUpdate()
      SQL"TRUNCATE TABLE scenario_tag".executeUpdate()
      SQL"TRUNCATE TABLE tag".executeUpdate()
      SQL"ALTER TABLE branch ALTER COLUMN id RESTART WITH 1".executeUpdate()
      SQL"ALTER TABLE feature ALTER COLUMN id RESTART WITH 1".executeUpdate()
      SQL"ALTER TABLE scenario ALTER COLUMN id RESTART WITH 1".executeUpdate()
    }
  }

  Given("""^No project is checkout$""") { () =>
    FileUtils.deleteDirectory(new File("target/data/".fixPathSeparator))
    FileUtils.deleteDirectory(new File(projectsRootDirectory))
    Files.createDirectories(Paths.get(projectsRootDirectory))
  }

  Given("""^the remote projects are empty$""") { () =>
    FileUtils.deleteDirectory(new File(remoteRootDirectory))
    Files.createDirectories(Paths.get(remoteRootDirectory))
  }

  Given("""^a git server that host a project$""") { () =>
    // nothing to do here
  }

  Given("""^a simple feature is available in my project$""") { () =>
    val fullPath = Paths.get("target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature".fixPathSeparator)
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
    val fullPath = Paths.get(s"target/$path".fixPathSeparator)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }

  Given("""^we have the following projects$""") { projects: util.List[Project] =>
    val projectsWithAbsoluteUrl = projects.asScala.map { project =>
      if (project.repositoryUrl.contains("target/")) project.copy(
        repositoryUrl = Paths.get(project.repositoryUrl).toUri.toString,
        featuresRootPath = project.featuresRootPath.fixPathSeparator)
      else project
    }

    projectRepository.saveAll(projectsWithAbsoluteUrl)

    CommonSteps.projects = projectsWithAbsoluteUrl.map(p => (p.id, p)).toMap
  }

  Given("""^we have those branches in the database$""") { branches: util.List[Branch] =>
    branchRepository.saveAll(branches.asScala)
  }

  Given("""^the cache is empty$""") { () =>
    cache.remove(criteriasListCacheKey)
    cache.remove(criteriasTreeCacheKey)
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
    contentAsJson(response) mustBe Json.parse(expectedJson.lines.map(l => if (separator == "\\" && (l.contains(""""path":""") || l.contains(""""features":""")) ) l.replace("/", """\\""") else l).mkString("\n"))
  }

  Then("""^the page contains$""") { expectedPageContentPart: String =>
    val content = contentAsString(response)
    cleanHtmlWhitespaces(content) must include(cleanHtmlWhitespaces(expectedPageContentPart))
  }

  Then("""^the file system store now the file "([^"]*)"$""") { (file: String, content: String) =>
    managed(Source.fromFile(file.fixPathSeparator)).acquireAndGet(_.mkString mustBe content)
  }

  Then("""^the file system store now the files$""") { files: DataTable =>
    files.asScala.map { line =>
      val file = line("file")
      val content = line("content")

      managed(Source.fromFile(file.fixPathSeparator)).acquireAndGet(_.mkString mustBe content)
    }
  }

  Then("""^I get the following scenarios$""") { dataTable: DataTable =>

    implicit val branchFormat = Json.format[BranchDocumentationDTO]
    implicit val projectFormat = Json.format[ProjectDocumentationDTO]
    implicit val documentationFormat = Json.format[Documentation]

    val documentation = Json.parse(contentAsString(response)).as[Documentation]
    val expectedScenarios = dataTable.asMaps(classOf[String], classOf[String]).asScala.toSeq

    expectedScenarios.length mustBe nbRealScenario(documentation)

    expectedScenarios.map { columns =>

      val nodeName = columns.get("hierarchy")
      val projectId = columns.get("project")
      val featurePath = columns.get("feature").fixPathSeparator
      val scenarioName = columns.get("scenario")

      val node = getHierarchy(nodeName, documentation)
      node.isDefined mustBe true


      val project = node.get.projects.filter(_.id == projectId)
      project.size mustBe 1
      project.head.branches.length mustBe 1

      val branch = project.head.branches.head

      val features = branch.features.filter(_.path == featurePath)
      features.size mustBe 1

      val scenario = features.head.scenarios.filter(_.name == scenarioName)
      scenario.size mustBe 1
    }
  }

  def nbRealScenario(documentation: Documentation): Int = {
    documentation.projects.map(p => if (p.branches.nonEmpty) nbRealScenario(p.branches.head) else 0).sum + documentation.children.map(nbRealScenario).sum
  }

  def nbRealScenario(branch: BranchDocumentationDTO): Int = {
    branch.features.map(_.scenarios.length).sum
  }

  def getHierarchy(hierarchy: String, source: Documentation): Option[Documentation] = {
    if (source.id == hierarchy) Some(source)
    else if (source.children.isEmpty) None
    else source.children.flatMap(getHierarchy(hierarchy, _)).headOption
  }
}
