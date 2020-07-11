import io.cucumber.datatable.DataTable

import scala.collection.JavaConverters._

package object steps {

  // TODO remove
  implicit class DataTableAsScala(dataTable: DataTable) {
    def asScala: Seq[Map[String, String]] = dataTable.asMaps[String, String](classOf[String], classOf[String]).asScala.map(_.asScala.toMap)
  }

}
