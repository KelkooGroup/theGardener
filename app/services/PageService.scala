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

case class DirectoryMeta(label: String, description: String, pages: List[String], children: List[String])

case class Meta(directory: DirectoryMeta)


class PageService @Inject()(config: Config, directoryRepository: DirectoryRepository, pageRepository: PageRepository) {
  val projectsRootDirectory = config.getString("projects.root.directory")
  val documentationMetaFile = config.getString("documentation.meta.file")
  implicit val directoryMetaFormat = Json.format[DirectoryMeta]
  implicit val metaFormat = Json.format[Meta]

  def getLocalRepository(projectId: String, branch: String): String = s"$projectsRootDirectory$projectId/$branch/".fixPathSeparator

  def processDirectory(branch: Branch, path: String, localDirectoryPath: String, relativePath: String = "/", order: Int = 0, isRoot: Boolean = true): Option[Directory] = {
    val directoryTheGardenerFile = new File(localDirectoryPath + "/" + documentationMetaFile)
    if (directoryTheGardenerFile.exists()) {
      Try {
        val meta = Json.parse(new FileInputStream(directoryTheGardenerFile)).as[Meta]
        val pathSplit = path.split("/")

        val name = if (isRoot) "root" else pathSplit(pathSplit.length - 1)

        val currentDirectory = directoryRepository.save(Directory(-1, name, meta.directory.label, meta.directory.description, order, relativePath, path, branch.id))
        val pages: Seq[Page] = meta.directory.pages.zipWithIndex.flatMap { case (pagePath, index) => processPage(currentDirectory, localDirectoryPath, pagePath, index) }
        val children: Seq[Directory] = meta.directory.children.zipWithIndex.flatMap { case (childName, index) => processDirectory(branch, path + childName + "/", localDirectoryPath + "/" + childName, relativePath + childName + "/", index, isRoot = false) }

        if (pages.isEmpty && children.isEmpty) {
          directoryRepository.delete(currentDirectory)
        }

        Directory(currentDirectory.id, currentDirectory.name, currentDirectory.label, currentDirectory.description, currentDirectory.order, currentDirectory.relativePath, currentDirectory.path, currentDirectory.branchId, pages.map(_.path), children)

      }.logError(s"Error while parsing directory $path").toOption
    } else None
  }

  def processPage(currentDirectory: Directory, localDirectoryPath: String, name: String, order: Int): Option[Page] = {
    val pageFile = new File(localDirectoryPath + "/" + name + ".md")
    if (pageFile.exists()) {
      Try {
        val pageContent = FileUtils.readFileToString(pageFile, "UTF-8")
        pageRepository.save(Page(-1, name, name, name, order, pageContent, currentDirectory.relativePath + name, currentDirectory.path + name, currentDirectory.id))

      }.logError(s"Error while parsing page ${pageFile.getPath}").toOption
    } else None
  }
}
