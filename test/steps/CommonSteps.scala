package steps


import java.io.File.separator
import java.io._
import java.nio.file._
import java.util

import akka.stream.Materializer
import anorm._
import com.typesafe.config._
import controllers.dto._
import io.cucumber.datatable.DataTable
import io.cucumber.scala.{EN, JacksonDefaultDataTableEntryTransformer, ScalaDsl}
import io.cucumber.scala.Implicits._
import julienrf.json.derived
import models._
import models.Feature._
import net.ruippeixotog.scalascraper.browser._
import org.apache.commons.io._
import org.eclipse.jgit.api._
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.scalatest._
import org.scalatestplus.mockito._
import play.api.cache._
import play.api.db._
import play.api.inject._
import play.api.inject.guice._
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import play.api.{Application, Logging, Mode}
import repositories._
import resource._
import services._
import services.clients.{OpenApiClient, ReplicaClient}
import steps.Injector._
import utils.CustomConfigSystemReader.overrideSystemGitConfig
import utils._
import scala.concurrent.duration._
import scala.jdk.CollectionConverters._
import scala.concurrent._
import scala.io.Source
import scala.reflect._

object Injector {
  val builder = new GuiceApplicationBuilder
  lazy val injector = builder.injector()

  def inject[T: ClassTag]: T = injector.instanceOf[T]
}

case class ProjectTableRow(id: String, name: String, repositoryUrl: String, sourceUrlTemplate: String, stableBranch: String, displayedBranches: String, featuresRootPath: String, documentationRootPath: String, variables: String) {
  def toProject(): Project = {
    implicit val variableFormat = Json.format[Variable]
    Project(id, name, repositoryUrl, Option(sourceUrlTemplate).filter(_.nonEmpty), stableBranch, Option(displayedBranches), Option(featuresRootPath), Option(documentationRootPath), Option(variables).map(Json.parse(_).as[Seq[Variable]]))
  }
}


object CommonSteps extends MockitoSugar with MustMatchers {

  implicit val pageFormat = Json.format[Page]
  implicit val directoryFormat = Json.format[Directory]
  implicit val scenarioFormat = derived.flat.oformat[ScenarioDefinition]((__ \ "keyword").format[String])
  implicit val branchFormat = Json.format[Branch]
  implicit val hierarchyFormat = Json.format[HierarchyNode]
  implicit val variableFormat = Json.format[Variable]
  implicit val projectFormat = Json.format[Project]
  implicit val ec: ExecutionContext = ExecutionContext.global

  overrideSystemGitConfig()

  val fakeOpenApiClient: OpenApiClient = mock[OpenApiClient]

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
  val gherkinRepository = inject[GherkinRepository]
  val wsClient = inject[WSClient]
  val gitService = Injector.inject[GitService]
  val config = inject[Config]
  val conf = inject[play.api.Configuration]
  val environment = inject[play.api.Environment]
  val actorSystem = inject[akka.actor.ActorSystem]
  val hierarchyService = inject[HierarchyService]
  val asyncCache = inject[AsyncCacheApi]
  val cache = new DefaultSyncCacheApi(asyncCache)
  val pageServiceCache = new PageServiceCache(cache)
  val spyPageServiceCache = spy(pageServiceCache)
  val pageIndex = new IndexService
  val searchService = new SearchService(pageIndex)
  val spySearchService = spy(searchService)
  val pageService = new PageService(conf, projectRepository, directoryRepository, pageRepository, gherkinRepository, fakeOpenApiClient, pageServiceCache, pageIndex, hierarchyService)
  val spyPageService = spy(pageService)
  val menuService = new MenuService(hierarchyRepository, projectRepository, branchRepository, featureRepository, directoryRepository, pageRepository, cache)
  val spyMenuService = spy(menuService)
  val replicaService = new ReplicaClient(conf, wsClient)
  val spyReplicaService = spy(replicaService)
  val projectService = new ProjectService(projectRepository, gitService, featureService, featureRepository, branchRepository, directoryRepository, pageRepository, menuService, pageService, pageIndex, conf, environment, actorSystem)
  val spyProjectService = spy(projectService)


