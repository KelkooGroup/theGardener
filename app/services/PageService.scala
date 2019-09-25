package services

import java.io.{File, FileInputStream}

import controllers.dto.PageFragment
import javax.inject.Inject
import models.{PageJoinProject, _}
import org.apache.commons.io.FileUtils
import play.api.Configuration
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
                                       scenariosModule: Option[ScenariosModule] = None,
                                       scenarios: Option[Feature] = None,
                                       includeExternalPage: Option[IncludeExternalPageModule] = None)

class PageService @Inject()(config: Configuration, projectRepository: ProjectRepository, directoryRepository: DirectoryRepository, pageRepository: PageRepository, gherkinRepository: GherkinRepository) {


  implicit val pageMetaFormat = Json.format[PageMeta]
  implicit val directoryMetaFormat = Json.format[DirectoryMeta]

  implicit val tagsModuleFormat = Json.format[TagsModule]
  implicit val scenariosModuleMetaFormat = Json.format[ScenariosModule]
  implicit val metaFormat = Json.format[Module]


  val projectsRootDirectory = config.get[String]("projects.root.directory")
  val documentationMetaFile = config.get[String]("documentation.meta.file")
  val baseUrl = config.getOptional[String]("application.baseUrl").getOrElse("")

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

  def processPage(currentDirectory: Directory, localDirectoryPath: String, name: String, order: Int): Option[Page] = {
    val pageFile = new File(localDirectoryPath + "/" + name + ".md")
    if (pageFile.exists()) {

      readPageContent(pageFile, name).map { case (content, label, description) =>
        pageRepository.save(Page(-1, name, label, description, order, Some(content), currentDirectory.relativePath + name, currentDirectory.path + name, currentDirectory.id))
      }

    } else None
  }

  def processPage(projectId: String, branchName: String, page: Page, documentationRootPath: String): Option[Page] = {
    val pageFile = new File(getLocalRepository(projectId, branchName) + documentationRootPath + "/" + page.relativePath + ".md")
    if (pageFile.exists()) {
      readPageContent(pageFile, page.name).map { case (content, label, description) =>
        pageRepository.save(page.copy(markdown = Some(content), label = label, description = description))
      }

    } else None
  }

  def readPageContent(page: File, name: String): Option[(String, String, String)] = {

    Try(FileUtils.readFileToString(page, "UTF-8")).logError(s"Error while reading content page ${page.getPath}").toOption.map { content =>
      (for {
        metaString <- findPageModuleJson(content)
        meta <- parseModule(metaString, page.getPath).flatMap(_.page)
      } yield (content, meta.label.getOrElse(name), meta.description.orElse(meta.label).getOrElse(name))).getOrElse((content, name, name))
    }
  }

  def parseModule(moduleString: String, path: String): Option[Module] = {
    Try(Json.parse(moduleString.trim).as[Module]).logError(s"Error while parsing module '${moduleString}' of page ${path}").toOption
  }


  def extractMarkdown(pageJoinProject: PageJoinProject): Option[String] = {
    pageJoinProject.page.markdown match {
      case None => None
      case Some(originalMarkdown) =>
        Some(findPageModule(originalMarkdown).map(meta => originalMarkdown.replace(meta, "").trim).getOrElse(originalMarkdown))
    }
  }

  def appendCurrentLine(fragment: PageFragmentUnderProcessing, line: String): PageFragmentUnderProcessing = {
    fragment.data match {
      case Some(data) => fragment.copy(data = Some(data + "\n" + line))
      case None => fragment.copy(data = Some(line))
    }
  }


  def splitMarkdown(markdown: String, path: String): Seq[PageFragmentUnderProcessing] = {
    markdown.split('\n').foldLeft((Seq[PageFragmentUnderProcessing](), new PageFragmentUnderProcessing())) { (fragmentsAndCurrent, line) =>
      fragmentsAndCurrent match {
        case (fragments, currentFragment) =>
          processPageFragmentLine(line, path, fragments, currentFragment)
      }
    }._1
  }

