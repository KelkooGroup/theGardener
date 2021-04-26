package steps

import io.cucumber.scala._
import org.mockito.Mockito.{times, _}
import org.scalatestplus.mockito._
import services.PageWithContent


class SynchronizationSteps extends ScalaDsl with EN with MockitoSugar {

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

  Then("""^the pages has been recomputed from the database for the project "([^"]*)"$""") { (projectId: String) =>
    verify(spyProjectService, times(1)).reloadFromDatabase(projectId)
  }

  Then("""^the cache store "([^"]*)" with the value$""") { (key: String, expectedValue: String) =>

    cache.get[PageWithContent](key) match {
      case None => fail(s"$key do not exists")
      case Some(actualValue) => expectedValue mustEqual actualValue.toString
    }

  }

}
