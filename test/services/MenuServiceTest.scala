package services

import models.{HierarchyNode, Menu}
import org.scalatest.Matchers._
import org.scalatest._
import org.scalatestplus.mockito._
import repositories.FeatureRepository
import services.MenuService._

class MenuServiceTest extends WordSpec with MustMatchers with MockitoSugar {

  val featureRepository = mock[FeatureRepository]

  val separator = "."

  val rootNode = HierarchyNode(".", "root", " Hierarchy root", "Views", "View")
  val engNode = HierarchyNode(".01.", "eng", "Engineering view", "System groups", "System group")
  val libraryNode = HierarchyNode(".01.01.", "library", "Library system group", "Systems", "System")
  val suggestionNode = HierarchyNode(".01.01.01.", "suggestion", "Suggestion system", "Projects", "Project")
  val productNode = HierarchyNode(".02.", "product", "Product view ", "System groups", "System group")

  val root = Menu(rootNode.id, Seq(rootNode))
  val eng = Menu(engNode.id, Seq(engNode))
  val library = Menu(libraryNode.id, Seq(libraryNode))
  val suggestion = Menu(suggestionNode.id, Seq(suggestionNode))
  val product = Menu(productNode.id, Seq(productNode))

  val menuSubtree = library.copy(hierarchy = Seq(rootNode, engNode, libraryNode), children = Seq(
    suggestion.copy(hierarchy = Seq(rootNode, engNode, libraryNode, suggestionNode))))

  val menuTree = root.copy(children = Seq(
    eng.copy(hierarchy = Seq(rootNode, engNode), children = Seq(menuSubtree)),
    product.copy(hierarchy = Seq(rootNode, productNode))))

  "MenuService" should {
    "find if a hierarchy node is the child of another" in {
      assert(isChild(separator, root.id, eng.id))
      assert(isChild(separator, root.id, product.id))
      assert(isChild(separator, eng.id, library.id))
      assert(isChild(separator, library.id, suggestion.id))
      assert(!isChild(separator, root.id, library.id))
      assert(!isChild(separator, library.id, root.id))
      assert(!isChild(separator, library.id, eng.id))
      assert(!isChild(separator, root.id, suggestion.id))
      assert(!isChild(separator, eng.id, suggestion.id))
      assert(!isChild(separator, eng.id, product.id))
    }

    "build a tree with the hierarchy nodes" in {
      val nodes = Seq(eng, library, suggestion, product)

      val actualTree = buildTree(root, nodes)

      val expectedTree = menuTree.children

      actualTree must contain theSameElementsInOrderAs expectedTree
    }

    "build a subtree with the hierarchy node slug name" in {

      findMenuSubtree(".01.01.")(menuTree) mustBe Some(menuSubtree)
    }

    "merge children hierarchy" in {
      mergeChildrenHierarchy(menuTree) must contain theSameElementsAs Seq(rootNode, engNode, libraryNode, suggestionNode, productNode)
    }
  }
}
