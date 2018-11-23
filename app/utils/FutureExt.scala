package utils

import scala.concurrent._
import scala.util.control.NonFatal

object FutureExt {

  /**
    * For each item in items, sequentially generate a future using futureGenerator to create a future for an item.
    *
    * @param items initial items
    * @param f     function to generate a future for one item
    * @param ec    execution context
    * @tparam T item type
    * @tparam U future type
    * @return a future containing the sequence of results
    */
  def sequentially[T, U](items: Seq[T])(f: T => Future[U])(implicit ec: ExecutionContext): Future[Seq[U]] = {
    items.foldLeft(Future.successful(Seq[U]())) { (acc, item) =>
      acc.flatMap { seq =>
        f(item).map(seq :+ _).recover {
          case NonFatal(_) => seq
        }
      }
    }
  }
}
