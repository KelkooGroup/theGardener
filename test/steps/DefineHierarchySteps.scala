package steps

import java.util

import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models._
import org.scalatest.mockito.MockitoSugar

import scala.collection.JavaConverters._

class DefineHierarchySteps extends ScalaDsl with EN with MockitoSugar {


  import CommonSteps._

  Given("""^no hierarchy is setup in theGardener$""") { () =>
  }

  Given("""^no hierarchy nodes is setup in theGardener$""") { () =>
    hierarchyRepository.deleteAll()
  }

  Given("""^the hierarchy nodes are$""") { hierarchies: util.List[Hierarchy] =>
    hierarchyRepository.saveAll(hierarchies.asScala)

    CommonSteps.hierarchies = hierarchies.asScala.map(p => (p.id, p)).toMap
  }

  Then("""^the hierarchy nodes are now$""") { (hierarchy: DataTable) =>
    checkHierarchies(hierarchy, hierarchyRepository.findAll())
  }

  def checkHierarchies(hierarchy: DataTable, actualHierarchies: Seq[Hierarchy]): Unit = {
    val exceptedHierarchies = hierarchy.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala).map { hierarchy =>
      Hierarchy(hierarchy("id").toString, hierarchy("slugName").toString, hierarchy("name").toString)
    }
    actualHierarchies.size mustBe exceptedHierarchies.size
  }

}
