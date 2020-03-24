package services.clients

import com.github.ghik.silencer.silent
import models.{Branch, Directory, OpenApiModel, OpenApiPath, OpenApiRow, Page, PageJoinProject, Project, Variable}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Results._
import play.api.routing.sird._
import play.api.test.WsTestClient
import play.core.server.Server
import play.api.libs.json._
import services.{OpenApiModelModule, OpenApiPathModule}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@silent("Interpolated")
@silent("missing interpolator")
class OpenApiClientTest extends WordSpec with MustMatchers with MockitoSugar with ScalaFutures {

  val openApiModuleBasic = OpenApiModelModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"), Option(1))

  val openApiModuleWithoutUrl = OpenApiModelModule(None, Option("model"), Option("#/definitions/Project"), Option(1))
  val openApiModuleWithoutDeep = OpenApiModelModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"))
  val openApiModuleWithLabel = OpenApiModelModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"), Option(1), Option("ProjectLabel"))
  val openApiModuleWithDeep2 = OpenApiModelModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"), Option(2))


  val page = Page(1, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)
  val directory = Directory(1, "directory", "label", "description", 0, "relativePath", "path", 1)
  val branch = Branch(1, "master", isStable = true, "suggestionsWS")
  val variable = Variable("${openApi.json.url}", "/api/docs/swagger.json")
  val project = Project("suggestionsWS", "Suggestions WebServices", "git@github.com:library/suggestionsWS.git", "master", Some("^(^master$)|(^feature\\/.*$)"), Some("test/features"), variables = Option(Seq(variable)))
  val pageJoinProject = PageJoinProject(page, directory, branch, project)

  val expectedErrorModel = OpenApiModel("", Option(Seq()), Seq(), Seq(), Seq(" "))

  val expectedErrorPath = OpenApiPath(Json.parse("{}"), Seq(""))

  val required = Option(Seq("id", "name", "repositoryUrl", "stableBranch"))
  val openApiRows = Seq(
    OpenApiRow("id", "string", "", "id of the project", "theGardener"),
    OpenApiRow("name", "string", "", "name of the project", "theGardener"),
    OpenApiRow("repositoryUrl", "string", "", "location of the project", "https://github.com/KelkooGroup/theGardener"),
    OpenApiRow("stableBranch", "string", "", "stableBranch of the project", "master"),
    OpenApiRow("displayedBranches", "string", "", "branches that will be displayed", "qa|master|feature.*|bugfix.*"),
    OpenApiRow("featuresRootPath", "string", "", "path that lead to the feature files", "test/features"),
    OpenApiRow("documentationRootPath", "string", "", "path that lead to the documentation files", "documentation"),
    OpenApiRow("variables", "array of Variable", "", "variables defined for this project", "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]"),
    OpenApiRow("hierarchy", "array of HierarchyNode", "", "Hierarchy matching the project", ""),
    OpenApiRow("branches", "array of Branch", "", "branches of the project", "")
  )
  val expectedResult =
    OpenApiModel("Project", required, openApiRows, Seq())

  val childrenModelDeep2 = Seq(OpenApiModel("Variable", Option(Seq("name", "value")),
    Seq(
      OpenApiRow("name", "string", "", "", ""),
      OpenApiRow("value", "string", "", "", "")
    ), Seq()),
    OpenApiModel("HierarchyNode", Option(Seq("childLabel", "childrenLabel", "id", "name", "slugName")),
      Seq(
        OpenApiRow("id", "string", "", "", ""),
        OpenApiRow("slugName", "string", "", "", ""),
        OpenApiRow("name", "string", "", "", ""),
        OpenApiRow("childrenLabel", "string", "", "", ""),
        OpenApiRow("childLabel", "string", "", "", ""),
        OpenApiRow("directoryPath", "string", "", "", "")
      ), Seq()),
    OpenApiModel("Branch", Option(Seq("features", "id", "isStable", "name", "projectId")),
      Seq(
        OpenApiRow("id", "integer", "", "", ""),
        OpenApiRow("name", "string", "", "", ""),
        OpenApiRow("isStable", "boolean", "", "", ""),
        OpenApiRow("projectId", "string", "", "", ""),
        OpenApiRow("features", "array of string", "", "", ""),
        OpenApiRow("rootDirectory", "Directory", "", "", "")
      ), Seq())
  )

