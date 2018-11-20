package utils

import java.util.concurrent.ConcurrentHashMap

import akka.Done
import com.google.inject.AbstractModule
import javax.inject._
import play.api.cache._

import scala.collection.JavaConverters._
import scala.collection.concurrent
import scala.concurrent.duration.Duration
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

class CacheModule extends AbstractModule {
  def configure() = {
    bind(classOf[AsyncCacheApi]).to(classOf[InMemoryCache])
    bind(classOf[SyncCacheApi]).to(classOf[DefaultSyncCacheApi])
  }
}

@Singleton
class InMemoryCache @Inject()(implicit ec: ExecutionContext) extends AsyncCacheApi {

  val cache: concurrent.Map[String, Any] = new ConcurrentHashMap[String, Any]().asScala

  def set(key: String, value: Any, expiration: Duration = Duration.Inf): Future[Done] = Future {
    cache.put(key, value)

    Done
  }

  def remove(key: String): Future[Done] = Future {
    cache -= key

    Done
  }

  def get[T: ClassTag](key: String): Future[Option[T]] = Future {
    cache.get(key).asInstanceOf[Option[T]]
  }

  def getOrElseUpdate[A: ClassTag](key: String, expiration: Duration = Duration.Inf)(orElse: => Future[A]): Future[A] = {
    get[A](key).flatMap {
      case Some(value) => Future.successful(value)
      case None => orElse.flatMap(value => set(key, value, expiration).map(_ => value))
    }
  }

  def removeAll(): Future[Done] = Future {
    cache.clear()

    Done
  }

}
