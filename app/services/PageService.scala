package services

import java.io.{File, FileInputStream}

import com.typesafe.config.Config
import controllers.dto.PageFragment
import javax.inject.Inject
import models.{PageJoinProject, _}
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import repositories._
import utils._

import scala.util.Try

case class PageMeta(label: Option[String] = None, description: Option[String] = None)

case class DirectoryMeta(label: Option[String] = None, description: Option[String] = None, pages: Option[Seq[String]] = None, children: Option[Seq[String]] = None)

case class TagsModule(tags: Seq[String])

case class ScenariosModule(project: Option[String] = None, branchName: Option[String] = None, feature: Option[String] = None, select: Option[TagsModule] = None)

case class Module(directory: Option[DirectoryMeta] = None, page: Option[PageMeta] = None, scenarios: Option[ScenariosModule] = None)

case class PageFragmentUnderProcessing(data: Option[String] = None, markdown: Option[String] = None, scenariosModule: Option[ScenariosModule] = None, scenarios: Option[Feature] = None)

class PageService @Inject()(config: Config, projectRepository: ProjectRepository, directoryRepository: DirectoryRepository, pageRepository: PageRepository, gherkinRepository: GherkinRepository) {


  implicit val pageMetaFormat = Json.format[PageMeta]
  implicit val directoryMetaFormat = Json.format[DirectoryMeta]

  implicit val tagsModuleFormat = Json.format[TagsModule]
  implicit val scenariosModuleMetaFormat = Json.format[ScenariosModule]
  implicit val metaFormat = Json.format[Module]


  val projectsRootDirectory = config.getString("projects.root.directory")
  val documentationMetaFile = config.getString("documentation.meta.file")
  val baseUrl = config.getString("application.baseUrl")

  val moduleStart = "```thegardener"
  val moduleEnd = "```"
  val pageModuleRegex = """\`\`\`thegardener([\s\S]*"page"[^`]*)\`\`\`""".r
  val imageRegex = """\!\[.*\]\((.*)\)""".r
  val referenceRegex = """\[.*\]\:\s(\S*)""".r
  val scenariosModuleRegex = """\`\`\`thegardener([\s\S]*"scenarios"[^`]*)\`\`\`""".r

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
    Try(Json.parse(moduleString).as[Module]).logError(s"Error while parsing module '${moduleString}' of page ${path}").toOption
  }

  def extractMarkdown(pageJoinProject: PageJoinProject): Option[String] = {
    pageJoinProject.page.markdown match {
      case None => None
      case Some(originalMarkdown) =>
        val markdownWithoutPageModule = findPageModule(originalMarkdown).map(meta => originalMarkdown.replace(meta, "").trim).getOrElse(originalMarkdown)
        val images = findPageImagesWithRelativePath(markdownWithoutPageModule)
        val references = findPageReferencesWithRelativePath(markdownWithoutPageModule)
        Some((images ++ references).fold(markdownWithoutPageModule)((acc, relativePath) => acc.replace(relativePath, s"$baseUrl/api/assets?path=${pageJoinProject.page.path}/$relativePath")))
    }
  }

  def splitMarkdown(markdown: String, path: String): Seq[PageFragmentUnderProcessing] = {
    scenariosModuleRegex.findFirstMatchIn(markdown) match {
      case Some(scenarios) =>

        val scenariosModuleString = scenarios.group(1)
        val scenariosModuleJson: Option[Module] = parseModule(scenariosModuleString, path)
        val scenariosModuleOriginal = moduleStart + scenariosModuleString + moduleEnd
        val markdownBeforeScenarios = markdown.substring(0, markdown.indexOf(scenariosModuleOriginal))
        val dataAfterScenario = markdown.substring(markdownBeforeScenarios.length + scenariosModuleOriginal.length, markdown.length - 1)

        val ignore = new PageFragmentUnderProcessing(markdown = Some(markdownBeforeScenarios)) :: new PageFragmentUnderProcessing(data = Some(dataAfterScenario)) :: Nil
        scenariosModuleJson match {
          case Some(scenariosModuleJson) => scenariosModuleJson.scenarios  match {
            case Some(scenarios) =>
              new PageFragmentUnderProcessing(markdown = Some(markdownBeforeScenarios)) :: new PageFragmentUnderProcessing(scenariosModule = Some(scenarios)) :: new PageFragmentUnderProcessing(markdown = Some(dataAfterScenario)) :: Nil
            case None => ignore
          }
          case None => ignore
        }

      case None => new PageFragmentUnderProcessing(markdown = Some(markdown)) :: Nil
    }
  }

  def processPageFragments(fragments: Seq[PageFragmentUnderProcessing], pageJoinProject: PageJoinProject): Seq[PageFragment] = {
    fragments.map { fragmentUnderProcessing =>
      fragmentUnderProcessing.scenariosModule match {
        case Some(scenariosModule) =>
          val feature = buildFeature(scenariosModule, pageJoinProject)
          new PageFragmentUnderProcessing(scenarios = feature)

        case None => fragmentUnderProcessing
      }
    }.map { fragment =>
      PageFragment(fragment)
    }
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
    val tagsFilter = None
    val filter = new ProjectMenuItem(project.id, "", branchName, featureFilter, tagsFilter)
    val documentation = gherkinRepository.buildProjectGherkin(filter)

    documentation.branches.filter(_.name.equals(branchName)).headOption.map { branch =>
      branch.features.filter(_.path.equals(featureFilter.getOrElse(""))).headOption
    }.flatten
  }

  def findPageModule(pageContent: String): Option[String] = pageModuleRegex.findFirstIn(pageContent)

  def findPageModuleJson(pageContent: String): Option[String] = for (m <- pageModuleRegex.findFirstMatchIn(pageContent)) yield m.group(1)

  def findPageImagesWithRelativePath(pageContent: String): Seq[String] = (for (m <- imageRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))

  def findPageReferencesWithRelativePath(pageContent: String): Seq[String] = (for (m <- referenceRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))
}
