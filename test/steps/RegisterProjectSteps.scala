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

  def registerProject(project: Project) = {
    response = route(app, FakeRequest("POST", "/api/projects").withJsonBody(Json.toJson(project))).get
    await(response)
  }

  def triggerProject(project : Project) = {
    response = route(app, FakeRequest("GET", "/api/projects").withJsonBody(Json.toJson(project))).get
    await(response)
  }


  private def checkProjectsInDb(expectedProjects: Seq[Project]) = {
    val actualProjects = projectRepository.findAll()
    actualProjects mustBe expectedProjects
  }

  Given("""^the server "([^"]*)" host under the project "([^"]*)" on the branch "([^"]*)" the file "([^"]*)"$""") { (server: String, name: String, branch: String, path: String, content: String) =>
    val serverProject = "gitlab.corp.kelkoo.net"
    serverProject must be(server)

    val projectName = "library/suggestionsWS"
    projectName must be(name)

    val projectBranch = branch
    projectBranch must be(branch)

    val fullPath = Paths.get("target" + path)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }

  Given("""^no project settings are setup in theGardener$""") { () =>
    projectRepository.deleteAll()
  }

  Given("""^the root data path is "([^"]*)"$""") { path: String =>
    val fullPath = Paths.get("target/" + path)
    Files.createDirectories(fullPath.getParent)
  }

  When("""^a user register a new project in theGardener$""") { () =>
    val project = Project("suggestionsWS ", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git ", "master", "test/features")
    registerProject(project)
  }

  When("""^BDD features synchronization action is triggered$""") { () =>
    val scenarios = Project("suggestionsWS ", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git ", "master", "test/features")
    triggerProject(scenarios)
  }

  Then("""^those projects settings are setup in theGardener$""") { () =>
    val expectedProjects = Seq(Project("suggestionsWS ", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git ", "master", "test/features"))
    checkProjectsInDb(expectedProjects)
  }

  When("""^a user register a new project with$""") { projects: util.List[Project] =>
    registerProject(projects.get(0))
  }

  Then("""^the projects settings are now$""") { projects: util.List[Project] =>
    checkProjectsInDb(projects.asScala)
  }
}
