package steps


import java.io._
import java.io.File.separator
import java.nio.file._
import java.util

import anorm._
import com.typesafe.config._
import controllers._
import controllers.dto._
import cucumber.api.DataTable
import cucumber.api.scala._
import julienrf.json.derived
import models._
import models.Feature._
import net.ruippeixotog.scalascraper.browser._
import org.apache.commons.io._
import org.eclipse.jgit.api._
import org.scalatest._
import org.scalatestplus.mockito._
import play.api.{Application, Logging, Mode}
import play.api.cache._
import play.api.db._
import play.api.inject._
import play.api.inject.guice._
import play.api.libs.json._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import repository._
import resource._
import services._
import steps.Injector._
import utils._

import scala.collection.JavaConverters._
import scala.concurrent._
import scala.io.Source
import scala.reflect._

object Injector {
  val builder = new GuiceApplicationBuilder
  lazy val injector = builder.injector()

  def inject[T: ClassTag]: T = injector.instanceOf[T]
}

case class ProjectTableRow(id: String, name: String, repositoryUrl: String, stableBranch: String, displayedBranches: String, featuresRootPath: String, documentationRootPath: String){
  def toProject(): Project = {
    Project(this.id, this.name, this.repositoryUrl, this.stableBranch, Option(this.displayedBranches), Option(this.featuresRootPath), Option(this.documentationRootPath))
  }
}


object CommonSteps extends MockitoSugar with MustMatchers {

  implicit val pageFormat = Json.format[Page]
  implicit val directoryFormat = Json.format[Directory]
  implicit val scenarioFormat = derived.flat.oformat[ScenarioDefinition]((__ \ "keyword").format[String])
  implicit val branchFormat = Json.format[Branch]
  implicit val hierarchyFormat = Json.format[HierarchyNode]
  implicit val projectFormat = Json.format[Project]

  var response: Future[Result] = _
  var page: String = _

  var projects: Map[String, Project] = _

  val db = inject[Database]
  val scenarioRepository = inject[ScenarioRepository]
  val featureRepository = inject[FeatureRepository]
  val branchRepository = inject[BranchRepository]
  val hierarchyRepository = inject[HierarchyRepository]
  val projectRepository = inject[ProjectRepository]
  val featureService = inject[FeatureService]
  val tagRepository = inject[TagRepository]
  val directoryRepository = inject[DirectoryRepository]
  val pageRepository = inject[PageRepository]
  val config = inject[Config]
  val cache = inject[AsyncCacheApi]

  val applicationBuilder = builder.overrides(bind[SyncCacheApi].toInstance(new DefaultSyncCacheApi(cache))).in(Mode.Test)

  var app: Application = _

  val projectsRootDirectory = config.getString("projects.root.directory").fixPathSeparator
  val remoteRootDirectory = "target/remote/data/".fixPathSeparator

  var server: RunningServer = _

  val browser = HtmlUnitBrowser.typed()

  private def startServer(app: Application) = {
    server = DefaultTestServerFactory.start(app)
  }

  private def stopServer() = {
    if (server != null) server.stopServer.close()
  }

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

class CommonSteps extends ScalaDsl with EN with MockitoSugar with Logging {

  import CommonSteps._

  Before() { _ =>
    app = applicationBuilder.build()
    startServer(app)
  }

  After { _ =>
    stopServer()
  }

  Given("""^we have the following configuration$""") { configs: util.List[Configuration] =>
    stopServer()

    val newConfig = configs.asScala.foldLeft(config)((acc: Config, conf: Configuration) => acc.withValue(conf.path, ConfigValueFactory.fromAnyRef(conf.value)))

    val newApp = applicationBuilder.overrides(bind[Config].toInstance(newConfig)).build()

    startServer(newApp)
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
      SQL"TRUNCATE TABLE page".executeUpdate()
      SQL"TRUNCATE TABLE directory".executeUpdate()
      SQL"ALTER TABLE branch ALTER COLUMN id RESTART WITH 1".executeUpdate()
      SQL"ALTER TABLE feature ALTER COLUMN id RESTART WITH 1".executeUpdate()
      SQL"ALTER TABLE scenario ALTER COLUMN id RESTART WITH 1".executeUpdate()
      SQL"ALTER TABLE page ALTER COLUMN id RESTART WITH 1".executeUpdate()
      SQL"ALTER TABLE directory ALTER COLUMN id RESTART WITH 1".executeUpdate()
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

  Given("""^we have the following projects$""") { projects: util.List[ProjectTableRow] =>
    val projectsWithAbsoluteUrl = projects.asScala.map { project =>
      if (project.repositoryUrl.contains("target/")) project.copy(
        repositoryUrl = Paths.get(project.repositoryUrl).toUri.toString,
        featuresRootPath = project.featuresRootPath.fixPathSeparator,
        documentationRootPath = if (project.documentationRootPath != null) project.documentationRootPath.fixPathSeparator else project.documentationRootPath
      )

      else project
    }.map(_.toProject())

    projectRepository.saveAll(projectsWithAbsoluteUrl)

    CommonSteps.projects = projectsWithAbsoluteUrl.map(p => (p.id, p)).toMap
  }

  Given("""^we have those branches in the database$""") { branches: util.List[Branch] =>
    branchRepository.saveAll(branches.asScala)
  }

  Given("""^we have those directories in the database$""") { directories: util.List[Directory] =>
    directoryRepository.saveAll(directories.asScala)
  }

  Given("""^we have those pages in the database$""") { pages: util.List[Page] =>
    pageRepository.saveAll(pages.asScala)
  }

  Given("""^the cache is empty$""") { () =>
    await(cache.removeAll())
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

  Then("""^I get the following json response body$""") { expected: String =>
    contentType(response) mustBe Some(JSON)
    val actualJson = contentAsJson(response)
    val expectedJson = Json.parse(expected.lines.map(l => if (separator == "\\" && (l.contains(""""path":""") || l.contains(""""features":"""))) l.replace("/", """\\""") else l).mkString("\n"))

    Files.write(Paths.get("test/actual.json".fixPathSeparator), Json.prettyPrint(actualJson).getBytes())
    Files.write(Paths.get("test/expected.json".fixPathSeparator), Json.prettyPrint(expectedJson).getBytes())

    actualJson mustBe expectedJson
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
    implicit val documentationFormat = Json.format[DocumentationDTO]

    val documentation = Json.parse(contentAsString(response)).as[DocumentationDTO]
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

  def nbRealScenario(documentation: DocumentationDTO): Int = {
    documentation.projects.map(p => if (p.branches.nonEmpty) nbRealScenario(p.branches.head) else 0).sum + documentation.children.map(nbRealScenario).sum
  }

  def nbRealScenario(branch: BranchDocumentationDTO): Int = {
    branch.features.map(_.scenarios.length).sum
  }

  def getHierarchy(hierarchy: String, source: DocumentationDTO): Option[DocumentationDTO] = {
    if (source.id == hierarchy) Some(source)
    else if (source.children.isEmpty) None
    else source.children.flatMap(getHierarchy(hierarchy, _)).headOption
  }
}
