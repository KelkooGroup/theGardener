package services

import java.io._

import akka.actor.ActorSystem
import models._
import org.apache.commons.io.FileUtils._
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito._
import org.scalatest._
import org.scalatest.concurrent._
import org.scalatestplus.mockito._
import play.api.{Configuration, Environment, Mode}
import repositories._
import utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.Success

class ProjectServiceTest extends WordSpec with MustMatchers with BeforeAndAfter with MockitoSugar with ScalaFutures {
  override implicit val patienceConfig = PatienceConfig(timeout = scaled(30.seconds))

  val encoding = "UTF-8"

  val featureBranch = "feature/add-suggestions"
  val bugfixBranch = "bugfix/fix-suggestions-engine"


  val gitService = mock[GitService]
  val projectRepository = mock[ProjectRepository]
  val featureRepository = mock[FeatureRepository]
  val branchRepository = mock[BranchRepository]
  val directoryRepository = mock[DirectoryRepository]
  val pageRepository = mock[PageRepository]
  val featureService = mock[FeatureService]
  val menuService = mock[MenuService]
  val pageService = mock[PageService]
  val environment = mock[Environment]


  val projectService = new ProjectService(projectRepository, gitService, featureService, featureRepository, branchRepository, directoryRepository, pageRepository, menuService, pageService, Configuration.load(Environment.simple()), environment, ActorSystem())

  val project = Project("suggestionsWS", "Suggestions WebServices", "git@github.com:library/suggestionsWS.git", "master", Some("^(^master$)|(^feature\\/.*$)"), Some("test/features"))
  val masterDirectory = projectService.getLocalRepository(project.id, project.stableBranch)
  val featureBranchDirectory = projectService.getLocalRepository(project.id, featureBranch)
  val bugfixBranchDirectory = projectService.getLocalRepository(project.id, bugfixBranch)
  val masterBranch = Branch(1, project.stableBranch, isStable = true, project.id)
  val projectsRootDirectory = projectService.projectsRootDirectory

  before {
    Mockito.reset(gitService, projectRepository)
  }

  "ProjectService" should {
    "checkout the remote branches of a project" in {
      when(gitService.getRemoteBranches(project.repositoryUrl)).thenReturn(Future.successful(Seq(project.stableBranch, featureBranch, bugfixBranch)))

      when(gitService.clone(anyString(), anyString())).thenReturn(Future.failed(new Exception()))
      when(gitService.checkout(anyString(), anyString())).thenReturn(Future.failed(new Exception()))

      when(gitService.clone(project.repositoryUrl, masterDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(project.stableBranch, masterDirectory)).thenReturn(Future.successful(()))

      when(gitService.clone(project.repositoryUrl, featureBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(featureBranch, featureBranchDirectory)).thenReturn(Future.successful(()))

      when(gitService.clone(project.repositoryUrl, bugfixBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(bugfixBranch, bugfixBranchDirectory)).thenReturn(Future.successful(()))

      when(featureService.parseBranchDirectory(any[Project], any[Branch], any[String])).thenReturn(Seq())
      when(featureRepository.saveAll(any[Seq[Feature]])).thenReturn(Seq())
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      when(branchRepository.findByProjectIdAndName(any[String], any[String])).thenReturn(None)
      when(branchRepository.save(any[Branch])).thenReturn(Branch(1, "master", isStable = true, project.id))

      val result = projectService.checkoutRemoteBranches(project).logError("Failed to checkout remote branches")

      whenReady(result) { _ =>
        Thread.sleep(100) // Workaround to fix flaky test issue on CI

        verify(gitService, times(1)).getRemoteBranches(project.repositoryUrl)

        verify(gitService, times(1)).clone(project.repositoryUrl, masterDirectory)
        verify(gitService, times(1)).clone(project.repositoryUrl, featureBranchDirectory)
        verify(gitService, times(1)).clone(project.repositoryUrl, bugfixBranchDirectory)
      }
    }

    "synchronize all existing projects" in {
      if (new File(projectsRootDirectory).exists()) {
        forceDelete(new File(projectsRootDirectory))
        forceMkdir(new File(projectsRootDirectory))
      }

      forceMkdir(new File(masterDirectory))
      forceMkdir(new File(bugfixBranchDirectory))


      Mockito.reset(gitService, projectRepository)

      when(projectRepository.findAll()).thenReturn(Seq(project))

      when(gitService.getRemoteBranches(project.repositoryUrl)).thenReturn(Future.successful(Seq(project.stableBranch, featureBranch)))

      when(gitService.pull(anyString())).thenReturn(Future.failed(new Exception()))
      when(gitService.pull(masterDirectory)).thenReturn(Future.successful((Seq(), Seq(), Seq())))

      when(gitService.clone(anyString(), anyString())).thenReturn(Future.failed(new Exception()))
      when(gitService.checkout(anyString(), anyString())).thenReturn(Future.failed(new Exception()))

      when(gitService.clone(project.repositoryUrl, featureBranchDirectory)).thenReturn(Future.successful(()))
      when(gitService.checkout(featureBranch, featureBranchDirectory)).thenReturn(Future.successful(()))

      when(featureService.parseBranchDirectory(any[Project], any[Branch], any[String])).thenReturn(Seq())
      when(featureRepository.saveAll(any[Seq[Feature]])).thenReturn(Seq())
      when(featureRepository.findByBranchIdAndPath(any[Long], any[String])).thenReturn(None)

      when(branchRepository.save(any[Branch])).thenReturn(masterBranch)
      when(branchRepository.findByProjectIdAndName(any[String], any[String])).thenReturn(Some(masterBranch))
      when(branchRepository.findAllByProjectId(any[String])).thenReturn(Seq(masterBranch, Branch(2, bugfixBranch, isStable = false, project.id)))

      when(menuService.refreshCache()).thenReturn(Success(Menu("", Seq())))

      when(environment.mode).thenReturn(Mode.Test)

      val result = projectService.synchronizeAll().logError("Failed to synchronize projects")

      whenReady(result) { _ =>
        Thread.sleep(100) // Workaround to fix flaky test issue on CI

        verify(projectRepository, times(1)).findAll()

        verify(gitService, times(1)).getRemoteBranches(project.repositoryUrl)

        verify(gitService, times(1)).pull(masterDirectory)

        verify(gitService, times(1)).clone(project.repositoryUrl, featureBranchDirectory)
      }
    }
  }
}
