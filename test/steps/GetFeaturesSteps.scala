package steps

import java.nio.file.{Files, Paths}
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.Result

import scala.concurrent.Future


class GetFeaturesSteps extends ScalaDsl with EN with MockitoSugar {

  var response: Future[Result] = _


  Given("""^the file system store the file "([^"]*)"$""") { (path: String, content: String) =>
    val fullPath = Paths.get("target" + path)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }


  Then("""^the file system store now the file "([^"]*)"$""") { (path: String, content: String) =>
    val fullPath = Paths.get("target" + path)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }



}
