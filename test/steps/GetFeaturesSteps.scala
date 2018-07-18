package steps

import java.io._
import java.net._
import java.nio.file._

import cucumber.api.scala._
import models._
import org.eclipse.jgit.api._
import org.scalatest.mockito._
import services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.io._


class GetFeaturesSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  val projectService = Injector.inject[ProjectService]
  val gitService = Injector.inject[GitService]

  val featureContent = "Feature: As a user, I want some book suggestions so that I can do some discovery"


  Given("""^a project in theGardener hosted on a remote server$""") { () =>
    val git = initRemoteRepository("master", "target/data/GetFeatures/library/suggestionsWS/")
    addFile(git, "target/data/GetFeatures/library/suggestionsWS/", "test/features/suggestions/provide_book_suggestions.feature", featureContent)

    val project = Project("suggestionsWS", "Suggestions WebServices", new URL(new URL("file:"), new File("target/data/GetFeatures/library/suggestionsWS/").getAbsolutePath).toURI.toString, "master", "test/features")
    projectRepository.save(project)
    CommonSteps.projects = Map(project.id -> project)
  }

  Given("""^the branch "([^"]*)" of the project "([^"]*)" is already checkout$""") { (branch: String, projectId: String) =>
    val localRepository = s"$projectsRootDirectory/$projectId/$branch"

    Await.result(gitService.clone(projects(projectId).repositoryUrl, localRepository)
      .flatMap(_ => gitService.checkout(branch, localRepository)), 30.seconds)
  }

  Given("""^the file "([^"]*)" of the server "([^"]*)" in the project "([^"]*)" on the branch "([^"]*)" is updated with content$""") { (file: String, remoteRepository: String, project: String, branch: String, content: String) =>
    val projectRepositoryPath = s"$remoteRepository/$project"

    val git = Git.open(new File(projectRepositoryPath))
    git.checkout.setName(branch).call()

    Files.write(Paths.get(s"$projectRepositoryPath/$file"), content.getBytes("UTF-8"))

    git.add().addFilepattern(".").call()

    git.commit().setMessage(s"Update file $file").call()
  }

  When("""^BDD features synchronization action is triggered$""") { () =>
    Await.result(projectService.synchronizeAll(), 30.seconds)
  }

  Then("""^the project BDD features of this project are retrieved from the remote server$""") { () =>
    Source.fromFile("target/data/git/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature").mkString mustBe featureContent
  }
}

