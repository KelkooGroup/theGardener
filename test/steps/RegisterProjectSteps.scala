package steps


import java.nio.file.{Files, Paths}

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.mockito.MockitoSugar
import cucumber.api.DataTable
import models.Project


import scala.collection.JavaConverters._


class RegisterProjectSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^no project settings are setup in theGardener$""") { () =>
    cleanDatabase()
  }

  Given("""^the root data path is "([^"]*)"$""") { (path: String) =>
    val fullPath = Paths.get("target/" + path)
    Files.createDirectories(fullPath.getParent)
  }


  Then("""^the projects settings are now$""") { (data: DataTable) =>
    val expectedProject = data.asList(classOf[Project]).asScala.toList
    val actualProject = projectRepository.getAll()
    actualProject mustBe expectedProject
  }


  Given("""^a git server that host a project$""") { () =>
    cleanDatabase()
    projectRepository.getAll()

  }

  When("""^a user register this project in theGardener$""") { () =>
  }


  Then("""^those project settings are stored in theGardener system$""") { () =>
  }


}
