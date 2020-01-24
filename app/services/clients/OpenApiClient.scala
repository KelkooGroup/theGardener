package services.clients

import javax.inject.{Inject, Singleton}
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OpenApiClient @Inject()(wsClient: WSClient)(implicit ec: ExecutionContext) {

  def getOpenApiJsonString(openApiUrl: String): Future[String] = {
      wsClient.url(openApiUrl).get().map(_.body)
  }
}
