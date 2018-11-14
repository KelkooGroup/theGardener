import javax.inject.Inject
import play.api.http.HttpFilters
import play.filters.cors.CORSFilter
import com.kelkoo.play.filters.AccessLogFilter

class Filters @Inject()(corsFilter: CORSFilter, accessLogFilter: AccessLogFilter) extends HttpFilters {
  def filters = Seq(corsFilter, accessLogFilter)
}
