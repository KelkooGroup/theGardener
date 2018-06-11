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
    val expectedProjects = data.asList(classOf[Project]).asScala
    val actualProjects = projectRepository.getAll()
    actualProjects mustBe expectedProjects
  }

  When("""^a user register a new project with$""") { (data: DataTable) =>
    cleanDatabase()

    val projects = data.asList(classOf[Project]).asScala.map(project => Project(project.id, project.name, project.repositoryUrl, project.stableBranch, project.featuresRootPath))

    projects.foreach(projectRepository.insertOne)

    CommonSteps.projects = projects.map(p => (p.id, p)).toMap

    projectService.projects ++= CommonSteps.projects
  }


  Given("""^a git server that host a project$""") { () =>
    cleanDatabase()
  }

  When("""^a user register a new project in theGardener$""") { (data: DataTable) =>
    cleanDatabase()

    val projects = data.asList(classOf[Project]).asScala.map(project => Project(project.id, project.name, project.repositoryUrl, project.stableBranch, project.featuresRootPath))

    projects.foreach(projectRepository.insertOne)

    CommonSteps.projects = projects.map(p => (p.id, p)).toMap

    projectService.projects ++= CommonSteps.projects
  }


}
