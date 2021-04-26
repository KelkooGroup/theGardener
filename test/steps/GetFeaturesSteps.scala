package steps

import java.io.File.separator
import java.io._
import java.nio.file._
import java.util

import anorm._
import io.cucumber.scala.{EN, ScalaDsl}
import models.Feature._
import models._
import org.eclipse.jgit.api._
import org.scalatestplus.mockito._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import resource._
import utils._

import scala.jdk.CollectionConverters._
import scala.concurrent._
import scala.concurrent.duration._
import scala.io.Source

class GetFeaturesSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._



  val featureContent = "Feature: As a user, I want some book suggestions so that I can do some discovery"


  Given("""^a project in theGardener hosted on a remote server$""") { () =>
    managed(initRemoteRepositoryIfNeeded("master", "target/data/GetFeatures/library/suggestionsWS/".fixPathSeparator)).acquireAndGet { git =>

      addFile(git, "target/data/GetFeatures/library/suggestionsWS/".fixPathSeparator, "test/features/suggestions/provide_book_suggestions.feature".fixPathSeparator, featureContent)
    }

    val project = Project("suggestionsWS", "Suggestions WebServices", Paths.get("target/data/GetFeatures/library/suggestionsWS/".fixPathSeparator).toUri.toString, None, "master", None, Some("test" + separator + "features"))
    projectRepository.save(project)
    CommonSteps.projects = Map(project.id -> project)
  }

  Given("""^the branch "([^"]*)" of the project "([^"]*)" is already checkout$""") { (branch: String, projectId: String) =>
    val localRepository = s"$projectsRootDirectory/$projectId/$branch".fixPathSeparator

    Await.result(gitService.clone(projects(projectId).repositoryUrl, localRepository)
      .flatMap(_ => gitService.checkout(branch, localRepository)), 30.seconds)
  }

  Given("""^the file "([^"]*)" of the server "([^"]*)" in the project "([^"]*)" on the branch "([^"]*)" is updated with content$""") { (file: String, remoteRepository: String, project: String, branch: String, content: String) =>
    val projectRepositoryPath = s"$remoteRepository/$project".fixPathSeparator

    managed(Git.open(new File(projectRepositoryPath))).acquireAndGet { git =>

      git.checkout.setName(branch).call()

      Files.write(Paths.get(s"$projectRepositoryPath/$file".fixPathSeparator), content.getBytes("UTF-8"))

      git.add().addFilepattern(".").call()

      git.commit().setMessage(s"Update file $file").call()
    }
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

  Given("""^the project "([^"]*)" is synchronized$""") { project: String =>
//    response = route(app, FakeRequest("POST", s"/api/projects/$project/synchronize")).get
//    await(response)
    projectRepository.findById(project).map { project =>
      await(projectService.synchronize(project))
    }
  }

  Given("""^we have no tag in the database$""") { () =>
    tagRepository.deleteAll()
  }

  Given("""^we have those features in the database$""") { features: util.List[Feature] =>
    featureRepository.saveAll(features.asScala.toSeq.map(_.copy(background = None, tags = Seq(), language = None, scenarios = Seq(), comments = Seq())))
  }

  Given("""^we have those scenario for the feature "([^"]*)" in the database$""") { (featureId: Long, scenarios: util.List[Scenario]) =>
    scenarioRepository.saveAll(featureId, scenarios.asScala.toSeq.map(_.copy(tags = Seq(), steps = Seq())))
  }

  Given("""^we have those stepsAsJSon for the scenario "([^"]*)" in the database$""") { (scenarioId: Long, stepsAsJson: String) =>
    db.withConnection { implicit connection =>
      SQL"UPDATE scenario SET stepsAsJson = $stepsAsJson WHERE id = $scenarioId".executeUpdate()
    }
  }

  Given("""^we have those tags for the scenario "([^"]*)" in the database$""") { (scenarioId: Long, tags: util.List[String]) =>
    tagRepository.saveAllByScenarioId(scenarioId, tags.asScala.toSeq)
  }

  When("""^the synchronization action is triggered for all projects$""") { () =>
    val future = projectService.synchronizeAll()
    Await.result(future, 30.seconds)
  }

  When("""^the synchronization action is triggered by the webhook for project "([^"]*)"$""") { project: String =>
    response = route(app, FakeRequest("POST", s"/api/projects/$project/synchronize")).get
    Await.result(response, 30.seconds)
  }

  Then("""^the project BDD features of this project are retrieved from the remote server$""") { () =>
    managed(Source.fromFile("target/data/git/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature".fixPathSeparator)).acquireAndGet(_.mkString mustBe featureContent)
  }

  Then("""^we have now those branches in the database$""") { branches: util.List[Branch] =>
    val expectedBranches = branches.asScala.toSeq.map(b => Branch(b.id, b.name, b.isStable, b.projectId, List()))
    val actualBranches = branchRepository.findAll()
    actualBranches must contain theSameElementsAs expectedBranches
  }

  Then("""^we have now those features in the database$""") { features: util.List[Feature] =>
    val expectedFeatures = features.asScala.toSeq.map(f => f.copy(path = f.path.fixPathSeparator, background = None, tags = Seq(), language = None, scenarios = Seq(), comments = Seq(), keyword = "Feature"))
    val actualFeatures = featureRepository.findAll().map(_.copy(background = None, tags = Seq(), language = None, scenarios = Seq(), comments = Seq()))
    actualFeatures must contain theSameElementsAs expectedFeatures
  }

  Then("""^we have now those scenario in the database$""") { scenario: util.List[Scenario] =>
    val expectedScenarios = scenario.asScala.toSeq.map(_.copy(tags = Seq(), steps = Seq()))
    val actualScenarios = scenarioRepository.findAll().map(_.asInstanceOf[Scenario].copy(tags = Seq(), steps = Seq()))
    actualScenarios must contain theSameElementsAs expectedScenarios
  }

  Then("""^we have now those scenario outline in the database$""") { scenario: util.List[ScenarioOutline] =>
    val expectedScenarios = scenario.asScala.toSeq.map(_.copy(tags = Seq(), steps = Seq(), examples = Seq()))
    val actualScenarios = scenarioRepository.findAll().map(_.asInstanceOf[ScenarioOutline].copy(tags = Seq(), steps = Seq(), examples = Seq()))
    actualScenarios must contain theSameElementsAs expectedScenarios
  }

  Then("""^we have now those stepsAsJSon for the scenario "([^"]*)" in the database$""") { (scenarioId: Long, expectedStep: String) =>
    val actualStep = scenarioRepository.findById(scenarioId).map(_.steps)
    Json.toJson(actualStep) mustBe Json.parse(expectedStep)
  }

  Then("""^we have now those examplesAsJSon for the scenario "([^"]*)" in the database$""") { (scenarioId: Long, expectedExamples: String) =>
    val actualExamples = scenarioRepository.findById(scenarioId).map(_.asInstanceOf[ScenarioOutline].examples)
    actualExamples mustBe Some(Json.parse(expectedExamples).as[Seq[Examples]])
  }

  Then("""^we have now those tags for the scenario "([^"]*)" in the database$""") { (scenarioId: Long, tags: util.List[String]) =>
    val expectedTags = tags.asScala.toSeq
    val actualTags = tagRepository.findAllByScenarioId(scenarioId)
    actualTags must contain theSameElementsAs expectedTags
  }
}
