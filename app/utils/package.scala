import play.api.Logger

import scala.concurrent._
import scala.util.control.NonFatal
import scala.util.{Failure, Try}

package object utils {

  implicit class TryOps[T](t: Try[T]) {
    def logError(msg: => String): Try[T] = t.recoverWith {
      case e => Logger.error(msg, e)
        Failure(e)
    }
  }

  implicit class FutureOps[T](f: Future[T]) {
    def logError(msg: => String)(implicit ec: ExecutionContext): Future[T] = f.recoverWith {
      case NonFatal(e) => Logger.error(msg, e)
        Future.failed(e)
    }
  }
}
