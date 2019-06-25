import java.io.File

import play.api.{Logger, Logging}

import scala.concurrent._
import scala.util.control.NonFatal
import scala.util.{Failure, Try}

package object utils extends Logging {

  implicit class TryOps[T](t: Try[T]) {
    def logError(msg: => String): Try[T] = t.recoverWith {
      case e => logger.error(msg, e)
        Failure(e)
    }
  }

  implicit class FutureOps[T](f: Future[T]) {
    def logError(msg: => String)(implicit ec: ExecutionContext): Future[T] = f.recoverWith {
      case NonFatal(e) => logger.error(msg, e)
        Future.failed(e)
    }
  }

  implicit class PathExt(path: String) {
    def fixPathSeparator: String = path.replace('/', File.separatorChar)
  }

}
