import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

/**
  * The ScalaTest + Play library builds on ScalaTest’s Selenium DSL to make it easy to test your Play applications from web browsers.
  * *
  * To run all tests in your test class using a same browser instance, mix OneBrowserPerSuite into your test class. You’ll also need to mix in a BrowserFactory trait that will provide a Selenium web driver: one of ChromeFactory, FirefoxFactory, HtmlUnitFactory, InternetExplorerFactory, SafariFactory.
  * *
  * In addition to mixing in a BrowserFactory, you will need to mix in a ServerProvider trait that provides a TestServer: one of OneServerPerSuite, OneServerPerTest, or ConfiguredServer.
  * *
  * For example, the following test class mixes in OneServerPerSuite and HtmUnitFactory:
  */
class TestWithinBrowser extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  // Override app if you need an Application with other than default parameters.
  //implicit override lazy val app = new GuiceApplicationBuilder().disable[EhCacheModule].build()

  def sharedTests(browser: BrowserInfo) = {
    "Application" should {

      "work within a browser" in {
        go to s"http://localhost:$port/example"
        pageTitle mustBe "Sample page"
        click on find(name("b")).value
        eventually {
          pageTitle mustBe "scalatest"
        }
      }
    }
  }

}


