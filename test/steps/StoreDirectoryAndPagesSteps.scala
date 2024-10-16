package steps

import java.util

import io.cucumber.scala._
import models._
import org.scalatestplus.mockito._
import org.mockito.Mockito.{times, _}
import scala.jdk.CollectionConverters._

class StoreDirectoryAndPagesSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Then("""^we have now those directories in the database$""") { directories: util.List[Directory] =>
    val expectedDirectories = directories.asScala.toSeq.map(d => Directory(d.id, d.name, d.label, d.description, d.order, d.relativePath, d.path, d.branchId))
    val actualDirectories = directoryRepository.findAll()
    actualDirectories must contain theSameElementsAs expectedDirectories
  }

  Then("""^we have now those pages in the database$""") { pages: util.List[PageRow] =>
    val expectedPages = pages.asScala.toSeq.map(p => Page(p.id, p.name, p.label, p.description, p.order, Option(p.markdown), p.relativePath, p.path, p.directoryId, p.dependOnOpenApi))
    val actualPages = pageRepository.findAllWithContent()
    actualPages must contain theSameElementsAs expectedPages
  }

  Given("""^page computation count is reset$"""){ () =>
    reset(spyPageService)
  }

  Then("""^page "([^"]*)" hasn't been computed$"""){ path:String =>
    verifyComputationTimes(path, 0)
  }

  Then("""^page "([^"]*)" has been computed only one time$"""){ path:String =>
    verifyComputationTimes(path, 1)
  }

  Then("""^page "([^"]*)" has been computed (\d+) times$"""){ (path:String, times:Int) =>
    verifyComputationTimes(path, times)
  }

  def verifyComputationTimes(path: String, nb: Int): Unit = {
    verify(spyPageServiceCache, times(nb)).put(path)
  }


}
