package steps

import anorm._
import io.cucumber.datatable.DataTable
import io.cucumber.scala.Implicits._
import io.cucumber.scala.{EN, ScalaDsl}
import models._
import org.scalatestplus.mockito._

class DefineHierarchySteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  DataTableType { line: Map[String, Option[String]] =>
    val directoryPath = line.get("directoryPath").flatten
    val shortcut = line.get("shortcut").flatten
    HierarchyNode(
      line("id").get, line("slugName").get, line("name").get, line("childrenLabel").get, line("childLabel").get, directoryPath, shortcut
    )
  }

  Given("""^no hierarchy nodes is setup in theGardener$""") { () =>
    hierarchyRepository.deleteAll()
  }

  Given("""^the hierarchy nodes are$""") {table: DataTable =>
    val hierarchies = table.asScalaRawList[HierarchyNode]
    hierarchyRepository.saveAll(hierarchies)
  }

  Given("""^there is no links from projects to hierarchy nodes$""") { () =>
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE project_hierarchyNode".executeUpdate()

    }
  }

  Given("""^the links between hierarchy nodes are$""") { table: DataTable =>
    table.asScalaMaps.foreach { projectHierarchy =>
      projectRepository.linkHierarchy(
        projectHierarchy("projectId").get,
        projectHierarchy("hierarchyId").get
      )
    }
  }

  Then("""^the links between hierarchy nodes are now$""") { table: DataTable =>
    table.asScalaMaps.foreach { projectHierarchy =>
      hierarchyRepository.findAllByProjectId(projectHierarchy("projectId").get).map(_.id) must contain(projectHierarchy("hierarchyId").get)
    }
  }

  Then("""^the hierarchy nodes are now$""") { table: DataTable =>
    val exceptedHierarchies = table.asScalaRawList[HierarchyNode]
    val actualHierarchies = hierarchyRepository.findAll()
    actualHierarchies must contain theSameElementsAs exceptedHierarchies
  }
}