  implicit val materializer = inject[Materializer]

  val applicationBuilder = builder.overrides(bind[SyncCacheApi].toInstance(cache),
    bind[PageServiceCache].toInstance(spyPageServiceCache),
    bind[OpenApiClient].toInstance(fakeOpenApiClient),
    bind[PageService].toInstance(spyPageService),
    bind[MenuService].toInstance(spyMenuService),
    bind[ProjectService].toInstance(spyProjectService),
    bind[ReplicaClient].toInstance(spyReplicaService),
    bind[SearchService].toInstance(spySearchService)).in(Mode.Test)

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
    ()
  }
}

case class Configuration(path: String, value: String)

case class PageRow(id: Long, name: String, label: String, description: String, order: Int, markdown: String, relativePath: String, path: String, directoryId: Long, dependOnOpenApi: Boolean)

case class LuceneDoc(id: String, hierarchy: String, path: String, breadcrumb: String, project: String, branch: String, label: String, description: String, pageContent: String)

class CommonSteps extends ScalaDsl with EN with MockitoSugar with Logging with JacksonDefaultDataTableEntryTransformer {

  import CommonSteps._

  Before {
    app = applicationBuilder.build()
    startServer(app)
  }

  After {
    stopServer()
  }

  Given("""^we have the following configuration$""") { configs: util.List[Configuration] =>
    stopServer()

    val newConfig = configs.asScala.toSeq.foldLeft(config)((acc: Config, conf: Configuration) => acc.withValue(conf.path, ConfigValueFactory.fromAnyRef(conf.value)))

    val newApp = applicationBuilder.overrides(bind[Config].toInstance(newConfig)).build()

    startServer(newApp)
  }

  Given("""^the configuration$""") { configs: util.List[Configuration] =>

    configs.forEach { conf =>
      val value: String = app.configuration.get[String](conf.path)
      value mustBe conf.value
      ()
    }
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
    val fullPath = Paths.get(s"$path".fixPathSeparator)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }

  Given("""^we have the following projects$""") { projects: util.List[ProjectTableRow] =>
    val projectsWithAbsoluteUrl = projects.asScala.toSeq.map { project =>
      if (project.repositoryUrl.contains("target/")) project.copy(
        repositoryUrl = Paths.get(project.repositoryUrl).toUri.toString,
        featuresRootPath = if (project.featuresRootPath != null) project.featuresRootPath.fixPathSeparator else project.featuresRootPath,
        documentationRootPath = if (project.documentationRootPath != null) project.documentationRootPath.fixPathSeparator else project.documentationRootPath
      )
      else project
    }.map(_.toProject())

    projectRepository.saveAll(projectsWithAbsoluteUrl)

    CommonSteps.projects = projectsWithAbsoluteUrl.map(p => (p.id, p)).toMap
  }

  Given("""^we have those branches in the database$""") { branches: util.List[Branch] =>
    branchRepository.saveAll(branches.asScala.toSeq)
  }

  Given("""^we have those directories in the database$""") { directories: util.List[Directory] =>
    directoryRepository.saveAll(directories.asScala.toSeq)
  }

  Given("""^we have those pages in the database$""") { pages: util.List[PageRow] =>
    pageRepository.saveAll(pages.asScala.toSeq.map(p => Page(p.id, p.name, p.label, p.description, p.order, Option(p.markdown), p.relativePath, p.path, p.directoryId)))
  }

  Given("""^we have the following document in the lucene index$""") { docs: util.List[LuceneDoc] =>
    docs.asScala.toSeq.map(doc =>
      pageIndex.insertOrUpdateDocument(PageIndexDocument(doc.id, doc.hierarchy, doc.path, doc.breadcrumb, doc.project, doc.branch, doc.label, doc.description, doc.pageContent))
    )
  }

  Given("""^the lucene index is loaded from the database$""") { () =>
    pageIndex.reset()
    response = route(app, FakeRequest("POST", "/api/admin/projects/refreshFromDatabase")).get
    Await.result(response, 30.seconds)
  }

