package services

import java.io.{File, FileInputStream}

import controllers.dto.{PageFragment, PageFragmentContent}
import javax.inject.Inject
import models.{PageJoinProject, _}
import org.apache.commons.io.FileUtils
import play.api.{Configuration, Logging}
import play.api.cache.SyncCacheApi
import play.api.libs.json.Json
import repositories._
import utils._

import scala.util.Try

case class PageMeta(label: Option[String] = None, description: Option[String] = None)

case class DirectoryMeta(label: Option[String] = None, description: Option[String] = None, pages: Option[Seq[String]] = None, children: Option[Seq[String]] = None)

case class TagsModule(tags: Seq[String])

case class ScenariosModule(project: Option[String] = None, branchName: Option[String] = None, feature: Option[String] = None, select: Option[TagsModule] = None)

case class IncludeExternalPageModule(url: String)

object IncludeExternalPageModule {
  implicit val pageFormat = Json.format[IncludeExternalPageModule]
}

case class Module(directory: Option[DirectoryMeta] = None,
                  page: Option[PageMeta] = None,
                  scenarios: Option[ScenariosModule] = None,
                  includeExternalPage: Option[IncludeExternalPageModule] = None)

sealed trait PageFragmentUnderProcessingStatus

case object PageFragmentStatusMakdown extends PageFragmentUnderProcessingStatus

case object PageFragmentStatusMakdownEscape extends PageFragmentUnderProcessingStatus

case object PageFragmentStatusModule extends PageFragmentUnderProcessingStatus

case class PageFragmentUnderProcessing(status: PageFragmentUnderProcessingStatus = PageFragmentStatusMakdown,
                                       data: Option[String] = None,
                                       markdown: Option[String] = None,
                                       page: Option[PageMeta] = None,
                                       scenariosModule: Option[ScenariosModule] = None,
                                       scenarios: Option[Feature] = None,
                                       includeExternalPage: Option[IncludeExternalPageModule] = None)

case class PageWithContent(page: Page, content: Seq[PageFragment])

class PageService @Inject()(config: Configuration, projectRepository: ProjectRepository, directoryRepository: DirectoryRepository, pageRepository: PageRepository, gherkinRepository: GherkinRepository, cache: SyncCacheApi) extends Logging {


  implicit val pageMetaFormat = Json.format[PageMeta]
  implicit val directoryMetaFormat = Json.format[DirectoryMeta]

  implicit val tagsModuleFormat = Json.format[TagsModule]
  implicit val scenariosModuleMetaFormat = Json.format[ScenariosModule]
  implicit val metaFormat = Json.format[Module]


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

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def getPagePath(projectId: String, branch: String, path: String, documentationRootPath: String): String = s"$projectId>$branch>${path.substring(path.indexOf(documentationRootPath) + documentationRootPath.length, path.length)}".replace(".md", "")

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

  def computePageFromPath(path: String, refresh: Boolean = false): Option[PageWithContent] = {
    if (refresh) {
      computePageFromPathUsingDatabase(path)
    } else {
      cache.get[PageWithContent](computePageCacheKey(path)) match {
        case Some(page) => {
          Some(page)
        }
        case None => {
          computePageFromPathUsingDatabase(path)
        }
      }
    }
  }

  def replacePageInCache(path: String, page: PageWithContent): Unit = {
    cache.set(computePageCacheKey(path), page)
  }


  private def computePageCacheKey(path: String): String = s"page_$path"