  val basicPathModule = OpenApiPathModule(Option("/api/docs/swagger.json"), Option(Seq("/api/projects/{id}", "/api/directories")))
  val pathModuleWithMethod = OpenApiPathModule(Option("/api/docs/swagger.json"), Option(Seq("/api/projects/{id}", "/api/directories")), Option(Seq("GET")))


  val expectedSpecBasic =
    """
      {
        "basePath":"/",
        "paths":{
           "/api/projects/{id}":{
              "get":{
                 "tags":[
                    "ProjectController"
                 ],
                 "summary":"Get a project",
                 "description":"",
                 "operationId":"getProject",
                 "produces":[
                    "application/json"
                 ],
                 "parameters":[
                    {
                       "name":"id",
                       "in":"path",
                       "description":"Project id",
                       "required":true,
                       "type":"string"
                    }
                 ],
                 "responses":{
                    "200":{
                       "description":"successful operation",
                       "schema":{
                          "$ref":"#/definitions/Project"
                       }
                    },
                    "404":{
                       "description":"Project not found"
                    }
                 }
              },
              "put":{
                 "tags":[
                    "ProjectController"
                 ],
                 "summary":"Update a project",
                 "description":"",
                 "operationId":"updateProject",
                 "produces":[
                    "application/json"
                 ],
                 "parameters":[
                    {
                       "name":"id",
                       "in":"path",
                       "description":"Project id",
                       "required":true,
                       "type":"string"
                    },
                    {
                       "in":"body",
                       "name":"body",
                       "description":"The project to update",
                       "required":true,
                       "schema":{
                          "$ref":"#/definitions/Project"
                       }
                    }
                 ],
                 "responses":{
                    "200":{
                       "description":"successful operation",
                       "schema":{
                          "$ref":"#/definitions/Project"
                       }
                    },
                    "400":{
                       "description":"Incorrect json"
                    },
                    "404":{
                       "description":"Project not found"
                    }
                 }
              },
              "delete":{
                 "tags":[
                    "ProjectController"
                 ],
                 "summary":"Delete a project",
                 "description":"",
                 "operationId":"deleteProject",
                 "produces":[
                    "application/json"
                 ],
                 "parameters":[
                    {
                       "name":"id",
                       "in":"path",
                       "description":"Project id",
                       "required":true,
                       "type":"string"
                    }
                 ],
                 "responses":{
                    "200":{
                       "description":"successful operation",
                       "schema":{
                          "$ref":"#/definitions/ActionAnyContent"
                       }
                    },
                    "404":{
                       "description":"Project not found"
                    }
                 }
              }
           },
           "/api/directories":{
              "get":{
                 "tags":[
                    "DirectoryController"
                 ],
                 "summary":"Get directory from path",
                 "description":"",
                 "operationId":"getDirectoryFromPath",
                 "produces":[
                    "application/json"
                 ],
                 "parameters":[
                    {
                       "name":"path",
                       "in":"query",
                       "required":false,
                       "type":"string"
                    }
                 ],
                 "responses":{
                    "200":{
                       "description":"successful operation",
                       "schema":{
                          "type":"array",
                          "items":{
                             "$ref":"#/definitions/DirectoryDTO"
                          }
                       }
                    },
                    "404":{
                       "description":"Directory not found"
                    }
                 }
              }
           }
        },
        "host":"dc1-thegardener-corp-pp-01.corp.dc1.kelkoo.net:9000",
        "definitions":{
           "Branch":{
              "type":"object",
              "required":[
                 "features",
                 "id",
                 "isStable",
                 "name",
                 "projectId"
              ],
              "properties":{
                 "id":{
                    "type":"integer",
                    "format":"int64"
                 },
                 "name":{
                    "type":"string"
                 },
                 "isStable":{
                    "type":"boolean"
                 },
                 "projectId":{
                    "type":"string"
                 },
                 "features":{
                    "type":"array",
                    "items":{
                       "type":"string"
                    }
                 },
                 "rootDirectory":{
                    "$ref":"#/definitions/Directory"
                 }
              }
           },
           "HierarchyNode":{
              "type":"object",
              "required":[
                 "childLabel",
                 "childrenLabel",
                 "id",
                 "name",
                 "slugName"
              ],
              "properties":{
                 "id":{
                    "type":"string"
                 },
                 "slugName":{
                    "type":"string"
                 },
                 "name":{
                    "type":"string"
                 },
                 "childrenLabel":{
                    "type":"string"
                 },
                 "childLabel":{
                    "type":"string"
                 },
                 "directoryPath":{
                    "type":"string"
                 }
              }
           },
           "Project":{
              "type":"object",
              "required":[
                 "id",
                 "name",
                 "repositoryUrl",
                 "stableBranch"
              ],
              "properties":{
                 "id":{
                    "type":"string",
                    "example":"theGardener",
                    "description":"id of the project"
                 },
                 "name":{
                    "type":"string",
                    "example":"theGardener",
                    "description":"name of the project"
                 },
                 "repositoryUrl":{
                    "type":"string",
                    "example":"https://github.com/KelkooGroup/theGardener",
                    "description":"location of the project"
                 },
                 "stableBranch":{
                    "type":"string",
                    "example":"master",
                    "description":"stableBranch of the project"
                 },
                 "displayedBranches":{
                    "type":"string",
                    "example":"qa|master|feature.*|bugfix.*",
                    "description":"branches that will be displayed"
                 },
                 "featuresRootPath":{
                    "type":"string",
                    "example":"test/features",
                    "description":"path that lead to the feature files"
                 },
                 "documentationRootPath":{
                    "type":"string",
                    "example":"documentation",
                    "description":"path that lead to the documentation files"
                 },
                 "variables":{
                    "type":"array",
                    "example":"[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]",
                    "description":"variables defined for this project",
                    "items":{
                       "$ref":"#/definitions/Variable"
                    }
                 },
                 "hierarchy":{
                    "type":"array",
                    "description":"Hierarchy matching the project",
                    "items":{
                       "$ref":"#/definitions/HierarchyNode"
                    }
                 },
                 "branches":{
                    "type":"array",
                    "description":"branches of the project",
                    "items":{
                       "$ref":"#/definitions/Branch"
                    }
                 }
              }
           },
           "Variable":{
              "type":"object",
              "required":[
                 "name",
                 "value"
              ],
              "properties":{
                 "name":{
                    "type":"string"
                 },
                 "value":{
                    "type":"string"
                 }
              }
           }
        },
        "infos":{

        },
        "swagger":"2.0"
      }
    """

