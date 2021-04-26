package services.clients

import javax.inject._
import play.api.libs.ws.WSClient
import play.api.{Configuration, Logging}

import scala.concurrent._


class ReplicaClient @Inject()(config: Configuration, wsClient: WSClient) extends Logging {

  val replicaUrlOpt = config.getOptional[String]("replica.url")


  def triggerSychronizeOnReplica(projectId: String): Future[Unit] = {
    replicaUrlOpt match {
      case Some(replicaUrl) =>
        val url = s"$replicaUrl/api/admin/projects/$projectId/refreshFromDatabase"
        logger.info(s"Trigger the synchronize on $projectId with url $url")
        postOnUrl(url)
      case _ => Future.successful(())
    }
  }

  def postOnUrl(url: String): Future[Unit] = {
    wsClient.url(url).post("")
    Future.successful(())
  }


}
