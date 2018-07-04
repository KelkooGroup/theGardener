package steps

import java.nio.file.{Files, Paths}

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.mockito.MockitoSugar
import services.SynchronizedService


class GetFeaturesSteps extends ScalaDsl with EN with MockitoSugar {

  val projectService = Injector.inject[SynchronizedService]


  Given("""^the file system store the file "([^"]*)"$""") { (path: String, content: String) =>
    val fullPath = Paths.get("target" + path)
    Files.createDirectories(fullPath.getParent)
    Files.write(fullPath, content.getBytes())
  }


  Then("""^the file system store now the file "([^"]*)"$""") { (path: String, content: String) =>
   ???
  }

  When("""^BDD features synchronization action is triggered$""") { () =>
    projectService.init("url")
  }


}