  val expectedSpecWithMethodFilter =
    """
      {
        "basePath":"/",
        "paths":{
           "/api/projects/{id}":{
              "get":{
                 "tags":[
                    "ProjectController"
                 ],
                 "summary":"Get a project",
                 "description":"",
                 "operationId":"getProject",
                 "produces":[
                    "application/json"
                 ],
                 "parameters":[
                    {
                       "name":"id",
                       "in":"path",
                       "description":"Project id",
                       "required":true,
                       "type":"string"
                    }
                 ],
                 "responses":{
                    "200":{
                       "description":"successful operation",
                       "schema":{
                          "$ref":"#/definitions/Project"
                       }
                    },
                    "404":{
                       "description":"Project not found"
                    }
                 }
              }
           },
           "/api/directories":{
              "get":{
                 "tags":[
                    "DirectoryController"
                 ],
                 "summary":"Get directory from path",
                 "description":"",
                 "operationId":"getDirectoryFromPath",
                 "produces":[
                    "application/json"
                 ],
                 "parameters":[
                    {
                       "name":"path",
                       "in":"query",
                       "required":false,
                       "type":"string"
                    }
                 ],
                 "responses":{
                    "200":{
                       "description":"successful operation",
                       "schema":{
                          "type":"array",
                          "items":{
                             "$ref":"#/definitions/DirectoryDTO"
                          }
                       }
                    },
                    "404":{
                       "description":"Directory not found"
                    }
                 }
              }
           }
        },
        "host":"dc1-thegardener-corp-pp-01.corp.dc1.kelkoo.net:9000",
        "definitions":{
          "Branch" : {
            "type" : "object",
            "required" : [ "features", "id", "isStable", "name", "projectId" ],
            "properties" : {
              "id" : {
                "type" : "integer",
                "format" : "int64"
              },
              "name" : {
                "type" : "string"
              },
              "isStable" : {
                "type" : "boolean"
              },
              "projectId" : {
                "type" : "string"
              },
              "features" : {
                "type" : "array",
                "items" : {
                  "type" : "string"
                }
              },
              "rootDirectory" : {
                "$ref" : "#/definitions/Directory"
              }
            }
          },
          "HierarchyNode" : {
            "type" : "object",
            "required" : [ "childLabel", "childrenLabel", "id", "name", "slugName" ],
            "properties" : {
              "id" : {
                "type" : "string"
              },
              "slugName" : {
                "type" : "string"
              },
              "name" : {
                "type" : "string"
              },
              "childrenLabel" : {
                "type" : "string"
              },
              "childLabel" : {
                "type" : "string"
              },
              "directoryPath" : {
                "type" : "string"
              }
            }
          },
          "Project" : {
            "type" : "object",
            "required" : [ "id", "name", "repositoryUrl", "stableBranch" ],
            "properties" : {
              "id" : {
                "type" : "string",
                "example" : "theGardener",
                "description" : "id of the project"
              },
              "name" : {
                "type" : "string",
                "example" : "theGardener",
                "description" : "name of the project"
              },
              "repositoryUrl" : {
                "type" : "string",
                "example" : "https://github.com/KelkooGroup/theGardener",
                "description" : "location of the project"
              },
              "stableBranch" : {
                "type" : "string",
                "example" : "master",
                "description" : "stableBranch of the project"
              },
              "displayedBranches" : {
                "type" : "string",
                "example" : "qa|master|feature.*|bugfix.*",
                "description" : "branches that will be displayed"
              },
              "featuresRootPath" : {
                "type" : "string",
                "example" : "test/features",
                "description" : "path that lead to the feature files"
              },
              "documentationRootPath" : {
                "type" : "string",
                "example" : "documentation",
                "description" : "path that lead to the documentation files"
              },
              "variables" : {
                "type" : "array",
                "example" : "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]",
                "description" : "variables defined for this project",
                "items" : {
                  "$ref" : "#/definitions/Variable"
                }
              },
              "hierarchy" : {
                "type" : "array",
                "description" : "Hierarchy matching the project",
                "items" : {
                  "$ref" : "#/definitions/HierarchyNode"
                }
              },
              "branches" : {
                "type" : "array",
                "description" : "branches of the project",
                "items" : {
                  "$ref" : "#/definitions/Branch"
                }
              }
            }
          },
          "Variable" : {
            "type" : "object",
            "required" : [ "name", "value" ],
            "properties" : {
              "name" : {
                "type" : "string"
              },
              "value" : {
                "type" : "string"
              }
            }
          }
       },
       "infos":{
       },
       "swagger":"2.0"
     }
   """
   val expectedPath = OpenApiPath(Json.parse(expectedSpecBasic))
   val expectedPathWithMethodFilter = OpenApiPath(Json.parse(expectedSpecWithMethodFilter))

