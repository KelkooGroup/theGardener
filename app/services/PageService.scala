package services

import java.io.{File, FileInputStream}

import com.typesafe.config.Config
import javax.inject.Inject
import models._
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import repositories._
import utils._

import scala.util.Try

case class PageMeta(label: Option[String], description: Option[String])

case class DirectoryMeta(label: Option[String], description: Option[String], pages: Option[Seq[String]], children: Option[Seq[String]])

case class Meta(directory: Option[DirectoryMeta], page: Option[PageMeta])


class PageService @Inject()(config: Config, directoryRepository: DirectoryRepository, pageRepository: PageRepository) {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val documentationMetaFile = config.getString("documentation.meta.file")
  implicit val pageMetaFormat = Json.format[PageMeta]
  implicit val directoryMetaFormat = Json.format[DirectoryMeta]
  implicit val metaFormat = Json.format[Meta]

  val metaRegex = """\`\`\`thegardener([\s\S]*"page"[^`]*)\`\`\`""".r
  val imageRegex = """\!\[.*\]\((.*)\)""".r
  val referenceRegex = """\[.*\]\:\s(\S*)""".r

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def getPagePath(projectId: String, branch: String, path: String, documentationRootPath: String): String = s"$projectId>$branch>${path.substring(path.indexOf(documentationRootPath) + documentationRootPath.length, path.length)}".replace(".md", "")

  def processDirectory(branch: Branch, path: String, localDirectoryPath: String, relativePath: String = "/", order: Int = 0, isRoot: Boolean = true): Option[Directory] = {
    val metaFile = new File(localDirectoryPath + "/" + documentationMetaFile)
    if (metaFile.exists()) {
      Try {
        val meta = Json.parse(new FileInputStream(metaFile)).as[Meta]
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
        metaString <- findPageMetaJson(content)
        meta <- Try(Json.parse(metaString).as[Meta]).logError(s"Error while parsing meta of page ${page.getPath}").toOption.flatMap(_.page)
      } yield (content, meta.label.getOrElse(name), meta.description.orElse(meta.label).getOrElse(name))).getOrElse((content, name, name))
    }
  }

  def findPageMeta(pageContent: String): Option[String] = metaRegex.findFirstIn(pageContent)

  def findPageMetaJson(pageContent: String): Option[String] = for (m <- metaRegex.findFirstMatchIn(pageContent)) yield m.group(1)


  def findPageImagesWithRelativePath(pageContent: String): Seq[String] = (for (m <- imageRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))

  def findPageReferencesWithRelativePath(pageContent: String): Seq[String] = (for (m <- referenceRegex.findAllMatchIn(pageContent)) yield m.group(1)).toSeq.filterNot(_.startsWith("http"))
}
