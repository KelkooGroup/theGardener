import play.api.Logger

import scala.util.{Failure, Try}

package object utils {
  implicit class TryOps[T](t: Try[T]) {
    def logError(msg: => String): Try[T] = t.recoverWith {
      case e => Logger.error(msg, e)
        Failure(e)
    }
  }
}
