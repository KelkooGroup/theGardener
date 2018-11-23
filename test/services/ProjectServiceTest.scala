package services

import java.io._

import akka.actor.ActorSystem
import com.typesafe.config._
import models._
import org.apache.commons.io.FileUtils._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito._
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.mockito._
import repository._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

class ProjectServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with MockitoSugar with ScalaFutures {
  override implicit val patienceConfig = PatienceConfig(timeout = scaled(30.seconds))

  val encoding = "UTF-8"

  val featureBranch = "add-suggestions"
  val bugfixBranch = "fix-suggestions-engine"

  val gitService = mock[GitService]
  val projectRepository = mock[ProjectRepository]
  val featureRepository = mock[FeatureRepository]
  val branchRepository = mock[BranchRepository]
  val featureService = mock[FeatureService]
  val criteriaService = mock[CriteriaService]

  val projectService = new ProjectService(projectRepository, gitService, ConfigFactory.load(), ActorSystem(), featureService, featureRepository, branchRepository, criteriaService)

  val project = Project("suggestionsWS", "Suggestions WebServices", "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git", "master", "test/features")
  val masterDirectory = projectService.getLocalRepository(project.id, project.stableBranch)
  val featureBranchDirectory = projectService.getLocalRepository(project.id, featureBranch)
  val bugfixBranchDirectory = projectService.getLocalRepository(project.id, bugfixBranch)
  val masterBranch = Branch(1, project.stableBranch, isStable = true, project.id)


  before {
    Mockito.reset(gitService, projectRepository)
  }

  "ProjectService" should {
    "checkout the remote branches of a project" in {
      when(gitService.getRemoteBranches(project.repositoryUrl)).thenReturn(Future.successful(Seq(project.stableBranch, featureBranch, bugfixBranch)))

      when(gitService.clone(anyString(), anyString())).thenReturn(Future.failed(new Exception()))
      when(gitService.checkout(anyString(), anyString(), anyBoolean())).thenReturn(Future.failed(new Exception()))

      when(gitService.clone(project.repositoryUrl, masterDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(project.stableBranch, masterDirectory)).thenReturn(Future.successful(()))

      when(gitService.clone(project.repositoryUrl, featureBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(featureBranch, featureBranchDirectory)).thenReturn(Future.successful(()))

      when(gitService.clone(project.repositoryUrl, bugfixBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(bugfixBranch, bugfixBranchDirectory)).thenReturn(Future.successful(()))

      when(featureService.parseBranchDirectory(any[Project], any[Branch], any[String])).thenReturn(Seq())
      when(featureRepository.saveAll(any[Seq[Feature]])).thenReturn(Seq())
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      when(branchRepository.save(any[Branch])).thenReturn(Branch(1, "master", isStable = true, project.id))

      val result = projectService.checkoutRemoteBranches(project)

      whenReady(result) { _ =>
        verify(gitService, times(1)).getRemoteBranches(project.repositoryUrl)

        verify(gitService, times(1)).clone(project.repositoryUrl, masterDirectory)
        verify(gitService, times(1)).clone(project.repositoryUrl, featureBranchDirectory)
        verify(gitService, times(1)).clone(project.repositoryUrl, bugfixBranchDirectory)
      }
    }

    "synchronize all exiting projects" in {
      forceMkdir(new File(masterDirectory))
      forceMkdir(new File(bugfixBranchDirectory))

      Mockito.reset(gitService, projectRepository)

      when(projectRepository.findAll()).thenReturn(Seq(project))

      when(gitService.getRemoteBranches(project.repositoryUrl)).thenReturn(Future.successful(Seq(project.stableBranch, featureBranch)))

      when(gitService.pull(anyString())).thenReturn(Future.failed(new Exception()))
      when(gitService.pull(masterDirectory)).thenReturn(Future.successful((Seq(), Seq(), Seq())))

      when(gitService.clone(anyString(), anyString())).thenReturn(Future.failed(new Exception()))
      when(gitService.checkout(anyString(), anyString(), anyBoolean())).thenReturn(Future.failed(new Exception()))

      when(gitService.clone(project.repositoryUrl, featureBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(featureBranch, featureBranchDirectory)).thenReturn(Future.successful(()))

      when(featureService.parseBranchDirectory(any[Project], any[Branch], any[String])).thenReturn(Seq())
      when(featureRepository.saveAll(any[Seq[Feature]])).thenReturn(Seq())
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      when(branchRepository.save(any[Branch])).thenReturn(masterBranch)
      when(branchRepository.findByProjectIdAndName(any[String], any[String])).thenReturn(Some(masterBranch))
      when(branchRepository.findAllByProjectId(any[String])).thenReturn(Seq(masterBranch, Branch(2, bugfixBranch, isStable = false, project.id)))


      val result = projectService.synchronizeAll()

      whenReady(result) { _ =>
        verify(projectRepository, times(1)).findAll()

        verify(gitService, times(1)).getRemoteBranches(project.repositoryUrl)

        verify(gitService, times(1)).pull(masterDirectory)

        verify(gitService, times(1)).clone(project.repositoryUrl, featureBranchDirectory)

        new File(bugfixBranchDirectory).exists() mustBe false
      }
    }
  }
}
