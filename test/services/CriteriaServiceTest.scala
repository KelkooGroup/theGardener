package services

import models.{Criteria, HierarchyNode}
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatest.mockito._
import repository.FeatureRepository
import services.CriteriaService._

class CriteriaServiceTest extends WordSpec with MustMatchers with MockitoSugar {

  val featureRepository = mock[FeatureRepository]

  val rootNode = HierarchyNode(".", "root", " Hierarchy root", "Views", "View")
  val engNode = HierarchyNode(".01.", "eng", "Engineering view", "System groups", "System group")
  val libraryNode = HierarchyNode(".01.01.", "library", "Library system group", "Systems", "System")
  val suggestionNode = HierarchyNode(".01.01.01.", "suggestion", "Suggestion system", "Projects", "Project")
  val productNode = HierarchyNode(".02.", "product", "Product view ", "System groups", "System group")

  val root = Criteria(rootNode.id, Seq(rootNode))
  val eng = Criteria(engNode.id, Seq(engNode))
  val library = Criteria(libraryNode.id, Seq(libraryNode))
  val suggestion = Criteria(suggestionNode.id, Seq(suggestionNode))
  val product = Criteria(productNode.id, Seq(productNode))

  val criteriasSubtree = library.copy(hierarchy = Seq(rootNode, engNode, libraryNode), children = Seq(
    suggestion.copy(hierarchy = Seq(rootNode, engNode, libraryNode, suggestionNode))))

  val criteriasTree = root.copy(children = Seq(
    eng.copy(hierarchy = Seq(rootNode, engNode), children = Seq(criteriasSubtree)),
    product.copy(hierarchy = Seq(rootNode, productNode))))

  "CriteriaService" should {
    "find if a hierarchy node is the child of another" in {
      assert(isChild(root)(eng))
      assert(isChild(root)(product))
      assert(isChild(eng)(library))
      assert(isChild(library)(suggestion))
      assert(!isChild(root)(library))
      assert(!isChild(library)(root))
      assert(!isChild(library)(eng))
      assert(!isChild(root)(suggestion))
      assert(!isChild(eng)(suggestion))
      assert(!isChild(eng)(product))
    }

    "build a tree with the hierarchy nodes" in {
      val nodes = Seq(eng, library, suggestion, product)

      val actualTree = buildTree(root, nodes)

      val expectedTree = criteriasTree.children

      actualTree must contain theSameElementsInOrderAs expectedTree
    }

    "build a subtree with the hierarchy node slug name" in {

      findCriteriasSubtree(".01.01.")(criteriasTree) mustBe Some(criteriasSubtree)
    }

    "merge children hierarchy" in {
      mergeChildrenHierarchy(criteriasTree) must contain theSameElementsAs Seq(rootNode, engNode, libraryNode, suggestionNode, productNode)
    }
  }
}