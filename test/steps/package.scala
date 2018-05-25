import cucumber.api.DataTable

import scala.collection.JavaConverters._

package object steps {

  implicit class DataTableAsScala(dataTable: DataTable) {
    def asScala: Seq[Map[String, String]] = dataTable.asMaps(classOf[String], classOf[String]).asScala.map(_.asScala.toMap)
  }

}
