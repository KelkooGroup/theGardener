package steps

import controllers.MyDataSource
import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import models.MyModel
import org.mockito.Mockito._
import org.scalatest.MustMatchers
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.inject._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.collection.JavaConverters._
import scala.concurrent.Future

class MyAppSteps extends PlaySpec with GuiceOneAppPerSuite with ScalaDsl with EN with MustMatchers with MockitoSugar {

  var mockDataSource: MyDataSource = mock[MyDataSource]
  var response: Future[Result] = _

  // Override app if you need an Application with other than default parameters.
  implicit override lazy val app = new GuiceApplicationBuilder().overrides(bind[MyDataSource].toInstance(mockDataSource)).build()


  Given("""^My data source returns the following data$""") { (dataSpec: DataTable) =>
    val data = dataSpec.asMaps(classOf[String], classOf[String]).asScala.map { row =>
      MyModel(row.get("field1"), row.get("field2").toInt)
    }
    when(mockDataSource.someData) thenReturn data
  }

  When("""^I perform a GET on following URL "([^"]*)"$""") { (url: String) =>
    response = route(app, FakeRequest(GET, url)).get
  }

  Then("""^I get a response with status "([^"]*)"$""") { (expectedStatus: String) =>
    status(response) mustBe expectedStatus.toInt
  }

  Then("""^I get the following json response body$""") { (expectedJson: DataTable) =>
    contentAsString(response) mustBe expectedJson.asMaps(classOf[String], classOf[String]).asScala.head.get("json")
  }

}