  val response1 =
    """
      {
       "swagger" : "2.0",
       "info" : {
         "version" : "1.0",
         "title" : "",
         "contact" : {
           "name" : ""
         },
         "license" : {
           "name" : "",
           "url" : "http://licenseUrl"
         }
       },
       "host" : "dc1-thegardener-corp-pp-01.corp.dc1.kelkoo.net:9000",
       "basePath" : "/",
       "tags" : [ {
         "name" : "AdminController"
       }, {
         "name" : "DirectoryController"
       }, {
         "name" : "PageController"
       }, {
         "name" : "Angular Configuration"
       }, {
         "name" : "ProjectController"
       }, {
         "name" : "GherkinController"
       }, {
         "name" : "HierarchyController"
       }, {
         "name" : "MenuController"
       } ],
       "paths" : {
         "/api/directories" : {
           "get" : {
             "tags" : [ "DirectoryController" ],
             "summary" : "Get directory from path",
             "description" : "",
             "operationId" : "getDirectoryFromPath",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "path",
               "in" : "query",
               "required" : false,
               "type" : "string"
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "type" : "array",
                   "items" : {
                     "$ref" : "#/definitions/DirectoryDTO"
                   }
                 }
               },
               "404" : {
                 "description" : "Directory not found"
               }
             }
           }
         },
         "/api/config" : {
           "get" : {
             "tags" : [ "Angular Configuration" ],
             "summary" : "Get configuration",
             "description" : "",
             "operationId" : "getConfig",
             "produces" : [ "application/json" ],
             "parameters" : [ ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "type" : "object",
                   "additionalProperties" : {
                     "type" : "object"
                   }
                 }
               }
             }
           }
         },
         "/api/projects" : {
           "get" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Get all projects",
             "description" : "",
             "operationId" : "getAllProjects",
             "produces" : [ "application/json" ],
             "parameters" : [ ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "type" : "array",
                   "items" : {
                     "$ref" : "#/definitions/Project"
                   }
                 }
               }
             }
           },
           "post" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Register a new project",
             "description" : "",
             "operationId" : "registerProject",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "in" : "body",
               "name" : "body",
               "description" : "The project to register",
               "required" : true,
               "schema" : {
                 "$ref" : "#/definitions/Project"
               }
             } ],
             "responses" : {
               "201" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/Project"
                 }
               },
               "400" : {
                 "description" : "Incorrect json"
               }
             }
           }
         },
         "/api/projects/{id}" : {
           "get" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Get a project",
             "description" : "",
             "operationId" : "getProject",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project id",
               "required" : true,
               "type" : "string"
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/Project"
                 }
               },
               "404" : {
                 "description" : "Project not found"
               }
             }
           },
           "put" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Update a project",
             "description" : "",
             "operationId" : "updateProject",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project id",
               "required" : true,
               "type" : "string"
             }, {
               "in" : "body",
               "name" : "body",
               "description" : "The project to update",
               "required" : true,
               "schema" : {
                 "$ref" : "#/definitions/Project"
               }
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/Project"
                 }
               },
               "400" : {
                 "description" : "Incorrect json"
               },
               "404" : {
                 "description" : "Project not found"
               }
             }
           },
           "delete" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Delete a project",
             "description" : "",
             "operationId" : "deleteProject",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project id",
               "required" : true,
               "type" : "string"
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/ActionAnyContent"
                 }
               },
               "404" : {
                 "description" : "Project not found"
               }
             }
           }
         },
         "/api/projects/{id}/synchronize" : {
           "post" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Webhook to synchronize a new project",
             "description" : "",
             "operationId" : "synchronizeProject",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project id",
               "required" : true,
               "type" : "string"
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/ActionAnyContent"
                 }
               },
               "404" : {
                 "description" : "Project not found"
               }
             }
           }
         },
         "/api/projects/{id}/hierarchy" : {
           "get" : {
             "tags" : [ "ProjectController" ],
             "summary" : "get the hierarchy link to a project",
             "description" : "",
             "operationId" : "getLinkProjectToHierarchy",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "project Id",
               "required" : true,
               "type" : "string"
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/HierarchyNode"
                 }
               },
               "404" : {
                 "description" : "Project not found"
               }
             }
           }
         },
         "/api/projects/{id}/hierarchy/{hierarchyId}" : {
           "post" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Link a Project to hierarchy",
             "description" : "",
             "operationId" : "linkProjectToHierarchy",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project Id",
               "required" : true,
               "type" : "string"
             }, {
               "name" : "hierarchyId",
               "in" : "path",
               "description" : "Hierarchy Id",
               "required" : true,
               "type" : "string"
             } ],
             "responses" : {
               "201" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/HierarchyNode"
                 }
               },
               "400" : {
                 "description" : "Incorrect json"
               },
               "404" : {
                 "description" : "Project or hierarchy not found"
               }
             }
           },
           "delete" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Delete a link hierarchy to a project",
             "description" : "",
             "operationId" : "deleteLinkProjectToHierarchy",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project id",
               "required" : true,
               "type" : "string"
             }, {
               "name" : "hierarchyId",
               "in" : "path",
               "description" : "Hierarchy Id",
               "required" : true,
               "type" : "string"
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/ActionAnyContent"
                 }
               },
               "404" : {
                 "description" : "Link hierarchy project not found"
               }
             }
           }
         },
         "/api/projects/{id}/variables" : {
           "get" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Get variables from a project",
             "description" : "",
             "operationId" : "getVariables",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project id",
               "required" : true,
               "type" : "string"
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "type" : "array",
                   "items" : {
                     "type" : "object"
                   }
                 }
               },
               "404" : {
                 "description" : "Project not found"
               }
             }
           },
           "put" : {
             "tags" : [ "ProjectController" ],
             "summary" : "Update variables of a project",
             "description" : "",
             "operationId" : "updateVariables",
             "produces" : [ "application/json" ],
             "parameters" : [ {
               "name" : "id",
               "in" : "path",
               "description" : "Project id",
               "required" : true,
               "type" : "string"
             }, {
               "in" : "body",
               "name" : "body",
               "description" : "The variables to update",
               "required" : true,
               "schema" : {
                 "$ref" : "#/definitions/Variable"
               }
             } ],
             "responses" : {
               "200" : {
                 "description" : "successful operation",
                 "schema" : {
                   "$ref" : "#/definitions/Project"
                 }
               },
               "400" : {
                 "description" : "Incorrect json"
               },
               "404" : {
                 "description" : "Project not found"
               }
             }
           }
         }
       },
       "definitions" : {
         "Branch" : {
           "type" : "object",
           "required" : [ "features", "id", "isStable", "name", "projectId" ],
           "properties" : {
             "id" : {
               "type" : "integer",
               "format" : "int64"
             },
             "name" : {
               "type" : "string"
             },
             "isStable" : {
               "type" : "boolean"
             },
             "projectId" : {
               "type" : "string"
             },
             "features" : {
               "type" : "array",
               "items" : {
                 "type" : "string"
               }
             },
             "rootDirectory" : {
               "$ref" : "#/definitions/Directory"
             }
           }
         },
         "HierarchyNode" : {
           "type" : "object",
           "required" : [ "childLabel", "childrenLabel", "id", "name", "slugName" ],
           "properties" : {
             "id" : {
               "type" : "string"
             },
             "slugName" : {
               "type" : "string"
             },
             "name" : {
               "type" : "string"
             },
             "childrenLabel" : {
               "type" : "string"
             },
             "childLabel" : {
               "type" : "string"
             },
             "directoryPath" : {
               "type" : "string"
             }
           }
         },
         "Project" : {
           "type" : "object",
           "required" : [ "id", "name", "repositoryUrl", "stableBranch" ],
           "properties" : {
             "id" : {
               "type" : "string",
               "example" : "theGardener",
               "description" : "id of the project"
             },
             "name" : {
               "type" : "string",
               "example" : "theGardener",
               "description" : "name of the project"
             },
             "repositoryUrl" : {
               "type" : "string",
               "example" : "https://github.com/KelkooGroup/theGardener",
               "description" : "location of the project"
             },
             "stableBranch" : {
               "type" : "string",
               "example" : "master",
               "description" : "stableBranch of the project"
             },
             "displayedBranches" : {
               "type" : "string",
               "example" : "qa|master|feature.*|bugfix.*",
               "description" : "branches that will be displayed"
             },
             "featuresRootPath" : {
               "type" : "string",
               "example" : "test/features",
               "description" : "path that lead to the feature files"
             },
             "documentationRootPath" : {
               "type" : "string",
               "example" : "documentation",
               "description" : "path that lead to the documentation files"
             },
             "variables" : {
               "type" : "array",
               "example" : "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]",
               "description" : "variables defined for this project",
               "items" : {
                 "$ref" : "#/definitions/Variable"
               }
             },
             "hierarchy" : {
               "type" : "array",
               "description" : "Hierarchy matching the project",
               "items" : {
                 "$ref" : "#/definitions/HierarchyNode"
               }
             },
             "branches" : {
               "type" : "array",
               "description" : "branches of the project",
               "items" : {
                 "$ref" : "#/definitions/Branch"
               }
             }
           }
         },
         "Variable" : {
           "type" : "object",
           "required" : [ "name", "value" ],
           "properties" : {
             "name" : {
               "type" : "string"
             },
             "value" : {
               "type" : "string"
             }
           }
         }
       }
      }
      """


