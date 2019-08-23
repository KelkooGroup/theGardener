package services

import java.io.{File, FileInputStream}

import com.typesafe.config.Config
import javax.inject.Inject
import models._
import org.apache.commons.io.FileUtils
import play.api.libs.json.Json
import repository._
import utils._

import scala.util.Try

case class PageMeta(label: String, description: String)

case class DirectoryMeta(label: String, description: String, pages: Option[Seq[String]], children: Option[Seq[String]])

case class Meta(directory: Option[DirectoryMeta], page: Option[PageMeta])


class PageService @Inject()(config: Config, directoryRepository: DirectoryRepository, pageRepository: PageRepository) {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val documentationMetaFile = config.getString("documentation.meta.file")
  implicit val pageMetaFormat = Json.format[PageMeta]
  implicit val directoryMetaFormat = Json.format[DirectoryMeta]
  implicit val metaFormat = Json.format[Meta]

  val pageRegex = "\\`\\`\\`thegardener([\\s\\S]*\"page\"[\\s\\S]*)\\`\\`\\`".r

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def processDirectory(branch: Branch, path: String, localDirectoryPath: String, relativePath: String = "/", order: Int = 0, isRoot: Boolean = true): Option[Directory] = {
    val metaFile = new File(localDirectoryPath + "/" + documentationMetaFile)
    if (metaFile.exists()) {
      Try {
        val meta = Json.parse(new FileInputStream(metaFile)).as[Meta]
        val pathSplit = path.split("/")

        val name = if (isRoot) "root" else pathSplit(pathSplit.length - 1)

        val currentDirectory = directoryRepository.save(Directory(-1, name, meta.directory.map(_.label).getOrElse(name), meta.directory.map(_.description).getOrElse(name), order, relativePath, path, branch.id))

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
      Try {
        val pageContent = FileUtils.readFileToString(pageFile, "UTF-8")


        val (label, description) = (for {
          metaString <- findPageMeta(pageContent)
          meta <- Try(Json.parse(metaString).as[Meta]).toOption
          page <- meta.page
        } yield (page.label, page.description)).getOrElse((name, name))

        pageRepository.save(Page(-1, name, label, description, order, Some(pageContent), currentDirectory.relativePath + name, currentDirectory.path + name, currentDirectory.id))

      }.logError(s"Error while parsing page ${pageFile.getPath}").toOption
    } else None
  }

  def findPageMeta(pageContent: String): Option[String] = for (m <- pageRegex.findFirstMatchIn(pageContent)) yield m.group(1)
}