  private def processPageFragmentLine(line: String, path: String, fragments: Seq[PageFragmentUnderProcessing], currentFragment: PageFragmentUnderProcessing): (Seq[PageFragmentUnderProcessing], PageFragmentUnderProcessing) = {
    val currentFragmentWithNewLine = appendCurrentLine(currentFragment, line)
    val newFragmentWithNewLine = appendCurrentLine(new PageFragmentUnderProcessing(), line)

    currentFragment.status match {

      case PageFragmentStatusMakdownEscape =>

        if (line.trim.startsWith(MarkdownEscape)) {
          (fragments :+ currentFragment.copy(markdown = currentFragment.data), newFragmentWithNewLine.copy(status = PageFragmentStatusMakdownEscape))

        } else {
          (fragments, currentFragmentWithNewLine)
        }

      case PageFragmentStatusModule =>

        processPageFragmentModule(line, path, fragments, currentFragment, currentFragmentWithNewLine)

      case _ =>

        if (line.trim.startsWith(MarkdownEscape)) {
          (fragments :+ currentFragment.copy(markdown = currentFragment.data), newFragmentWithNewLine.copy(status = PageFragmentStatusMakdownEscape))

        } else if (line.trim.startsWith(ModuleStart)) {
          (fragments :+ currentFragment.copy(markdown = currentFragment.data), newFragmentWithNewLine.copy(status = PageFragmentStatusModule))

        } else {
          (fragments, currentFragmentWithNewLine)
        }
    }
  }

  private def processPageFragmentModule(line: String, path: String, fragments: Seq[PageFragmentUnderProcessing], currentFragment: PageFragmentUnderProcessing, currentFragmentWithNewLine: PageFragmentUnderProcessing) = {
    if (line.trim.startsWith(MarkdownCodeStart)) {

      val module = currentFragment.data match {
        case Some(data) =>
          parseModule(data.replace(ModuleStart, ""), path)
        case _ => None
      }

      module match {
        case Some(module) =>

          module.scenarios match {

            case Some(scenarios) => (fragments :+ currentFragment.copy(scenariosModule = Some(scenarios)), new PageFragmentUnderProcessing())
            case _ =>

              module.includeExternalPage match {

                case Some(include) => (fragments :+ currentFragment.copy(includeExternalPage = Some(include)), new PageFragmentUnderProcessing())
                case _ =>
                  (fragments, new PageFragmentUnderProcessing())

              }
          }
        case _ =>
          (fragments, new PageFragmentUnderProcessing())
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
                new PageFragmentUnderProcessing(scenarios = feature)
              case _ =>
                fragmentUnderProcessing
            }

          case PageFragmentStatusMakdown =>
            fragmentUnderProcessing.markdown match {
              case Some(rawMarkdown) =>
                val images = findPageImagesWithRelativePath(rawMarkdown)
                val references = findPageReferencesWithRelativePath(rawMarkdown)
                val markdown = (images ++ references).fold(rawMarkdown)((acc, relativePath) => acc.replace(relativePath, s"$baseUrl/api/assets?path=${pageJoinProject.directory.path}$relativePath"))
                new PageFragmentUnderProcessing(markdown = Some(markdown))
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
    val filter = new ProjectMenuItem(project.id, project.name, branchName, featureFilter, tagsFilter)
    val documentation = gherkinRepository.buildProjectGherkin(filter)

    documentation.branches.filter(_.name.equals(branchName)).headOption.map {
      branch =>
        branch.features.filter(_.path.equals(featureFilter.getOrElse(""))).headOption
    }.flatten
  }

  def findPageModule(pageContent: String): Option[String] = PageModuleRegex.findFirstIn(pageContent)

  def findPageModuleJson(pageContent: String): Option[String] = for (m <- PageModuleRegex.findFirstMatchIn(pageContent)) yield m.group(1)

  def findPageImagesWithRelativePath(pageContent: String): Seq[String] = (for (m <- ImageRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))

  def findPageReferencesWithRelativePath(pageContent: String): Seq[String] = (for (m <- ReferenceRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))
}
