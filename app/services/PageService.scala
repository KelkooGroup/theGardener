package services

import java.io.{File, FileInputStream}
import scala.annotation.nowarn
import controllers.dto.{PageDTO, PageFragment, PageFragmentContent}

import javax.inject._
import models.{PageJoinProject, _}
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Logging}
import play.api.cache.SyncCacheApi
import play.api.libs.json._
import repositories._
import utils._
import services.MenuService.getCorrectedPath
import services.clients.OpenApiClient

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

case class PageMeta(label: Option[String] = None, description: Option[String] = None)

case class DirectoryMeta(label: Option[String] = None, description: Option[String] = None, pages: Option[Seq[String]] = None, children: Option[Seq[String]] = None)

case class TagsModule(tags: Seq[String])

case class ScenariosModule(project: Option[String] = None, branchName: Option[String] = None, feature: Option[String] = None, select: Option[TagsModule] = None, includeBackground: Option[Boolean] = None)

case class IncludeExternalPageModule(url: String)

case class OpenApiModelModule(openApiUrl: Option[String] = None, openApiType: Option[String] = None, ref: Option[String] = None, deep: Option[Int] = None, label: Option[String] = None, errorMessage: Option[String] = None)

case class OpenApiPathModule(openApiUrl: Option[String] = None, refStartsWith: Option[Seq[String]], ref: Option[Seq[String]] = None, methods: Option[Seq[String]] = None, errorMessage: Option[String] = None)

case class Items(openApiType: String, ref: String)

object IncludeExternalPageModule {
  implicit val pageFormat = Json.format[IncludeExternalPageModule]
}

case class Module(directory: Option[DirectoryMeta] = None,
                  page: Option[PageMeta] = None,
                  scenarios: Option[ScenariosModule] = None,
                  includeExternalPage: Option[IncludeExternalPageModule] = None,
                  openApi: Option[OpenApiModelModule] = None,
                  openApiPath: Option[OpenApiPathModule] = None)

sealed trait PageFragmentUnderProcessingStatus

case object PageFragmentStatusMarkdown extends PageFragmentUnderProcessingStatus

case object PageFragmentStatusMakdownEscape extends PageFragmentUnderProcessingStatus

case object PageFragmentStatusModule extends PageFragmentUnderProcessingStatus

case class PageFragmentUnderProcessing(status: PageFragmentUnderProcessingStatus = PageFragmentStatusMarkdown,
                                       data: Option[String] = None,
                                       markdown: Option[String] = None,
                                       page: Option[PageMeta] = None,
                                       scenariosModule: Option[ScenariosModule] = None,
                                       scenarios: Option[Feature] = None,
                                       includeExternalPage: Option[IncludeExternalPageModule] = None,
                                       openApi: Option[OpenApiModel] = None,
                                       openApiModule: Option[OpenApiModelModule] = None,
                                       openApiPath: Option[OpenApiPath] = None,
                                       openApiPathModule: Option[OpenApiPathModule] = None)

case class PageWithContent(page: Page, content: Seq[PageFragment])

@Singleton
class PageServiceCache @Inject()(configuration: Configuration, cache: SyncCacheApi) extends Logging {

  private val cacheTtl: Duration = configuration.getOptional[Long]("cache.ttl").map(_.minutes).getOrElse(Duration.Inf)

  def store(key: String, page: PageWithContent): Unit = {
    put(key)
    cache.set(key, page, cacheTtl)
  }

  def put(key: String): Unit = {
    logger.trace(s"Page put in cache the key $key")
  }

  def get(key: String): Option[PageWithContent] = {
    cache.get[PageWithContent](key)
  }

}


