package filters

import akka.stream.Materializer
import com.kelkoo.play.filters.AccessLogFilter
import javax.inject.Inject
import play.api.http.HttpFilters
import play.api.mvc.{EssentialFilter, RequestHeader, Result}
import play.api.{Environment, Mode}
import play.filters.cors.CORSFilter
import play.filters.gzip.GzipFilter

class Filters @Inject()(environment: Environment, accessLogFilter: AccessLogFilter, corsFilter: CORSFilter)(implicit mat: Materializer) extends HttpFilters {

  /**
    * Returns true for following cases:
    * - Response is a Javascript file (Angular app)
    * - Request is made on '/api/'
    *
    * @return
    */
  private def shouldGzip = (requestHeader: RequestHeader, response: Result) => {
    val responseIsJavascript = response.body.contentType.exists(_.startsWith("application/javascript"))
    val requestPathShouldBeGzipped = requestHeader.path.contains("/api/")
    responseIsJavascript || requestPathShouldBeGzipped
  }

  private val gzipFilter = new GzipFilter(shouldGzip = shouldGzip)

  override val filters: Seq[EssentialFilter] = environment.mode match {
    case Mode.Dev =>
      // CORSFilter only for DEV mode: allow Angular app to call API on different port
      Seq(accessLogFilter, gzipFilter, corsFilter)
    case _ =>
      Seq(accessLogFilter, gzipFilter)
  }

}
