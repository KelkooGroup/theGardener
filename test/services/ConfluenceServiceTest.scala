package services

import akka.actor.ActorSystem
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.test.WsTestClient
import play.api.{Configuration, Environment}
import repositories.{BranchRepository, ProjectRepository}
import services.clients.ConfluenceClient

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class ConfluenceServiceTest extends PlaySpec   with MockitoSugar{

  implicit val actorSystem: ActorSystem = ActorSystem("ConfluenceServiceTest")

  val testPageId = 109379663

  "ConfluenceServiceTest" should {

    "update page content when needed - Integration test" ignore {

      val projectRepository = mock[ProjectRepository]
      val branchRepository = mock[BranchRepository]
      val directoryService = mock[DirectoryService]
      WsTestClient.withClient { client =>
        val configuration = Configuration.load(Environment.simple())
        val confluenceClient = new ConfluenceClient(configuration, client)
        val confluenceService = new ConfluenceService( confluenceClient,projectRepository,branchRepository,directoryService)

        val pageId = 109379665L

        Await.result(confluenceClient.getConfluencePageVersion(pageId), 10.seconds) match {
          case Left(error) => fail(s"Fail while getting the version ${error.toString} ")
          case Right(initialVersion) => {

            Await.result(confluenceService.updateConfluencePage(pageId, "Update page content when needed","", "Content"), 10.seconds) match {
              case Left(error) => fail(s"Fail while updated  ${error.toString} ")
              case Right(_) => Await.result(confluenceClient.getConfluencePageVersion(pageId), 10.seconds) match {
                case Left(error) => fail(s"Fail while getting the updated version ${error.toString} ")
                case Right(updatedVersion) =>
                  initialVersion mustEqual (updatedVersion)
              }
            }
          }
        }
      }
    }
  }
}


