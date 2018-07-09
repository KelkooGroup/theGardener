package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.mockito.MockitoSugar
import services.ProjectService


class GetFeaturesSteps extends ScalaDsl with EN with MockitoSugar {

  val projectService = Injector.inject[ProjectService]

  When("""^BDD features synchronization action is triggered$""") { () =>

    projectService.synchronizeAll()
  }
}

