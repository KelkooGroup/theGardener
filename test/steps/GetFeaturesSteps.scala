package steps

import java.io._
import java.net._
import java.nio.file._
import java.util

import anorm._
import cucumber.api.scala.{EN, ScalaDsl}
import models.Feature._
import models._
import org.eclipse.jgit.api._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import services._

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.io.Source

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

  Given("""^we have no branch in the database$""") { () =>
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE branch".executeUpdate()
      SQL"ALTER TABLE branch ALTER COLUMN id RESTART WITH 1".executeUpdate()
    }
  }

  Given("""^we have no feature in the database$""") { () =>
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE feature".executeUpdate()
      SQL"ALTER TABLE feature ALTER COLUMN id RESTART WITH 1".executeUpdate()
    }
  }

  Given("""^we have no scenario in the database$""") { () =>
    db.withConnection { implicit connection =>
      SQL"TRUNCATE TABLE scenario".executeUpdate()
      SQL"ALTER TABLE scenario ALTER COLUMN id RESTART WITH 1".executeUpdate()
    }
  }

  Given("""^we have no tag in the database$""") { () =>
    tagRepository.deleteAll()
  }

  Given("""^we have those features in the database$""") { features: util.List[Feature] =>
    featureRepository.saveAll(features.asScala.map(_.copy(background = None, tags = Seq(), language = None, scenarios = Seq(), comments = Seq())))
  }

  Given("""^we have those scenario for the feature "([^"]*)" in the database$""") { (featureId: Long, scenarios: util.List[Scenario]) =>
    scenarioRepository.saveAll(featureId, scenarios.asScala.map(_.copy(tags = Seq(), steps = Seq())))
  }

  Given("""^we have those stepsAsJSon for the scenario "([^"]*)" in the database$""") { (scenarioId: Long, stepsAsJson: String) =>
    db.withConnection { implicit connection =>
      SQL"UPDATE scenario SET stepsAsJson = $stepsAsJson WHERE id = $scenarioId".executeUpdate()
    }
  }

  Given("""^we have those tags for the scenario "([^"]*)" in the database$""") { (scenarioId: Long, tags: util.List[String]) =>
    tagRepository.saveAllByScenarioId(scenarioId, tags.asScala)
  }

  When("""^BDD features synchronization action is triggered$""") { () =>
    val future = projectService.synchronizeAll()
    Await.result(future, 30.seconds)
  }

  When("""^the synchronization action is triggered by the scheduler$""") { () =>
    Thread.sleep(2000)
  }

  When("""^the synchronization action is triggered by the webhook for project "([^"]*)"$""") { project: String =>
    response = route(app, FakeRequest("POST", s"/api/projects/$project/synchronize")).get
    await(response)
  }

  Then("""^the project BDD features of this project are retrieved from the remote server$""") { () =>
    Source.fromFile("target/data/git/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature").mkString mustBe featureContent
  }

  Then("""^we have now those branches in the database$""") { branches: util.List[Branch] =>
    val expectedBranches = branches.asScala
    val actualBranches = branchRepository.findAll()
    actualBranches must contain theSameElementsAs expectedBranches
  }

  Then("""^we have now those features in the database$""") { features: util.List[Feature] =>
    val expectedFeatures = features.asScala.toList.map(_.copy(background = None, tags = Seq(), language = None, scenarios = Seq(), comments = Seq(), keyword = "Feature"))
    val actualFeatures = featureRepository.findAll().map(_.copy(background = None, tags = Seq(), language = None, scenarios = Seq(), comments = Seq()))
    actualFeatures must contain theSameElementsAs expectedFeatures
  }

  Then("""^we have now those scenario in the database$""") { scenario: util.List[Scenario] =>
    val expectedScenarios = scenario.asScala.toList.map(_.copy(tags = Seq(), steps = Seq()))
    val actualScenarios = scenarioRepository.findAll().map(_.asInstanceOf[Scenario].copy(tags = Seq(), steps = Seq()))
    actualScenarios must contain theSameElementsAs expectedScenarios
  }

  Then("""^we have now those stepsAsJSon for the scenario "([^"]*)" in the database$""") { (scenarioId: Int, expectedStep: String) =>
    val actualStep = scenarioRepository.findById(scenarioId).map(_.steps)
    Json.toJson(actualStep) mustBe Json.parse(expectedStep)
  }

  Then("""^we have now those tags for the scenario "([^"]*)" in the database$""") { (scenarioId: Int, tags: util.List[String]) =>
    val expectedTags = tags.asScala.toList
    val actualTags = tagRepository.findAllByScenarioId(scenarioId)
    actualTags must contain theSameElementsAs expectedTags
  }
}