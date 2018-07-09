package services

import com.typesafe.config.ConfigFactory
import models.Project
import org.mockito.Matchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.concurrent._
import org.scalatest.mockito._
import org.scalatest.{BeforeAndAfter, MustMatchers, WordSpec}
import repository.ProjectRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

class ProjectServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with MockitoSugar with ScalaFutures {
  val encoding = "UTF-8"

  val featureBranch = "feature/add-suggestions"
  val bugfixBranch = "bugfix/fix-suggestions-engine"

  val gitService = mock[GitService]
  val projectRepository = mock[ProjectRepository]

  val projectService = new ProjectService(projectRepository, gitService, ConfigFactory.load())

  val project = Project("suggestionsWS", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git", "master", "test/features")
  val masterDirectory = projectService.getLocalRepository(project.id, project.stableBranch)
  val featureBranchDirectory = projectService.getLocalRepository(project.id, featureBranch)
  val bugfixBranchDirectory = projectService.getLocalRepository(project.id, bugfixBranch)


  before {
    Mockito.reset(gitService, projectRepository)
  }


  "ProjectService" should {
    "create a project" in {
      when(gitService.getRemoteBranches(project.repositoryUrl)).thenReturn(Future.successful(Seq(project.stableBranch, featureBranch, bugfixBranch)))

      when(gitService.clone(anyString(), anyString())).thenReturn(Future.failed(new Exception()))
      when(gitService.checkout(anyString(), anyString(), anyBoolean())).thenReturn(Future.failed(new Exception()))

      when(gitService.clone(project.repositoryUrl, masterDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(project.stableBranch, masterDirectory)).thenReturn(Future.successful(()))

      when(gitService.clone(project.repositoryUrl, featureBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(featureBranch, featureBranchDirectory)).thenReturn(Future.successful(()))

      when(gitService.clone(project.repositoryUrl, bugfixBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(bugfixBranch, bugfixBranchDirectory)).thenReturn(Future.successful(()))

      when(projectRepository.save(project)).thenReturn(project)

      val result = projectService.create(project)

      whenReady(result, timeout(30.seconds)) { _ =>
        verify(gitService, times(1)).getRemoteBranches(project.repositoryUrl)

        verify(gitService, times(1)).clone(project.repositoryUrl, masterDirectory)
        verify(gitService, times(1)).clone(project.repositoryUrl, featureBranchDirectory)
        verify(gitService, times(1)).clone(project.repositoryUrl, bugfixBranchDirectory)

        verify(projectRepository, times(1)).save(project)
      }
    }

    "synchronize all exiting projects" ignore {

    }
  }

}
