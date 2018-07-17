package steps


import java.nio.file.{Files, Paths}
import java.util

import cucumber.api.scala.{EN, ScalaDsl}
import models._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.collection.JavaConverters._


class RegisterProjectSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  private def registerProject(project: Project) = {
    response = route(app, FakeRequest("POST", "/api/projects").withJsonBody(Json.toJson(project))).get
    await(response)
  }

  private def checkProjectsInDb(expectedProjects: Seq[Project]) = {
    val actualProjects = projectRepository.findAll()
    actualProjects mustBe expectedProjects
  }

  Given("""^no project settings are setup in theGardener$""") { () =>
    projectRepository.deleteAll()
  }

  Given("""^the root data path is "([^"]*)"$""") { path: String =>
    val fullPath = Paths.get("target/" + path)
    Files.createDirectories(fullPath.getParent)
  }

  When("""^a user register a new project in theGardener$""") { () =>
    val project = Project("suggestionsWS", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git ", "master", "test/features")

    registerProject(project)
  }

  Then("""^those projects settings are setup in theGardener$""") { () =>
    val expectedProjects = Seq(Project("sugggestionsWS", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git ", "master", "test/features"))
    checkProjectsInDb(expectedProjects)
  }

  When("""^a user register a new project with$""") { projects: util.List[Project] =>
    registerProject(projects.get(0).copy(hierarchy = None))
  }

  Then("""^the projects settings are now$""") { projects: util.List[Project] =>
    checkProjectsInDb(projects.asScala.map(_.copy(hierarchy = None)))
  }
}
