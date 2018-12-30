package services

import javax.inject.Inject
import models.HierarchyNode._
import models._
import play.api.cache._
import repository._
import services.CriteriaService._
import utils._

import scala.util.Try

class CriteriaService @Inject()(hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository, branchRepository: BranchRepository, featureRepository: FeatureRepository, cache: SyncCacheApi) {

  def refreshCache(): Unit = Try(getCriteriasTree(true))

  def getCriterias(refresh: Boolean = false): Seq[Criteria] = {
    if (refresh) cache.remove(criteriasListCacheKey)

    cache.getOrElseUpdate(criteriasListCacheKey) {

      val mapProjectIdProject = projectRepository.findAll().foldLeft(Map[String, Project]()) { (mapProjectIdProject, project) =>
        mapProjectIdProject + (project.id -> project)
      }

      val branches = branchRepository.findAll()
      val mapProjectIdBranches = branches.groupBy(r => r.projectId).foldLeft(Map[String, Seq[Branch]]()) { (mapProjectIdBranches, branch) =>
        val branches = branch._2.foldLeft(Seq[Branch]())((branches, b) => branches :+ b)
        mapProjectIdBranches + (branch._1 -> branches)
      }

      val mapBranchIdProjectFeaturePath = branches.foldLeft(Map[Long, String]()) { (mapBranchIdProjectFeaturePath, branch) =>
        mapBranchIdProjectFeaturePath + (branch.id -> mapProjectIdProject(branch.projectId).featuresRootPath)
      }

      val mapBranchIdFeaturePaths = featureRepository.findAllFeaturePaths().groupBy(r => r.branchId).foldLeft(Map[Long, Set[String]]()) { (mapBranchIdFeaturePaths, branchAndPath) =>
        val paths = branchAndPath._2.foldLeft(Set[String]()) { (paths, p) =>
          val projectFeaturePath = mapBranchIdProjectFeaturePath(branchAndPath._1).fixPathSeparator
          val relativePath = p.path.substring(p.path.indexOf(projectFeaturePath) + projectFeaturePath.length + 1)
          paths + relativePath
        }
        mapBranchIdFeaturePaths + (branchAndPath._1 -> paths)
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
        Criteria(hierarchyNode.id, Seq(hierarchyNode), projects.sortBy(_.id))
      }.sortBy(_.id)
    }
  }

  def getCriteriasTree(refresh: Boolean = false): Criteria = {
    if (refresh) cache.remove(criteriasTreeCacheKey)

    cache.getOrElseUpdate(criteriasTreeCacheKey) {

      val criterias = getCriterias(refresh)

      criterias.headOption
        .map(c => c.copy(children = buildTree(c, criterias.tail)))
        .getOrElse(Criteria("", Seq()))
    }
  }
}

object CriteriaService {

  val criteriasListCacheKey = "criteriasList"
  val criteriasTreeCacheKey = "criteriasTree"

  def isChild(parent: Criteria)(child: Criteria): Boolean = {
    val childId = child.id.split(s"\\$idSeparator").toSeq.filterNot(_.isEmpty)
    val parentId = parent.id.split(s"\\$idSeparator").toSeq.filterNot(_.isEmpty)

    childId.size > parentId.size && (parentId.isEmpty || childId.startsWith(parentId)) && childId.drop(parentId.size).size == 1
  }

  def buildTree(node: Criteria, children: Seq[Criteria]): Seq[Criteria] = {
    val (taken, left) = children.partition(isChild(node))

    taken.map { c =>
      val criteria = c.copy(hierarchy = node.hierarchy ++ c.hierarchy)
      criteria.copy(children = buildTree(criteria, left))
    }
  }

  def findCriteriasSubtree(id: String)(criteria: Criteria): Option[Criteria] = {
    if (criteria.id == id) Some(criteria)
    else if (criteria.children.isEmpty) None
    else criteria.children.flatMap(findCriteriasSubtree(id)).headOption
  }

  def mergeChildrenHierarchy(criteria: Criteria): Seq[HierarchyNode] = {
    (criteria.hierarchy ++ criteria.children.flatMap(c => c.hierarchy ++ mergeChildrenHierarchy(c))).distinct
  }
}
