import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Helpers._
import play.api.test.Injecting

class TestWithinServer extends PlaySpec with GuiceOneServerPerSuite with Injecting {

  "Application" should {

    "work within a server" in {

      val wsClient = inject[WSClient]
      val testIndex = s"http://localhost:$port/"

      val response = await(wsClient.url(testIndex).get())

      response.status mustBe OK
      response.body must include("Hello, the Gardener")
    }
  }
}
