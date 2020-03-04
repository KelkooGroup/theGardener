package services.clients

import com.github.ghik.silencer.silent
import javax.inject.{Inject, Singleton}
import models.{OpenApi, OpenApiPath, OpenApiRow, PageJoinProject, Variable}
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.WSClient
import services.{OpenApiModule, OpenApiPathModule}
import services.clients.OpenApiClient._

import scala.concurrent.{ExecutionContext, Future}

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
      }
    }.getOrElse(Future.successful(OpenApi("", Option(Seq()), Seq())))
  }

  def getOpenApiJsonString(openApiUrl: String): Future[String] = {
    wsClient.url(openApiUrl).get().map(_.body)
  }

  def getOpenApiPathSpec(openApiPathModule: OpenApiPathModule, pageJoinProject: PageJoinProject): Future[OpenApiPath] = {
    val openApiModuleWithUrlFromVariable = if (openApiPathModule.openApiUrl.isDefined) {
      openApiPathModule
    } else {
      openApiPathModule.copy(openApiUrl = getUrlSwaggerJsonFromVariable(pageJoinProject.project.variables))
    }
    openApiModuleWithUrlFromVariable.openApiUrl.map { url =>
      getOpenApiJsonString(url).map { response =>
        parseSwaggerJsonPaths(response, openApiPathModule.ref.getOrElse(Seq("")), openApiPathModule.methods.getOrElse(Seq("")))
      }
    }.getOrElse(Future.successful(OpenApiPath(Json.toJson(""))))
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

  def parseSwaggerJsonPaths(swaggerJson: String, reference: Seq[String], methods: Seq[String]): OpenApiPath = {
    val jsonTree: JsValue = Json.parse(swaggerJson)
    jsonTree \ "paths"
    reference.map(ref => ref.charAt(1))
    methods.toIndexedSeq
    OpenApiPath(Json.toJson("{\n    \"swagger\" : \"2.0\",\n    \"info\" : {\n    },\n    \"host\" : \"localhost:9000\",\n    \"basePath\" : \"/\",\n    \"tags\" : [{\n      \"name\" : \"ProjectController\"\n    } ],\n    \"paths\" : {\n      \"/api/projects/{id}\" : {\n        \"get\" : {\n          \"tags\" : [ \"ProjectController\" ],\n          \"summary\" : \"Get a project\",\n          \"description\" : \"\",\n          \"operationId\" : \"getProject\",\n          \"produces\" : [ \"application/json\" ],\n          \"parameters\" : [ {\n            \"name\" : \"id\",\n            \"in\" : \"path\",\n            \"description\" : \"Project id\",\n            \"required\" : true,\n            \"type\" : \"string\"\n          } ],\n          \"responses\" : {\n            \"200\" : {\n              \"description\" : \"successful operation\",\n              \"schema\" : {\n                \"$ref\" : \"#/definitions/Project\"\n              }\n            },\n            \"404\" : {\n              \"description\" : \"Project not found\"\n            }\n          }\n        },\n        \"put\" : {\n          \"tags\" : [ \"ProjectController\" ],\n          \"summary\" : \"Update a project\",\n          \"description\" : \"\",\n          \"operationId\" : \"updateProject\",\n          \"produces\" : [ \"application/json\" ],\n          \"parameters\" : [ {\n            \"name\" : \"id\",\n            \"in\" : \"path\",\n            \"description\" : \"Project id\",\n            \"required\" : true,\n            \"type\" : \"string\"\n          }, {\n            \"in\" : \"body\",\n            \"name\" : \"body\",\n            \"description\" : \"The project to update\",\n            \"required\" : true,\n            \"schema\" : {\n              \"$ref\" : \"#/definitions/Project\"\n            }\n          } ],\n          \"responses\" : {\n            \"200\" : {\n              \"description\" : \"successful operation\",\n              \"schema\" : {\n                \"$ref\" : \"#/definitions/Project\"\n              }\n            },\n            \"400\" : {\n              \"description\" : \"Incorrect json\"\n            },\n            \"404\" : {\n              \"description\" : \"Project not found\"\n            }\n          }\n        },\n        \"delete\" : {\n          \"tags\" : [ \"ProjectController\" ],\n          \"summary\" : \"Delete a project\",\n          \"description\" : \"\",\n          \"operationId\" : \"deleteProject\",\n          \"produces\" : [ \"application/json\" ],\n          \"parameters\" : [ {\n            \"name\" : \"id\",\n            \"in\" : \"path\",\n            \"description\" : \"Project id\",\n            \"required\" : true,\n            \"type\" : \"string\"\n          } ],\n          \"responses\" : {\n            \"200\" : {\n              \"description\" : \"successful operation\",\n              \"schema\" : {\n                \"$ref\" : \"#/definitions/ActionAnyContent\"\n              }\n            },\n            \"404\" : {\n              \"description\" : \"Project not found\"\n            }\n          }\n        }\n      }\n    },\n    \"definitions\" : {\n      \"OpenApi\" : {\n        \"type\" : \"object\",\n        \"required\" : [ \"openApiRows\" ],\n        \"properties\" : {\n          \"openApiRows\" : {\n            \"type\" : \"array\",\n            \"items\" : {\n              \"$ref\" : \"#/definitions/OpenApiRow\"\n            }\n          }\n        }\n      },\n      \"OpenApiRow\" : {\n        \"type\" : \"object\",\n        \"required\" : [ \"default\", \"description\", \"example\", \"openApiType\", \"title\" ],\n        \"properties\" : {\n          \"title\" : {\n            \"type\" : \"string\"\n          },\n          \"openApiType\" : {\n            \"type\" : \"string\"\n          },\n          \"default\" : {\n            \"type\" : \"string\"\n          },\n          \"description\" : {\n            \"type\" : \"string\"\n          },\n          \"example\" : {\n            \"type\" : \"string\"\n          }\n        }\n      },\n      \"HierarchyNode\" : {\n        \"type\" : \"object\",\n        \"required\" : [ \"childLabel\", \"childrenLabel\", \"id\", \"name\", \"slugName\" ],\n        \"properties\" : {\n          \"id\" : {\n            \"type\" : \"string\"\n          },\n          \"slugName\" : {\n            \"type\" : \"string\"\n          },\n          \"name\" : {\n            \"type\" : \"string\"\n          },\n          \"childrenLabel\" : {\n            \"type\" : \"string\"\n          },\n          \"childLabel\" : {\n            \"type\" : \"string\"\n          },\n          \"directoryPath\" : {\n            \"type\" : \"string\"\n          }\n        }\n      },\n      \"Branch\" : {\n        \"type\" : \"object\",\n        \"required\" : [ \"features\", \"id\", \"isStable\", \"name\", \"projectId\" ],\n        \"properties\" : {\n          \"id\" : {\n            \"type\" : \"integer\",\n            \"format\" : \"int64\"\n          },\n          \"name\" : {\n            \"type\" : \"string\"\n          },\n          \"isStable\" : {\n            \"type\" : \"boolean\"\n          },\n          \"projectId\" : {\n            \"type\" : \"string\"\n          },\n          \"features\" : {\n            \"type\" : \"array\",\n            \"items\" : {\n              \"type\" : \"string\"\n            }\n          },\n          \"rootDirectory\" : {\n            \"$ref\" : \"#/definitions/Directory\"\n          }\n        }\n      },\n      \"Project\" : {\n        \"type\" : \"object\",\n        \"required\" : [ \"id\", \"name\", \"repositoryUrl\", \"stableBranch\" ],\n        \"properties\" : {\n          \"id\" : {\n            \"type\" : \"string\",\n            \"example\" : \"theGardener\",\n            \"description\" : \"id of the project\"\n          },\n          \"name\" : {\n            \"type\" : \"string\",\n            \"example\" : \"theGardener\",\n            \"description\" : \"name of the project\"\n          },\n          \"repositoryUrl\" : {\n            \"type\" : \"string\",\n            \"example\" : \"https://github.com/KelkooGroup/theGardener\",\n            \"description\" : \"location of the project\"\n          },\n          \"stableBranch\" : {\n            \"type\" : \"string\",\n            \"example\" : \"master\",\n            \"description\" : \"stableBranch of the project\"\n          },\n          \"displayedBranches\" : {\n            \"type\" : \"string\",\n            \"example\" : \"qa|master|feature.*|bugfix.*\",\n            \"description\" : \"branches that will be displayed\"\n          },\n          \"featuresRootPath\" : {\n            \"type\" : \"string\",\n            \"example\" : \"test/features\",\n            \"description\" : \"path that lead to the feature files\"\n          },\n          \"documentationRootPath\" : {\n            \"type\" : \"string\",\n            \"example\" : \"documentation\",\n            \"description\" : \"path that lead to the documentation files\"\n          },\n          \"variables\" : {\n            \"type\" : \"array\",\n            \"example\" : \"[{\\\"name\\\":\\\"${swagger.url}\\\",\\\"value\\\":\\\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\\\"}]\",\n            \"description\" : \"variables defined for this project\",\n            \"items\" : {\n              \"$ref\" : \"#/definitions/Variable\"\n            }\n          },\n          \"hierarchy\" : {\n            \"type\" : \"array\",\n            \"description\" : \"Hierarchy matching the project\",\n            \"items\" : {\n              \"$ref\" : \"#/definitions/HierarchyNode\"\n            }\n          },\n          \"branches\" : {\n            \"type\" : \"array\",\n            \"description\" : \"branches of the project\",\n            \"items\" : {\n              \"$ref\" : \"#/definitions/Branch\"\n            }\n          }\n        }\n      },\n      \"Variable\" : {\n        \"type\" : \"object\",\n        \"required\" : [ \"name\", \"value\" ],\n        \"properties\" : {\n          \"name\" : {\n            \"type\" : \"string\"\n          },\n          \"value\" : {\n            \"type\" : \"string\"\n          }\n        }\n      }\n    }\n  }"))
  }

  def getSwaggerJsonInfos(swaggerJson: String): JsObject = {
  Json.toJson(swaggerJson).as[JsObject]
  }
}
