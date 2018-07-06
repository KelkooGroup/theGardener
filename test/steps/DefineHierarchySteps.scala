package steps

import java.util

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

  Then("""^the hierarchy nodes are now$""") { hierarchies: util.List[Hierarchy] =>
    checkHierarchiesInDb(hierarchies.asScala)}

  private def checkHierarchiesInDb(expectedHierarchies: Seq[Hierarchy]) = {
    val actualProjects = hierarchyRepository.findAll()
    actualProjects mustBe expectedHierarchies
  }
}
