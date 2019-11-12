package steps

import anorm._
import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models._
import org.scalatestplus.mockito._

import scala.collection.JavaConverters._

class DefineHierarchySteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^no hierarchy nodes is setup in theGardener$""") { () =>
    hierarchyRepository.deleteAll()
  }

  Given("""^the hierarchy nodes are$""") {table: DataTable =>
    val hierarchies = buildHierarchyNodes(table)
    hierarchyRepository.saveAll(hierarchies)
  }

  private def buildHierarchyNodes(table: DataTable) = {
    table.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala).map { f =>

      val directoryPath = if (f.keys.exists(_ == "directoryPath")) {
        if (f("directoryPath") == "") None else Some(f("directoryPath"))
      } else None
      HierarchyNode(
        f("id"), f("slugName"), f("name"), f("childrenLabel"), f("childLabel"), directoryPath
      )
    }
  }

  Given("""^there is no links from projects to hierarchy nodes$""") { () =>
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE project_hierarchyNode".executeUpdate()

    }
  }

  Given("""^the links between hierarchy nodes are$""") { table: DataTable =>
    table.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala).foreach { projectHierarchy =>
      projectRepository.linkHierarchy(
        projectHierarchy("projectId"),
        projectHierarchy("hierarchyId")
      )
    }
  }

  Then("""^the links between hierarchy nodes are now$""") { table: DataTable =>
    table.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala).foreach { projectHierarchy =>
      hierarchyRepository.findAllByProjectId(projectHierarchy("projectId")).map(_.id) must contain(projectHierarchy("hierarchyId"))
    }
  }

  Then("""^the hierarchy nodes are now$""") { table: DataTable =>
    val exceptedHierarchies = buildHierarchyNodes(table)
    val actualHierarchies = hierarchyRepository.findAll()
    actualHierarchies must contain theSameElementsAs exceptedHierarchies
  }
}
