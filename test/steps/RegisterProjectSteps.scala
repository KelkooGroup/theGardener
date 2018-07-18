package steps


import java.io._
import java.nio.file._
import java.util

import cucumber.api._
import cucumber.api.scala._
import models._
import org.apache.commons.io._
import org.eclipse.jgit.api._
import org.scalatest.mockito._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import _root_.scala.collection.JavaConverters._


class RegisterProjectSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  def registerProject(project: Project) = {
    response = route(app, FakeRequest("POST", "/api/projects").withJsonBody(Json.toJson(project))).get
    await(response)
  }

  private def checkProjectsInDb(expectedProjects: Seq[Project]) = {
    val actualProjects = projectRepository.findAll()
    actualProjects mustBe expectedProjects
  }

  Given("""^the server "([^"]*)" host under the project "([^"]*)" on the branch "([^"]*)" the file "([^"]*)"$""") { (remoteRepository: String, project: String, branch: String, file: String, content: String) =>
    val projectRepositoryPath = s"$remoteRepository/$project"

    val git = initRemoteRepository(branch, projectRepositoryPath)

    addFile(git, projectRepositoryPath, file, content)
  }

  Given("""^the server "([^"]*)" host under the project "([^"]*)" on the branch "([^"]*)" the files$""") { (remoteRepository: String, project: String, branch: String, files: DataTable) =>
    val projectRepositoryPath = s"$remoteRepository/$project"

    val git = initRemoteRepository(branch, projectRepositoryPath)

    files.asScala.map { line =>
      val file = line("file")
      val content = line("content")

      addFile(git, projectRepositoryPath, file, content)
    }
  }

  Given("""^no project settings are setup in theGardener$""") { () =>
    projectRepository.deleteAll()
  }

  Given("""^the root data path is "([^"]*)"$""") { path: String =>
    val fullPath = Paths.get("target/" + path)
    Files.createDirectories(fullPath.getParent)
  }

  When("""^a user register a new project with$""") { projects: util.List[Project] =>
    registerProject(projects.get(0))
  }

  When("""^a user register a new project in theGardener$""") { () =>
    val project = Project("suggestionsWS", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git ", "master", "test/features")
    registerProject(project)
  }

  Then("""^those projects settings are setup in theGardener$""") { () =>
    val expectedProjects = Seq(Project("suggestionsWS", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git ", "master", "test/features"))
    checkProjectsInDb(expectedProjects)
  }

  Then("""^the projects settings are now$""") { projects: util.List[Project] =>
    checkProjectsInDb(projects.asScala)
  }
}
