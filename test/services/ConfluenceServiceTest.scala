package services

import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec
import play.api.test.WsTestClient
import play.api.{Configuration, Environment}
import services.clients.ConfluenceClient

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.ActorSystem
import org.scalatestplus.play.PlaySpec

import scala.io.Source
import play.api.{Configuration, Environment}
import play.api.test.WsTestClient

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class ConfluenceServiceTest extends PlaySpec {

  implicit val actorSystem: ActorSystem = ActorSystem("ConfluenceServiceTest")


  "ConfluenceServiceTest" should {

    "prepare confluence page content  - Integration test" in {
      WsTestClient.withClient { client =>
        val configuration = Configuration.load(Environment.simple())
        val confluenceClient = new ConfluenceClient(configuration,client)
        val confluenceService = new ConfluenceService(configuration,confluenceClient)

        val status = Await.result(confluenceClient.updatePageContent(130912107, "TEST REST API from scala", ConfluenceMarkdownContent(ConfluenceClientTest.markDownFromFile)), 10.seconds)

        if (status.isLeft) {
          System.out.print(status.left)
        }

        assert(status.isRight)
      }
    }
  }
}


