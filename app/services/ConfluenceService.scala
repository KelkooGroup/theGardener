package services

import play.api.{Configuration, Logging}
import services.clients.ConfluenceClient

import javax.inject.Inject
import scala.concurrent.ExecutionContext

class ConfluenceService @Inject()(config: Configuration, confluenceClient: ConfluenceClient)(implicit ec: ExecutionContext) extends Logging {

}
