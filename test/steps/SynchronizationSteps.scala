package steps

import cucumber.api.scala._
import org.mockito.Mockito.{times, _}
import org.scalatestplus.mockito._


class SynchronizationSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._


  Given("""^the replica call count is reset$""") { () =>
    reset(spyReplicaService)
  }

  Then("""^the webhook "([^"]*)" is triggered$""") { url: String =>
    verify(spyReplicaService, times(1)).postOnUrl(url)
  }


}
