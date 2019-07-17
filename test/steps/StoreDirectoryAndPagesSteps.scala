package steps

import java.util
import cucumber.api.scala._
import models._
import org.scalatestplus.mockito._
import play.api.test.Helpers._
import play.api.test._
import scala.collection.JavaConverters._


class StoreDirectoryAndPagesSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Then("""^we have now those directories in the database$""") { directories: util.List[Directory] =>
    val expectedDirectories = directories.asScala.map(d => new Directory(d.id, d.name, d.label, d.description,d.order,d.relativePath,d.path,d.branchId))
    val actualDirectories = directoryRepository.findAll()
    actualDirectories must contain theSameElementsAs expectedDirectories
  }

  Then("""^we have now those pages in the database$""") { pages: util.List[Page] =>
    val expectedPages = pages.asScala.map(d => new Page(d.id, d.name, d.label, d.description,d.order,d.markdown,d.relativePath,d.path,d.directoryId))
    val actualPages = pageRepository.findAll()
    actualPages must contain theSameElementsAs expectedPages
  }
}