  "OpenApiClient" should {
    "parse swagger.json file with base model module" in {

      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)

          val result = Await.result(openApiClient.getOpenApiDescriptor(openApiModuleBasic, pageJoinProject), 10.seconds)
          result.mustBe(expectedResult)
        }
      }
    }

    "parse swagger.json file with model module without openApiUrl using ${openApi.json.url} variable." in {
      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)

          val result = Await.result(openApiClient.getOpenApiDescriptor(openApiModuleWithoutUrl, pageJoinProject), 10.seconds)
          result.mustBe(expectedResult)
        }
      }
    }

    "parse swagger.json file with model module with deep = 2" in {
      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)
          val result = Await.result(openApiClient.getOpenApiDescriptor(openApiModuleWithDeep2, pageJoinProject), 10.seconds)
          result.mustBe(expectedResult.copy(childrenModels = childrenModelDeep2))
        }
      }
    }

    "parse swagger.json file with model module without deep" in {
      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)
          val result = Await.result(openApiClient.getOpenApiDescriptor(openApiModuleWithoutDeep, pageJoinProject), 10.seconds)
          result.mustBe(expectedResult)
        }
      }
    }

    "parse swagger.json file with model module with a label" in {
      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)
          val result = Await.result(openApiClient.getOpenApiDescriptor(openApiModuleWithLabel, pageJoinProject), 10.seconds)
          result.mustBe(expectedResult.copy(modelName = "ProjectLabel"))
        }
      }
    }

    "return an OpenApi with an error if we get an exception" in {
      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(ServiceUnavailable)
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)
          val result = Await.result(openApiClient.getOpenApiDescriptor(openApiModuleWithLabel, pageJoinProject), 10.seconds)
          result.mustBe(expectedErrorModel)
        }
      }
    }

    "parse swagger.json file with base path module" in {

      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)

          val result = Await.result(openApiClient.getOpenApiPathSpec(basicPathModule, pageJoinProject), 10.seconds)
          result.mustBe(expectedPath)
        }
      }
    }

    "parse swagger.json file with path module with method filter" in {

      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>
          val openApiClient = new OpenApiClient(client)
          val result = Await.result(openApiClient.getOpenApiPathSpec(pathModuleWithMethod, pageJoinProject), 10.seconds)
          result.mustBe(expectedPathWithMethodFilter)
        }
      }
    }

    "return an OpenApiPath with an error if we get an exception" in {
      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(ServiceUnavailable)
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)
          val result = Await.result(openApiClient.getOpenApiPathSpec(basicPathModule, pageJoinProject), 10.seconds)
          result.mustBe(expectedErrorPath)
        }
      }
    }
  }
}
