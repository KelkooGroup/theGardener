import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.Helpers._
import play.api.test._

class ApplicationSpec extends PlaySpec with GuiceOneAppPerSuite {

  "Application" should {

    "send 404 on a bad request" in {
      val badResult = route(app, FakeRequest(GET, "/boum")).get
      status(badResult) mustBe NOT_FOUND
    }

    "render the index page" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Hello, the Gardener")
    }
  }
}