  Given("""^we have the following markdown for the page "([^"]*)"$""") { (path: String, markdown: String) =>
    pageRepository.findByPath(path).map { page =>
      pageRepository.save(page.copy(markdown = Some(markdown)))
    }
  }

  Given("""^we have the following variables from project "([^"]*)"$""") { (projectId: String, Requestvariables: String) =>
    projectRepository.findById(projectId).map { project =>
      projectRepository.save(project.copy(variables = Some(Json.parse(Requestvariables).as[Seq[Variable]])))
    }

  }

  Given("""^the cache is empty$""") { () =>
    await(asyncCache.removeAll())
  }


  Given("""^we have the following swagger.json hosted on "([^"]*)"$""") { (_: String, swaggerJson: String) =>
    mockOpenApiClient(swaggerJson)
  }

  When("""^the swagger.json hosted on "([^"]*)" is now$""") { (_: String, swaggerJson: String) =>
    mockOpenApiClient(swaggerJson)
  }

  Given("""^swagger\.json cannot be requested$""") { () =>
    reset(fakeOpenApiClient)
    when(fakeOpenApiClient.getOpenApiDescriptor(any[OpenApiModelModule](), any[PageJoinProject]())).thenReturn(Future.successful(OpenApiModel("", Option(Seq()), Seq(), Seq(), Seq("ERROR HTTP"))))
  }

  private def mockOpenApiClient(swaggerJson: String) = {
    reset(fakeOpenApiClient)
    when(fakeOpenApiClient.getOpenApiDescriptor(any[OpenApiModelModule](), any[PageJoinProject]())).thenAnswer((invocation: InvocationOnMock) => {
      val args = invocation.getArguments
      val openApiModule = args(0).asInstanceOf[OpenApiModelModule]

      Future.successful(OpenApiClient.parseSwaggerJsonDefinitions(swaggerJson, openApiModule.ref.getOrElse(""), openApiModule.deep, openApiModule.label))
    })
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
    val expectedJson = Json.parse(expected.linesIterator.map(l => if (separator == "\\" && (l.contains(""""path":""") || l.contains(""""features":"""))) l.replace("/", """\\""") else l).mkString("\n"))

    Files.write(Paths.get("test/actual.json".fixPathSeparator), Json.prettyPrint(actualJson).getBytes())
    Files.write(Paths.get("test/expected.json".fixPathSeparator), Json.prettyPrint(expectedJson).getBytes())

    actualJson mustBe expectedJson
  }

  Then("""^I get the following response body$""") { expected: String =>
    contentAsString(response) mustBe expected
  }

  Then("""^the page contains$""") { expectedPageContentPart: String =>
    val content = contentAsString(response)
    cleanHtmlWhitespaces(content) must include(cleanHtmlWhitespaces(expectedPageContentPart))
  }

  Then("""^the file system store now the file "([^"]*)"$""") { (file: String, content: String) =>
    managed(Source.fromFile(file.fixPathSeparator)).acquireAndGet(_.mkString mustBe content)
  }

  Then("""^the file system do not store now the file "([^"]*)"$""") { (file: String) =>
    Files.exists(Paths.get(file.fixPathSeparator)) mustBe false
  }

  Then("""^the file system store now the files$""") { files: DataTable =>
    files.asScalaMaps.map { line =>
      val file = line("file").get
      val content = line("content").get

      managed(Source.fromFile(file.fixPathSeparator)).acquireAndGet(_.mkString mustBe content)
    }
  }

  Then("""^I get the following scenarios$""") { dataTable: DataTable =>

    implicit val documentationFormat = Json.format[DocumentationDTO]

    val documentation = Json.parse(contentAsString(response)).as[DocumentationDTO]
    val expectedScenarios = dataTable.asScalaMaps

    expectedScenarios.length mustBe nbRealScenario(documentation)

    expectedScenarios.map { columns =>

      val nodeName = columns("hierarchy").get
      val projectId = columns("project").get
      val featurePath = columns("feature").get.fixPathSeparator
      val scenarioName = columns("scenario").get

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
