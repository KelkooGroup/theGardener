import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers._

/**
  * Sometimes you want to test with the real HTTP stack.
  * If all tests in your test class can reuse the same server instance, you can mix in OneServerPerSuite
  * (which will also provide a new Application for the suite)
  */
class TestWithinServer extends PlaySpec with GuiceOneServerPerSuite {

  // Override app if you need an Application with other than default parameters.
  //implicit override lazy val app = new GuiceApplicationBuilder().disable[EhCacheModule].build()

  "Application" should {

    "work within a server" in {

      val wsClient = app.injector.instanceOf[WSClient]
      val testHelloJsonUrl = s"http://localhost:$port/helloJson"
      // The test payment gateway requires a callback to this server before it returns a result...
      val response = await(wsClient.url(testHelloJsonUrl).get())

      response.status mustBe OK
      response.json mustBe Json.obj("message" -> "Hello, world")
    }
  }
}


