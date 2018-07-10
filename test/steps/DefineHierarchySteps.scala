package steps

import java.util

import cucumber.api.scala.{EN, ScalaDsl}
import models._
import org.scalatest.mockito.MockitoSugar

import scala.collection.JavaConverters._

class DefineHierarchySteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^no hierarchy nodes is setup in theGardener$""") { () =>
    hierarchyRepository.deleteAll()
  }

  Given("""^the hierarchy nodes are$""") { hierarchies: util.List[HierarchyNode] =>
    hierarchyRepository.saveAll(hierarchies.asScala)
  }

  Then("""^the hierarchy nodes are now$""") { (hierarchy: util.List[HierarchyNode]) =>
    val exceptedHierarchies = hierarchy.asScala
    val actualHierarchies = hierarchyRepository.findAll()
    actualHierarchies must contain theSameElementsAs (exceptedHierarchies)
  }
}
