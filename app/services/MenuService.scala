package services

import javax.inject._
import models.HierarchyNode._
import models._
import play.api.Configuration
import play.api.cache.SyncCacheApi
import repositories._
import services.MenuService._
import utils._

import scala.concurrent.duration._
import scala.util.Try


class MenuService @Inject()(
  configuration: Configuration,
  hierarchyRepository: HierarchyRepository,
  projectRepository: ProjectRepository,
  branchRepository: BranchRepository,
  featureRepository: FeatureRepository,
  directoryRepository: DirectoryRepository,
  pageRepository: PageRepository,
  cache: SyncCacheApi
) {

  private val cacheTtl: Duration = configuration.getOptional[Long]("cache.ttl").map(_.minutes).getOrElse(Duration.Inf)

  def refreshCache(): Try[Menu] = Try(getMenuTree(true))

  def getMenuListWithShortcut(refresh: Boolean = false): Seq[Menu] = {
    if (refresh) cache.remove(menuListCacheKey)

    cache.getOrElseUpdate(menuListCacheKey, cacheTtl) {

      val mapProjectIdProject = projectRepository.findAll().map(p => p.id -> p).toMap

      val branches = branchRepository.findAll()
      val mapProjectIdBranches = branches.groupBy(_.projectId)

      val mapBranchIdProjectFeaturePath = branches.flatMap(b => mapProjectIdProject(b.projectId).featuresRootPath.map(b.id -> _)).toMap

      val mapBranchIdFeaturePaths = featureRepository.findAllFeaturePaths().groupBy(r => r.branchId).map { branchAndPath =>
        val paths = branchAndPath._2.flatMap { p =>
          mapBranchIdProjectFeaturePath.get(branchAndPath._1).map(_.fixPathSeparator).map { projectFeaturePath =>
            p.path.substring(p.path.indexOf(projectFeaturePath) + projectFeaturePath.length + 1)
          }
        }.toSet

        branchAndPath._1 -> paths
      }

      val pages = pageRepository.findAll().sortBy(_.order).groupBy(_.directoryId)
      val directories = directoryRepository.findAll().map(d => d.copy(pages = pages.getOrElse(d.id, Seq())))

      val directoryTree = directories.groupBy(_.branchId).flatMap { case (branchId, branchDirectories) =>
        val (rootDirectories, children) = branchDirectories.partition(_.relativePath == "/")
        val project = (for {
          branch <- branchRepository.findById(branchId)
          projectFound <- projectRepository.findById(branch.projectId)
        } yield projectFound).getOrElse(Project("", "", "", None, "", None, None))
        rootDirectories.headOption.map(rootDirectory => branchId -> buildDirectoryTree(rootDirectory, children, project))
      }

      hierarchyRepository.findAll().map { hierarchyNode =>
        val projects = projectRepository.findAllByHierarchyId(hierarchyNode.id).map { project =>
          project.copy(branches = Some(
            mapProjectIdBranches.getOrElse(project.id, Seq()).filter(branch => project.displayedBranches.forall(regex => branch.name.matches(regex))).map { branch =>
              branch.copy(features = mapBranchIdFeaturePaths.getOrElse(branch.id, Set()).toList.sorted, rootDirectory = directoryTree.get(branch.id))
            }
          ))
        }
        val directory: Option[Directory] = hierarchyNode.directoryPath.flatMap { directoryPath =>
          directoryRepository.findByPath(directoryPath).map { d =>
            val pages = pageRepository.findAllByDirectoryId(d.id)
            (for {
              branch <- branchRepository.findById(d.branchId)
              project <- projectRepository.findById(branch.projectId)
            } yield d.copy(path = getCorrectedPath(d.path, project), pages = pages)).getOrElse(d)
          }

        }
        Menu(hierarchyNode.id, Seq(hierarchyNode), projects.sortBy(_.id), Seq(), directory, hierarchyNode.shortcut)
      }.sortBy(_.id)
    }
  }

  def getMenuTree(refresh: Boolean = false): Menu = {
    if (refresh) cache.remove(menuTreeCacheKey)

    cache.getOrElseUpdate(menuTreeCacheKey, cacheTtl) {

      val menuWithShortcutsList = getMenuListWithShortcut(refresh)

      val menuWithShortcutsTree = menuWithShortcutsList.headOption
        .map(c => c.copy(children = buildTree(c, menuWithShortcutsList.tail)))
        .getOrElse(Menu("", Seq()))

      resolveShortcutsInMenuTree(menuWithShortcutsTree)
    }
  }
}

object MenuService {

  val menuListCacheKey = "menuList"
  val menuTreeCacheKey = "menuTree"

  def isChild(separator: String, parent: String, child: String): Boolean = {
    val childId = child.split(s"\\$separator").toSeq.filterNot(_.isEmpty)
    val parentId = parent.split(s"\\$separator").toSeq.filterNot(_.isEmpty)

    childId.size > parentId.size && (parentId.isEmpty || childId.startsWith(parentId)) && childId.drop(parentId.size).size == 1
  }


  def resolveShortcutsInMenuTree(menuTreeRoot: Menu): Menu = {

    def getById(id: String): Option[Menu] = {
      findMenuSubtree(id)(menuTreeRoot)
    }

    def internResolveShortcutsInMenuTree(menu: Menu): Unit = {
      menu.shortcut match {
        case Some(shortcut) =>
          getById(shortcut) match {
            case Some(shortcutTarget) =>
              menu.projects = shortcutTarget.projects
              menu.children = shortcutTarget.children
              menu.directory = shortcutTarget.directory
            case _ =>
          }
          menu.shortcut = None
        case _ =>
      }
      menu.children.foreach(child => internResolveShortcutsInMenuTree(child))
    }

    menuTreeRoot.children.foreach(child => internResolveShortcutsInMenuTree(child))
    menuTreeRoot
  }

  def buildTree(node: Menu, children: Seq[Menu]): Seq[Menu] = {
    val (taken, left) = children.partition(child => isChild(idSeparator, node.id, child.id))

    taken.map {
      c =>
        val menu = c.copy(hierarchy = node.hierarchy ++ c.hierarchy)
        menu.copy(children = buildTree(menu, left))
    }
  }

  def findMenuSubtree(id: String)(menu: Menu): Option[Menu] = {
    if (id == menu.id) Some(menu)
    else if (menu.children.isEmpty) None
    else menu.children.flatMap(findMenuSubtree(id)).headOption
  }

  def findMenuSubtree(hierarchy: Seq[String])(menu: Menu): Option[Menu] = {
    if (hierarchy == menu.hierarchy.map(_.slugName).filterNot(_ == "root")) Some(menu)
    else if (menu.children.isEmpty) None
    else menu.children.flatMap(findMenuSubtree(hierarchy)).headOption
  }

  def mergeChildrenHierarchy(menu: Menu): Seq[HierarchyNode] = {
    (menu.hierarchy ++ menu.children.flatMap(c => c.hierarchy ++ mergeChildrenHierarchy(c))).distinct
  }

  def buildDirectoryTree(currentDirectory: Directory, directories: Seq[Directory], project: Project): Directory = {
    val (children, left) = directories.partition(d => isChild("/", currentDirectory.relativePath, d.relativePath))

    currentDirectory.copy(children = children.map(d => buildDirectoryTree(d, left, project)), path = getCorrectedPath(currentDirectory.path, project))

  }

  def getCorrectedPath(path: String, project: Project): String = {
    if (project.stableBranch == project.displayedBranches.getOrElse("")) {
      path.replace(project.stableBranch, "")
    } else {
      path
    }

  }
}
