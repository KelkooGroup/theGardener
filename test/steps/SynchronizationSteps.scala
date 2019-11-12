package steps

import cucumber.api.scala._
import org.mockito.Mockito.{times, _}
import org.scalatestplus.mockito._



class SynchronizationSteps  extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._


  Given("""^the replica call count is reset$""") { () =>
    reset(spyReplicaService)
  }

  Then("""^the webhook "([^"]*)" is triggered$""") { url: String =>
    verify(spyReplicaService, times(1)).postOnUrl(url)
  }


  Given("""^the menu reloaded count is reset$""") { () =>
    reset(spyMenuService)
  }

  Then("""^the menu has been reloaded$""") { () =>
    verify(spyMenuService, times(1)).refreshCache()
  }


  Given("""^the pages computation from the database count is reset$""") { () =>
    reset(spyMenuService)
  }

  Then("""^the pages has been recomputed from the database for the project "([^"]*)"$"""){ (projectId:String) =>
    verify(spyProjectService, times(1)).reloadFromDatabase(projectId)
  }


}
