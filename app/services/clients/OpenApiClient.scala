package services.clients

import com.github.ghik.silencer.silent
import javax.inject.{Inject, Singleton}
import models.{OpenApiModel, OpenApiPath, OpenApiRow, PageJoinProject, Variable}
import play.api.Logging
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import services.{OpenApiModelModule, OpenApiPathModule}
import services.clients.OpenApiClient._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class OpenApiClient @Inject()(wsClient: WSClient)(implicit ec: ExecutionContext) extends Logging {

  def getOpenApiDescriptor(openApiModule: OpenApiModelModule, pageJoinProject: PageJoinProject): Future[OpenApiModel] = {
    val openApiModuleWithUrlFromVariable = if (openApiModule.openApiUrl.isDefined) {
      openApiModule
    } else {
      openApiModule.copy(openApiUrl = getUrlSwaggerJsonFromVariable(pageJoinProject.project.variables))
    }
    openApiModuleWithUrlFromVariable.openApiUrl.map { url =>
      getOpenApiJsonString(url).map { response =>
        parseSwaggerJsonDefinitions(response, openApiModule.ref.getOrElse(""), openApiModule.deep, openApiModule.label)
      }.recoverWith {
        case NonFatal(_) => Future.successful(OpenApiModel("", Option(Seq()), Seq(), Seq(), Seq(openApiModule.errorMessage.getOrElse(" "))))
      }
    }.getOrElse(Future.successful(OpenApiModel("", Option(Seq()), Seq())))
  }

  def getOpenApiJsonString(openApiUrl: String): Future[String] = {
    wsClient.url(openApiUrl).withRequestTimeout(2.second).get().map { response =>
      if (response.status == 200) {
        response.body
      } else {
        throw new Exception(s"Request to $openApiUrl failed with code ${response.status}")
      }
    }
  }

  def getOpenApiPathSpec(openApiPathModule: OpenApiPathModule, pageJoinProject: PageJoinProject): Future[OpenApiPath] = {
    val openApiModuleWithUrlFromVariable = if (openApiPathModule.openApiUrl.isDefined) {
      openApiPathModule
    } else {
      openApiPathModule.copy(openApiUrl = getUrlSwaggerJsonFromVariable(pageJoinProject.project.variables))
    }
    openApiModuleWithUrlFromVariable.openApiUrl.map { url =>
      getOpenApiJsonString(url).map { response =>
        parseSwaggerJsonPaths(response, openApiPathModule.ref.getOrElse(Seq()), openApiPathModule.methods.getOrElse(Seq()), openApiPathModule.refStartsWith.getOrElse(Seq()))
      }.recoverWith {
        case NonFatal(_) => Future.successful(OpenApiPath(Json.parse("{}"), Seq(openApiPathModule.errorMessage.getOrElse(""))))
      }
    }.getOrElse(Future.successful(OpenApiPath(Json.toJson(""))))
  }
}

@silent("Interpolated")
@silent("missing interpolator")
object OpenApiClient {
  def parseSwaggerJsonDefinitions(swaggerJson: String, reference: String, deep: Option[Int], label: Option[String]): OpenApiModel = {
    var openApiChildren: Seq[OpenApiModel] = Seq()
    val modelName = referenceSplit(reference)
    val jsonTree: JsValue = Json.parse(swaggerJson)
    val wantedModelJson = (jsonTree \ "definitions" \ modelName \ "properties").asOpt[JsObject]
    wantedModelJson match {
      case Some(j) =>
        OpenApiModel(label.getOrElse(modelName), (jsonTree \ "definitions" \ modelName \ "required").asOpt[Seq[String]], j.fields.map {
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

      case _ => OpenApiModel("", Option(Seq()), Seq())
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


  def getSwaggerJsonInfos(swaggerJsonTree: JsObject): JsObject = {
    val swagger = Json.parse(s"""{"swagger":${swaggerJsonTree.value("swagger")}}""").as[JsObject]
    val host = Json.parse(s"""{"host":${swaggerJsonTree.value("host")}}""").as[JsObject]
    val basePath = Json.parse(s"""{"basePath":${swaggerJsonTree.value("basePath")}}""").as[JsObject]
    val definitions = Json.parse(s"""{"definitions":${swaggerJsonTree.value("definitions")}}""").as[JsObject]
    swagger.deepMerge(Json.parse(s"""{"infos": {}}""").as[JsObject]).deepMerge(host).deepMerge(basePath).deepMerge(definitions)

  }

  @silent("Interpolated")
  @silent("missing interpolator")
  def parseSwaggerJsonPaths(swaggerJson: String, reference: Seq[String], methods: Seq[String], refStartsWith: Seq[String]): OpenApiPath = {
    val jsonTree: JsValue = Json.parse(swaggerJson)
    val pathTree = (jsonTree \ "paths").asOpt[JsObject].getOrElse(throw new Exception("the url doesn't lead to a swagger.json"))
    val allRef = reference ++ getAllRefStartWith(refStartsWith, pathTree)
    val pathsString = allRef.map { ref =>
      if (methods.nonEmpty) {
        s""""$ref": {${filterApiMethods(pathTree.value(ref).asOpt[JsObject], methods)}}"""
      } else {
        s""""$ref": ${pathTree.value(ref)}"""
      }
    }.reduce((path1, path2) => path1 + ",\n" + path2)
    val pathsJsonObject = Json.parse("{\"paths\": {" + pathsString + "}}").asOpt[JsObject]
    pathsJsonObject.map(_.deepMerge(jsonTree.as[JsObject]))
    OpenApiPath(Json.toJson(pathsJsonObject.map(_.deepMerge(getSwaggerJsonInfos(jsonTree.asOpt[JsObject].getOrElse(throw new Exception("the url doesn't lead to a swagger.json")))))))
  }

  def getAllRefStartWith(refStartsWith: Seq[String], pathTree: JsObject): Seq[String] = {
    val keys = pathTree.keys
    refStartsWith.flatMap{ref =>
      keys.filter(_.startsWith(ref))
    }
  }

  def filterApiMethods(jsonPathObject: Option[JsObject], methods: Seq[String]): String = {
    methods.map { method =>
      jsonPathObject.map { jsonPathObject =>
        if (jsonPathObject.keys.contains(method.toLowerCase)) {
          s""""${method.toLowerCase}": ${jsonPathObject.value(method.toLowerCase)}"""
        } else {
          ""
        }
      }.getOrElse("")
    }.reduce { (method1, method2) =>
      if (method1 == "") {
        s"$method2"
      } else {
        if (method2 == "") {
          s"$method1"
        } else {
          s"$method1,\n$method2"
        }
      }
    }
  }
}