  def computePageFromPathUsingDatabase(path: String): Option[PageWithContent] = {
    pageRepository.findByPathJoinProject(path) match {
      case Some(pageJoinProject) =>

        pageJoinProject.page.markdown.map { markdown =>
          splitMarkdown(s"$markdown\n$MarkdownEnd", path)
        }.map { fragments =>
          processPageFragments(fragments, pageJoinProject)
        } match {
          case Some(fragments) => {
            logger.debug(s"Page computed : $path")
            val page = PageWithContent(pageJoinProject.page, fragments)
            replacePageInCache(path,page)
            Some(page)
          }
          case _ => None
        }
      case _ => None
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
      case Some(page) => cache.set(computePageCacheKey(page.path), computePageFromPathUsingDatabase(page.path))
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

  private def processPageFragmentLine(line: String, path: String, fragments: Seq[PageFragmentUnderProcessing], currentFragment: PageFragmentUnderProcessing): (Seq[PageFragmentUnderProcessing], PageFragmentUnderProcessing) = {
    val currentFragmentWithNewLine = appendCurrentLine(currentFragment, line)
    val newFragmentWithNewLine = appendCurrentLine(PageFragmentUnderProcessing(), line)

    currentFragment.status match {

      case PageFragmentStatusMakdownEscape =>

        if (line.trim.startsWith(MarkdownEscape)) {
          (fragments, currentFragmentWithNewLine.copy(status = PageFragmentStatusMakdown))

        } else {
          (fragments, currentFragmentWithNewLine)
        }

      case PageFragmentStatusModule =>

        processPageFragmentModule(line, path, fragments, currentFragment, currentFragmentWithNewLine)

      case _ =>

        if (line.trim.startsWith(MarkdownEnd)) {
          (fragments :+ currentFragment.copy(markdown = currentFragment.data), PageFragmentUnderProcessing())

        } else if (line.trim.startsWith(MarkdownEscape)) {
          (fragments, currentFragmentWithNewLine.copy(status = PageFragmentStatusMakdownEscape))

        } else if (line.trim.startsWith(ModuleStart)) {
          (fragments :+ currentFragment.copy(markdown = currentFragment.data), newFragmentWithNewLine.copy(status = PageFragmentStatusModule))

        } else {
          (fragments, currentFragmentWithNewLine)
        }
    }
  }

  private def processPageFragmentModule(line: String, path: String, fragments: Seq[PageFragmentUnderProcessing], currentFragment: PageFragmentUnderProcessing, currentFragmentWithNewLine: PageFragmentUnderProcessing) = {
    if (line.trim.startsWith(MarkdownCodeStart)) {

      currentFragment.data.flatMap(data => parseModule(data.replace(ModuleStart, ""), path)) match {
        case Some(module) =>

          module.page match {
            case Some(page) => (fragments :+ currentFragment.copy(page = Some(page)), PageFragmentUnderProcessing())
            case _ =>

              module.scenarios match {

                case Some(scenarios) => (fragments :+ currentFragment.copy(scenariosModule = Some(scenarios)), PageFragmentUnderProcessing())
                case _ =>

                  module.includeExternalPage match {

                    case Some(include) => (fragments :+ currentFragment.copy(includeExternalPage = Some(include)), PageFragmentUnderProcessing())
                    case _ =>
                      (fragments, PageFragmentUnderProcessing())

                  }
              }
          }
        case _ =>
          (fragments, PageFragmentUnderProcessing())
      }
    } else {
      (fragments, currentFragmentWithNewLine)
    }
  }

  def processPageFragments(fragments: Seq[PageFragmentUnderProcessing], pageJoinProject: PageJoinProject): Seq[PageFragment] = {
    fragments.map {
      fragmentUnderProcessing =>

        fragmentUnderProcessing.status match {

          case PageFragmentStatusModule =>
            fragmentUnderProcessing.scenariosModule match {
              case Some(scenariosModule) =>
                val feature = buildFeature(scenariosModule, pageJoinProject)
                PageFragmentUnderProcessing(scenarios = feature)
              case _ =>
                fragmentUnderProcessing
            }

          case PageFragmentStatusMakdown =>
            fragmentUnderProcessing.markdown match {
              case Some(rawMarkdown) =>
                val images = findPageImagesWithRelativePath(rawMarkdown)
                val references = findPageReferencesWithRelativePath(rawMarkdown)
                val markdown = (images ++ references).fold(rawMarkdown)((acc, relativePath) => acc.replace(relativePath, s"$baseUrl/api/assets?path=${pageJoinProject.directory.path}$relativePath"))
                PageFragmentUnderProcessing(markdown = Some(markdown))
              case _ =>
                fragmentUnderProcessing
            }

          case _ =>
            fragmentUnderProcessing
        }

    }.map {
      fragment =>
        PageFragment(fragment)
    }.filter(fragment => fragment.`type` != PageFragment.TypeUnknown)
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

    documentation.branches.find(_.name.equals(branchName)).flatMap { branch =>
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
          PageFragment(pageFragment.`type`, PageFragmentContent( includeExternalPage = replaceVariableInString(pageFragment.data.includeExternalPage.getOrElse("there is no markdown"), variables.toIndexedSeq, 0)))
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

}
