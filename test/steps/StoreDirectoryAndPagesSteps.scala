package steps

import java.util

import cucumber.api.scala._
import models._
import org.scalatestplus.mockito._

import scala.collection.JavaConverters._


class StoreDirectoryAndPagesSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Then("""^we have now those directories in the database$""") { directories: util.List[Directory] =>
    val expectedDirectories = directories.asScala.map(d => Directory(d.id, d.name, d.label, d.description, d.order, d.relativePath, d.path, d.branchId))
    val actualDirectories = directoryRepository.findAll()
    actualDirectories must contain theSameElementsAs expectedDirectories
  }

  Then("""^we have now those pages in the database$""") { pages: util.List[PageRow] =>
    val expectedPages = pages.asScala.map(p => Page(p.id, p.name, p.label, p.description, p.order, Option(p.markdown), p.relativePath, p.path, p.directoryId))
    val actualPages = pageRepository.findAllWithContent()
    actualPages must contain theSameElementsAs expectedPages
  }
}