// scalastyle:off number.of.methods
@Singleton
class PageService @Inject()(config: Configuration, projectRepository: ProjectRepository, directoryRepository: DirectoryRepository, pageRepository: PageRepository,
                            gherkinRepository: GherkinRepository, openApiClient: OpenApiClient, cache: PageServiceCache, indexService: IndexService, hierarchyService: HierarchyService)(implicit ec: ExecutionContext) extends Logging {

  implicit val pageMetaFormat = Json.format[PageMeta]
  implicit val directoryMetaFormat = Json.format[DirectoryMeta]

  implicit val tagsModuleFormat = Json.format[TagsModule]
  implicit val scenariosModuleMetaFormat = Json.format[ScenariosModule]
  implicit val openApiModuleFormat = Json.format[OpenApiModelModule]
  implicit val openApiPathModuleFormat = Json.format[OpenApiPathModule]
  implicit val metaFormat = Json.format[Module]
  implicit val itemsFormat = Json.format[Items]


  val projectsRootDirectory = config.get[String]("projects.root.directory")
  val documentationMetaFile = config.get[String]("documentation.meta.file")
  val baseUrl = config.getOptional[String]("application.baseUrl").getOrElse("")

  val MarkdownEnd = "~#~{~}~#~"
  val MarkdownEscape = "````"
  val MarkdownCodeStart = "```"
  val ModuleStart = "```thegardener"
  val ModuleEnd = "```"
  val PageModuleRegex = """\`\`\`thegardener([\s\S]*"page"[^`]*)\`\`\`""".r
  val ImageRegex = """\!\[.*\]\((.*)\)""".r
  val ReferenceRegex = """\[.*\]\:\s(\S*)""".r

  val StartVar = "${"
  val EndVar = "}"
  val SourceTemplateBranchToken = s"${StartVar}branch$EndVar"
  val SourceTemplatePathToken = s"${StartVar}path$EndVar"


  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def getPagePath(projectId: String, branch: String, path: String, documentationRootPath: String): String = {
    val branchName = if (branch == "") {
      projectRepository.findById(projectId).map(_.stableBranch)
    } else {
      branch
    }

    s"$projectId>$branchName>${path.substring(path.indexOf(documentationRootPath) + documentationRootPath.length, path.length)}".replace(".md", "")
  }


  def processDirectory(branch: Branch, path: String, localDirectoryPath: String, relativePath: String = "/", order: Int = 0, isRoot: Boolean = true): Option[Directory] = {
    val metaFile = new File(localDirectoryPath + "/" + documentationMetaFile)
    if (metaFile.exists()) {
      Try {
        val meta = Json.parse(new FileInputStream(metaFile)).as[Module]
        val pathSplit = path.split("/")

        val name = if (isRoot) "root" else pathSplit(pathSplit.length - 1)

        val currentDirectory = directoryRepository.save(Directory(-1, name, meta.directory.flatMap(_.label).getOrElse(name), meta.directory.flatMap(_.description).getOrElse(name), order, relativePath, path, branch.id))

        val pages = meta.directory.flatMap(_.pages).getOrElse(Seq()).zipWithIndex.flatMap { case (pagePath, index) =>
          processPage(currentDirectory, localDirectoryPath, pagePath, index)
        }

        val children = meta.directory.flatMap(_.children).getOrElse(Seq()).zipWithIndex.flatMap { case (childName, index) =>
          processDirectory(branch, path + childName + "/", localDirectoryPath + "/" + childName, relativePath + childName + "/", index, isRoot = false)
        }

        if (pages.isEmpty && children.isEmpty) {
          directoryRepository.delete(currentDirectory)
        }

        Directory(currentDirectory.id, currentDirectory.name, currentDirectory.label, currentDirectory.description, currentDirectory.order, currentDirectory.relativePath, currentDirectory.path, currentDirectory.branchId, pages, children)

      }.logError(s"Error while parsing directory $path").toOption
    } else None
  }

  def computePageFromPath(path: String, refresh: Boolean = false): Future[Option[PageDTO]] = {
    projectRepository.findById(projectIdFromPath(path))
      .map(project => completePathWithBranchIfNeeded(path, project.stableBranch))
      .map { pathWithBranch =>

        val pageJoinProjectOpt = pageRepository.findByPathJoinProject(pathWithBranch)

        computePageFromPath(pageJoinProjectOpt, pathWithBranch, refresh).map {
          _.map { pageWithContent =>
            val variables = getVariables(pageJoinProjectOpt)
            val content = replaceVariablesInMarkdown(pageWithContent.content, variables.getOrElse(Seq()))
            val sourceUrl = getSourceUrl(pageJoinProjectOpt)
            PageDTO(pageWithContent.page, content, sourceUrl)
          }
        }
      }
      .getOrElse(Future.successful(None))
  }

  private def projectIdFromPath(path: String) = {
    path.split(">")(0)
  }

  private def completePathWithBranchIfNeeded(path: String, stableBranch: String): String = {
    if (path.contains(">>")) {
      path.replace(">>", s">$stableBranch>")
    } else {
      path
    }
  }

  private def computePageFromPath(pageJoinProjectOpt: Option[PageJoinProject], pathWithBranch: String, refresh: Boolean): Future[Option[PageWithContent]] = {
    if (refresh) {
      computePageFromPathUsingDatabaseBis(pageJoinProjectOpt, pathWithBranch)
    } else {
      val key = computePageCacheKey(pathWithBranch)
      cache.get(key) match {
        case Some(page) =>
          Future.successful(Some(page))
        case None =>
          logger.debug(s"Page not found in cache: $key")
          computePageFromPathUsingDatabaseBis(pageJoinProjectOpt, pathWithBranch)
      }
    }
  }

  @nowarn("msg=missing interpolator")
  private def getVariables(pageJoinProjectOpt: Option[PageJoinProject]): Option[Seq[Variable]] = {
    pageJoinProjectOpt.map { pageJoinProject =>
      val project = pageJoinProject.project
      val branch = pageJoinProject.branch
      val availableImplicitVariable = Seq(Variable("${project.current}", s"${project.name}"), Variable("${branch.current}", s"${branch.name}"), Variable("${branch.stable}", s"${project.stableBranch}"))
      project.variables.getOrElse(Seq()) ++ availableImplicitVariable
    }
  }

  private def getSourceUrl(pageJoinProjectOpt: Option[PageJoinProject]): Option[String] = {
    for {
      pageJoinProject <- pageJoinProjectOpt
      sourceUrlTemplate <- pageJoinProject.project.sourceUrlTemplate
      docRootPath <- pageJoinProject.project.documentationRootPath
    } yield {
      val branchName = pageJoinProject.branch.name
      val filePath = docRootPath + pageJoinProject.page.relativePath + ".md"
      sourceUrlTemplate
        .replace(SourceTemplateBranchToken, branchName)
        .replace(SourceTemplatePathToken, filePath)
    }
  }

  private def computePageCacheKey(path: String): String = s"page_$path"

  def computePageFromPathUsingDatabase(path: String, forceRefresh: Boolean = true): Future[Option[PageWithContent]] = {
    val pageJoinProjectOpt = pageRepository.findByPathJoinProject(path)
    computePageFromPathUsingDatabaseBis(pageJoinProjectOpt, path, forceRefresh)
  }

  def computePageFromPathUsingDatabaseBis(pageJoinProjectOpt: Option[PageJoinProject], path: String, forceRefresh: Boolean = true): Future[Option[PageWithContent]] = {
    pageJoinProjectOpt match {
      case Some(pageJoinProject) =>
        if (pageJoinProject.branch.isStable) {
          insertOrUpdateIndex(pageJoinProject)
        }
        val key = computePageCacheKey(path)
        if (cache.get(key).isEmpty || forceRefresh || pageJoinProject.page.dependOnOpenApi) {
          logger.debug(s"Page computed: $path")
          pageJoinProject.page.markdown.map { markdown =>
            splitMarkdown(s"$markdown\n$MarkdownEnd", path)
          }.map { fragments =>
            processPageFragments(fragments, pageJoinProject)
          } match {
            case Some(fragmentsFuture) =>
              val dependOnOpenApiFuture = fragmentsFuture.map(fragment => fragment.exists(_.`type` == "openApi"))
              val pageFuture = fragmentsFuture.flatMap(fragments => dependOnOpenApiFuture.map(dependOnOpenApi => PageWithContent(pageJoinProject.page.copy(path = getCorrectedPath(path, pageJoinProject.project), dependOnOpenApi = dependOnOpenApi), fragments)))
              pageFuture.map { page =>
                dependOnOpenApiFuture.map(dependOnOpenApi => if (dependOnOpenApi) {
                  pageRepository.save(page.page.copy(path = if (path.contains(">>")) path.replace(">>", s">${pageJoinProject.project.stableBranch}>") else path))
                })
                cache.store(key, page)
                Some(page)
              }
            case _ => Future.successful(None)
          }
        } else {
          Future.successful(None)
        }
      case _ => Future.successful(None)
    }
  }

  private def insertOrUpdateIndex(pageJoinProject: PageJoinProject): Unit = {

    hierarchyService.getHierarchyPath(pageJoinProject).foreach { hierarchy =>
      val document = PageIndexDocument(id = hierarchy + "/" + pageJoinProject.page.path,
        hierarchy = hierarchy,
        path = pageJoinProject.page.path,
        breadcrumb = hierarchyService.getBreadcrumb(pageJoinProject),
        project = pageJoinProject.project.name,
        branch = pageJoinProject.branch.name,
        label = pageJoinProject.page.label,
        description = pageJoinProject.page.description,
        pageContent = pageJoinProject.page.markdown.getOrElse(""))
      indexService
        .insertOrUpdateDocument(document)
        .recover { case ex: Throwable =>
          logger.error("An error occured while indexing document in Lucene", ex)
          ()
        }
    }

  }

  def processPage(currentDirectory: Directory, localDirectoryPath: String, name: String, order: Int): Option[Page] = {
    val pageFile = new File(localDirectoryPath + "/" + name + ".md")
    if (pageFile.exists()) {

      val pageOption = readPageContent(pageFile, name).map { case (content, label, description) =>
        pageRepository.save(Page(-1, name, label, description, order, Some(content), currentDirectory.relativePath + name, currentDirectory.path + name, currentDirectory.id))
      }
      cachePage(pageOption)

    } else None
  }

  def processPage(projectId: String, branchName: String, page: Page, documentationRootPath: String): Option[Page] = {
    val pageFile = new File(getLocalRepository(projectId, branchName) + documentationRootPath + "/" + page.relativePath + ".md")
    if (pageFile.exists()) {
      val pageOption = readPageContent(pageFile, page.name).map { case (content, label, description) =>
        pageRepository.save(page.copy(markdown = Some(content), label = label, description = description))
      }
      cachePage(pageOption)

    } else None
  }

  private def cachePage(pageOption: Option[Page]): Option[Page] = {
    pageOption match {
      case Some(page) => computePageFromPathUsingDatabase(page.path)
      case _ =>
    }
    pageOption
  }

  def readPageContent(page: File, name: String): Option[(String, String, String)] = {

    Try(FileUtils.readFileToString(page, "UTF-8")).logError(s"Error while reading content page ${page.getPath}").toOption.map { content =>
      (for {
        meta <- findPageModule(content)
      } yield (content, meta.label.getOrElse(name), meta.description.orElse(meta.label).getOrElse(name))).getOrElse((content, name, name))
    }
  }

  def parseModule(moduleString: String, path: String): Option[Module] = {
    Try(Json.parse(moduleString.trim).as[Module]).logError(s"Error while parsing module '$moduleString' of page $path").toOption
  }

  def appendCurrentLine(fragment: PageFragmentUnderProcessing, line: String): PageFragmentUnderProcessing = {
    fragment.data match {
      case Some(data) => fragment.copy(data = Some(data + "\n" + line))
      case None => fragment.copy(data = Some(line))
    }
  }

  def splitMarkdown(markdown: String, path: String = ""): Seq[PageFragmentUnderProcessing] = {
    markdown.split('\n').foldLeft((Seq[PageFragmentUnderProcessing](), PageFragmentUnderProcessing())) { (fragmentsAndCurrent, line) =>
      fragmentsAndCurrent match {
        case (fragments, currentFragment) =>
          processPageFragmentLine(line, path, fragments, currentFragment)
      }
    }._1
  }

  // scalastyle:off cyclomatic.complexity
  private def processPageFragmentLine(line: String, path: String, fragments: Seq[PageFragmentUnderProcessing], currentFragment: PageFragmentUnderProcessing): (Seq[PageFragmentUnderProcessing], PageFragmentUnderProcessing) = {
    val currentFragmentWithNewLine = appendCurrentLine(currentFragment, line)
    val newFragmentWithNewLine = appendCurrentLine(PageFragmentUnderProcessing(), line)

    currentFragment.status match {

      case PageFragmentStatusMakdownEscape if line.trim.startsWith(MarkdownEscape) => (fragments, currentFragmentWithNewLine.copy(status = PageFragmentStatusMarkdown))
      case PageFragmentStatusMakdownEscape => (fragments, currentFragmentWithNewLine)

      case PageFragmentStatusModule => processPageFragmentModule(line, path, fragments, currentFragment, currentFragmentWithNewLine)

      case _ if line.trim.startsWith(MarkdownEnd) => (fragments :+ currentFragment.copy(markdown = currentFragment.data), PageFragmentUnderProcessing())
      case _ if line.trim.startsWith(MarkdownEscape) => (fragments, currentFragmentWithNewLine.copy(status = PageFragmentStatusMakdownEscape))
      case _ if line.trim.startsWith(ModuleStart) => (fragments :+ currentFragment.copy(markdown = currentFragment.data), newFragmentWithNewLine.copy(status = PageFragmentStatusModule))

      case _ => (fragments, currentFragmentWithNewLine)
    }
    // scalastyle:on cyclomatic.complexity
  }

  private def processPageFragmentModule(line: String, path: String, fragments: Seq[PageFragmentUnderProcessing], currentFragment: PageFragmentUnderProcessing, currentFragmentWithNewLine: PageFragmentUnderProcessing) = {
    if (line.trim.startsWith(MarkdownCodeStart)) {

      currentFragment.data.flatMap(data => parseModule(data.replace(ModuleStart, ""), path)) match {
        case Some(Module(_, Some(page), _, _, _, _)) =>
          (fragments :+ currentFragment.copy(page = Some(page)), PageFragmentUnderProcessing())

        case Some(Module(_, _, Some(scenarios), _, _, _)) =>
          (fragments :+ currentFragment.copy(scenariosModule = Some(scenarios)), PageFragmentUnderProcessing())

        case Some(Module(_, _, _, Some(include), _, _)) =>
          (fragments :+ currentFragment.copy(includeExternalPage = Some(include)), PageFragmentUnderProcessing())

        case Some(Module(_, _, _, _, Some(openApi), _)) =>
          (fragments :+ currentFragment.copy(openApiModule = Some(openApi)), PageFragmentUnderProcessing())

        case Some(Module(_, _, _, _, _, Some(openApiPath))) =>
          (fragments :+ currentFragment.copy(openApiPathModule = Some(openApiPath)), PageFragmentUnderProcessing())

        case _ =>
          (fragments, PageFragmentUnderProcessing())
      }
    } else {
      (fragments, currentFragmentWithNewLine)
    }
  }

  def processPageFragments(fragments: Seq[PageFragmentUnderProcessing], pageJoinProject: PageJoinProject): Future[Seq[PageFragment]] = {
    Future.sequence(fragments.map {

      case PageFragmentUnderProcessing(PageFragmentStatusModule, _, _, _, Some(scenariosModule), _, _, _, _, _, _) =>
        val feature = buildFeature(scenariosModule, pageJoinProject)

        if (scenariosModule.includeBackground.getOrElse(false)) {
          Future.successful(PageFragmentUnderProcessing(scenarios = feature))

        } else {
          Future.successful(PageFragmentUnderProcessing(scenarios = feature.map(_.copy(background = None))))
        }

      case PageFragmentUnderProcessing(PageFragmentStatusMarkdown, _, Some(rawMarkdown), _, _, _, _, _, _, _, _) =>

        val images = findPageImagesWithRelativePath(rawMarkdown)
        val references = findPageReferencesWithRelativePath(rawMarkdown)
        val markdown = (images ++ references).fold(rawMarkdown)((acc, relativePath) => acc.replace(relativePath, s"$baseUrl/api/assets?path=${
          pageJoinProject.directory.path
        }$relativePath"))

        Future.successful(PageFragmentUnderProcessing(markdown = Some(markdown)))

      case PageFragmentUnderProcessing(PageFragmentStatusModule, _, _, _, _, _, _, _, Some(openApiModule), _, _) =>
        openApiClient.getOpenApiDescriptor(openApiModule, pageJoinProject).flatMap(openApiModel => Future.successful(PageFragmentUnderProcessing(openApi = Some(openApiModel))))
      case PageFragmentUnderProcessing(PageFragmentStatusModule, _, _, _, _, _, _, _, _, _, Some(openApiPathModule)) =>
        openApiClient.getOpenApiPathSpec(openApiPathModule, pageJoinProject).flatMap(openApiPath => Future.successful(PageFragmentUnderProcessing(openApiPath = Some(openApiPath))))
      case fragmentUnderProcessing => Future.successful(fragmentUnderProcessing)

    }.map(fragments => fragments.map(PageFragment(_)))).map(_.filter(fragment => fragment.`type` != PageFragment.TypeUnknown))
  }

  def buildFeature(scenariosModule: ScenariosModule, currentPageJoinProject: PageJoinProject): Option[Feature] = {

    val project = scenariosModule.project match {
      case Some(projectId) => projectRepository.findById(projectId).getOrElse(currentPageJoinProject.project)
      case None => currentPageJoinProject.project
    }

    val branchName = scenariosModule.branchName.getOrElse(currentPageJoinProject.branch.name)
    val featureFilter = scenariosModule.feature match {
      case Some(featureRelativePath) => Some(project.featuresRootPath.getOrElse("") + featureRelativePath)
      case None => None
    }
    val tagsFilter = scenariosModule.select match {
      case Some(select) =>
        Some(select.tags)
      case _ => None
    }
    val filter = ProjectMenuItem(project.id, project.name, branchName, featureFilter, tagsFilter)
    val documentation = gherkinRepository.buildProjectGherkin(filter)

    documentation.branches.find(_.name.equals(branchName)).flatMap {
      branch =>
        branch.features.find(_.path.equals(featureFilter.getOrElse("")))
    }
  }

  def findPageModule(pageContent: String): Option[PageMeta] = {
    splitMarkdown(pageContent).find(_.page.isDefined).flatMap(_.page)
  }

  def findPageImagesWithRelativePath(pageContent: String): Seq[String] = (for (m <- ImageRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))

  def findPageReferencesWithRelativePath(pageContent: String): Seq[String] = (for (m <- ReferenceRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))

  def replaceVariablesInMarkdown(content: Seq[PageFragment], variables: Seq[Variable]): Seq[PageFragment] = {
    content.map(pageFragment =>
      if (pageFragment.`type` == "markdown") {
        PageFragment(pageFragment.`type`, PageFragmentContent(replaceVariableInString(pageFragment.data.markdown.getOrElse("there is no markdown"), variables.toIndexedSeq, 0)))
      } else {
        if (pageFragment.`type` == "includeExternalPage") {
          PageFragment(pageFragment.`type`, PageFragmentContent(includeExternalPage = replaceVariableInString(pageFragment.data.includeExternalPage.getOrElse("there is no markdown"), variables.toIndexedSeq, 0)))
        } else {
          pageFragment
        }
      }
    )
  }

  def replaceVariableInString(texte: String, variables: IndexedSeq[Variable], index: Int): Option[String] = {
    if (index != variables.length) {
      replaceVariableInString(texte.replace(variables(index).name, variables(index).value), variables, index + 1)
    } else {
      Option(texte)
    }
  }

  def getAllPagePaths(project: Project, directoryId: Long): Seq[Page] = {
    pageRepository.findAllByDirectoryId(directoryId).map(page => page.copy(path = getCorrectedPath(page.path, project)))
  }

}
