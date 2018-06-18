package steps


import java.nio.file.{Files, Paths}

import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.collection.JavaConverters._


class RegisterProjectSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._
  //level_0
  Given("""^a git server that host a project$""") { () =>

  }

  When("""^a user register a new project in theGardener$""") { () =>
  }

  Then("""^those projects settings are setup in theGardener$""") { () =>
  }

  Given("""^no project settings are setup in theGardener$""") { () =>
    projectRepository.deleteAll()
  }

  Given("""^the root data path is "([^"]*)"$""") { path: String =>
    val fullPath = Paths.get("target/" + path)
    Files.createDirectories(fullPath.getParent)
  }

  When("""^a user register a new project with$""") { data: DataTable =>
    val project = data.asList(classOf[Project]).asScala.head

    response = route(app, FakeRequest("POST", "/api/projects").withJsonBody(Json.toJson(project))).get
    await(response)
  }

  Then("""^the projects settings are now$""") { data: DataTable =>
    val expectedProjects = data.asList(classOf[Project]).asScala
    val actualProjects = projectRepository.findAll()
    actualProjects mustBe expectedProjects
  }
}
