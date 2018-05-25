import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

class TestWithinBrowser extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

    "Application" should {

      "work within a browser" in {
        go to s"http://localhost:$port/"
        pageSource must include("Hello, the Gardener")
      }
    }
}
