package services.clients

import com.github.ghik.silencer.silent
import javax.inject.{Inject, Singleton}
import models.{OpenApi, OpenApiRow, PageJoinProject, Variable}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import services.OpenApiModule
import services.clients.OpenApiClient._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class OpenApiClient @Inject()(wsClient: WSClient)(implicit ec: ExecutionContext) {

  def getOpenApiDescriptor(openApiModule: OpenApiModule, pageJoinProject: PageJoinProject): Future[OpenApi] = {
    val openApiModuleWithUrlFromVariable = if (openApiModule.openApiUrl.isDefined) {
      openApiModule
    } else {
      openApiModule.copy(openApiUrl = getUrlSwaggerJsonFromVariable(pageJoinProject.project.variables))
    }
    openApiModuleWithUrlFromVariable.openApiUrl.map { url =>
      getOpenApiJsonString(url).map { response =>
        parseSwaggerJsonDefinitions(response, openApiModule.ref.getOrElse(""), openApiModule.deep, openApiModule.label)
      }.recoverWith {
        case NonFatal(_) => Future.successful(OpenApi("", Option(Seq()), Seq(), Seq(), Seq(openApiModule.errorMessage.getOrElse(" "))))
      }
    }.getOrElse(Future.successful(OpenApi("", Option(Seq()), Seq())))
  }

  def getOpenApiJsonString(openApiUrl: String): Future[String] = {
    wsClient.url(openApiUrl).withRequestTimeout(1.second).get().map { response =>
      if (response.status == 200) {
        response.body
      } else {
        throw new Exception(s"Request to $openApiUrl failed with code ${response.status}")
      }
    }
  }
}

@silent("Interpolated")
@silent("missing interpolator")
object OpenApiClient {
  def parseSwaggerJsonDefinitions(swaggerJson: String, reference: String, deep: Option[Int], label: Option[String]): OpenApi = {
    var openApiChildren: Seq[OpenApi] = Seq()
    val modelName = referenceSplit(reference)
    val jsonTree: JsValue = Json.parse(swaggerJson)
    val wantedModelJson = (jsonTree \ "definitions" \ modelName \ "properties").asOpt[JsObject]
    wantedModelJson match {
      case Some(j) =>
        OpenApi(label.getOrElse(modelName), (jsonTree \ "definitions" \ modelName \ "required").asOpt[Seq[String]], j.fields.map {
          case (name, properties: JsObject) =>
            val valueMap = properties.value
            val openApiType = if (valueMap.get("type").isDefined) {
              valueMap.get("type").flatMap(_.asOpt[String])
            } else {
              Option(referenceSplit(valueMap.get("$ref").flatMap(_.asOpt[String]).getOrElse("")))
            }
            val example = valueMap.get("example").flatMap(_.asOpt[String])
            val description = valueMap.get("description").flatMap(_.asOpt[String])
            if (openApiType.getOrElse("") != "array") {
              OpenApiRow(name, openApiType.getOrElse(""), "", description.getOrElse(""), example.getOrElse(""))
            } else {
              val arrayDefinitionReference: String = (jsonTree \ "definitions" \ modelName \ "properties" \ name \ "items" \ "$ref").asOpt[String].getOrElse("")
              val arrayModelRefName = if (arrayDefinitionReference != "") {
                referenceSplit(arrayDefinitionReference)
              } else {
                (jsonTree \ "definitions" \ modelName \ "properties" \ name \ "items" \ "type").asOpt[String].getOrElse("")
              }
              if (deep.getOrElse(1) > 1) {
                openApiChildren = openApiChildren :+ parseSwaggerJsonDefinitions(swaggerJson, arrayDefinitionReference, deep.map(_ - 1), None)
              }
              OpenApiRow(name, s"array of $arrayModelRefName", "", description.getOrElse(""), example.getOrElse(""))
            }
          case _ =>
            OpenApiRow("", "", "", "", "")
        }, openApiChildren)

      case _ => OpenApi("", Option(Seq()), Seq())
    }
  }

  private def referenceSplit(ref: String): String = {
    if (ref != null && !ref.isEmpty) {
      val splitReference = ref.split("/")
      splitReference(splitReference.length - 1)
    } else {
      ""
    }
  }

  def getUrlSwaggerJsonFromVariable(variables: Option[Seq[Variable]]): Option[String] = {
    variables.flatMap { variable =>
      variable.find(_.name == "${openApi.json.url}").map(_.value)
    }
  }
}
