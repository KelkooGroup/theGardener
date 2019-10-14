package services

import javax.inject.Inject
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logging}

import scala.concurrent._

class ReplicaService @Inject()(config: Configuration,wsClient: WSClient)extends Logging {

  val replicaUrl = config.getOptional[String]("replica.url")


  def triggerSychronizeOnReplica(projectId: String): Future[Unit] = {
    replicaUrl match {
      case Some(replicaUrl) =>
        val url = s"${replicaUrl}/api/projects/${projectId}/synchronize"
        logger.info(s"Trigger the synchronize on $projectId with url ${url}")
        postOnUrl(url)
      case _=> Future.successful(())
    }
  }

  def postOnUrl(url: String): Future[Unit] = {
    wsClient.url(url).post("")
    Future.successful(())
  }


}
