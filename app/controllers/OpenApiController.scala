package controllers

import io.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses}
import javax.inject._
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc._

import scala.concurrent.ExecutionContext

class OpenApiController @Inject()(ws: WSClient)(implicit ec: ExecutionContext) extends InjectedController {

  @ApiOperation(value = "Apply get method for swagger ui", response = classOf[Any])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Action not found")))
  def getSwaggerResponse(url: String): Action[AnyContent] = Action.async {
    ws.url(url.replaceAll("amp", "&")).get().map(result => Ok(Json.parse(result.body)))
  }

  @ApiOperation(value = "Apply put method for swagger ui", response = classOf[Any])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Action not found")))
  def swaggerPutMethod(url: String, body: String): Action[AnyContent] = Action.async {
    if (!body.equals("undefined")) {
      ws.url(url.replaceAll("amp", "&")).put(Json.parse(body)).map(result => Ok(result.body))
    } else {
      ws.url(url.replaceAll("amp", "&")).put(Json.parse("{}")).map(result => Ok(result.body))
    }
  }

  @ApiOperation(value = "Apply post method for swagger ui", response = classOf[Any])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Action not found")))
  def swaggerPostMethod(url: String, body: String): Action[AnyContent] = Action.async {
    if (!body.equals("undefined")) {
      ws.url(url.replaceAll("amp", "&")).post(Json.parse(body)).map(result => Ok(result.body))
    } else {
      ws.url(url.replaceAll("amp", "&")).post(Json.parse("{}")).map(result => Ok(result.body))
    }
  }

  @ApiOperation(value = "Apply delete method for swagger ui", response = classOf[Any])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Action not found")))
  def swaggerDeleteMethod(url: String): Action[AnyContent] = Action.async {
    ws.url(url.replaceAll("amp", "&")).delete().map(result => Ok(result.body))
  }

}
