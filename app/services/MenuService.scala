package services

import javax.inject.Inject
import models.HierarchyNode._
import models._
import play.api.cache._
import repository._
import services.MenuService._
import utils._

import scala.util.Try

class MenuService @Inject()(hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository, branchRepository: BranchRepository, featureRepository: FeatureRepository, cache: SyncCacheApi) {

  def refreshCache(): Unit = Try(getMenuTree(true))

  def getMenu(refresh: Boolean = false): Seq[Menu] = {
    if (refresh) cache.remove(menuListCacheKey)

    cache.getOrElseUpdate(menuListCacheKey) {

      val mapProjectIdProject = projectRepository.findAll().map(p => p.id -> p).toMap

      val branches = branchRepository.findAll()
      val mapProjectIdBranches = branches.groupBy(_.projectId)

      val mapBranchIdProjectFeaturePath = branches.map(b => b.id -> mapProjectIdProject(b.projectId).featuresRootPath).toMap

      val mapBranchIdFeaturePaths = featureRepository.findAllFeaturePaths().groupBy(r => r.branchId).map { branchAndPath =>
        val paths = branchAndPath._2.map { p =>
          val projectFeaturePath = mapBranchIdProjectFeaturePath(branchAndPath._1).fixPathSeparator
          p.path.substring(p.path.indexOf(projectFeaturePath) + projectFeaturePath.length + 1)
        }.toSet

        branchAndPath._1 -> paths
      }

      hierarchyRepository.findAll().map { hierarchyNode =>
        val projects = projectRepository.findAllByHierarchyId(hierarchyNode.id).map { project =>
          val projectWithBranches = project.copy(branches = Some(
            mapProjectIdBranches.getOrElse(project.id, Seq()).map { branch =>
              val branchWithFeatures = branch.copy(features = mapBranchIdFeaturePaths.getOrElse(branch.id, Set()).toList.sorted)
              branchWithFeatures
            }
          ))
          projectWithBranches
        }
        Menu(hierarchyNode.id, Seq(hierarchyNode), projects.sortBy(_.id))
      }.sortBy(_.id)
    }
  }

  def getMenuTree(refresh: Boolean = false): Menu = {
    if (refresh) cache.remove(menuTreeCacheKey)

    cache.getOrElseUpdate(menuTreeCacheKey) {

      val menu = getMenu(refresh)

      menu.headOption
        .map(c => c.copy(children = buildTree(c, menu.tail)))
        .getOrElse(Menu("", Seq()))
    }
  }
}

object MenuService {

  val menuListCacheKey = "menuList"
  val menuTreeCacheKey = "menuTree"

  def isChild(parent: Menu)(child: Menu): Boolean = {
    val childId = child.id.split(s"\\$idSeparator").toSeq.filterNot(_.isEmpty)
    val parentId = parent.id.split(s"\\$idSeparator").toSeq.filterNot(_.isEmpty)

    childId.size > parentId.size && (parentId.isEmpty || childId.startsWith(parentId)) && childId.drop(parentId.size).size == 1
  }

  def buildTree(node: Menu, children: Seq[Menu]): Seq[Menu] = {
    val (taken, left) = children.partition(isChild(node))

    taken.map { c =>
      val menu = c.copy(hierarchy = node.hierarchy ++ c.hierarchy)
      menu.copy(children = buildTree(menu, left))
    }
  }

  def findMenuSubtree(id: String)(menu: Menu): Option[Menu] = {
    if (menu.id == id) Some(menu)
    else if (menu.children.isEmpty) None
    else menu.children.flatMap(findMenuSubtree(id)).headOption
  }

  def mergeChildrenHierarchy(menu: Menu): Seq[HierarchyNode] = {
    (menu.hierarchy ++ menu.children.flatMap(c => c.hierarchy ++ mergeChildrenHierarchy(c))).distinct
  }
}
