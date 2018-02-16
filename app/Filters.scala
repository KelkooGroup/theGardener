import javax.inject.Inject

import play.api.http.HttpFilters
import com.kelkoo.play.filters.AccessLogFilter

class Filters @Inject()(accessLogFilter: AccessLogFilter) extends HttpFilters {
  def filters = Seq(accessLogFilter)
}