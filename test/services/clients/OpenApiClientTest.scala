package services.clients

import com.github.ghik.silencer.silent
import models.{Branch, Directory, OpenApi, OpenApiPath, OpenApiRow, Page, PageJoinProject, Project, Variable}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Results.Ok
import play.api.routing.sird._
import play.api.test.WsTestClient
import play.core.server.Server
import services.{OpenApiModule, OpenApiPathModule}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@silent("Interpolated")
@silent("missing interpolator")
class OpenApiClientTest extends WordSpec with MustMatchers with MockitoSugar with ScalaFutures {

  val openApiModuleBasic = OpenApiModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"), Option(1))

  val openApiModuleWithoutUrl = OpenApiModule(None, Option("model"), Option("#/definitions/Project"), Option(1))
  val openApiModuleWithoutDeep = OpenApiModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"))
  val openApiModuleWithLabel = OpenApiModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"), Option(1), Option("ProjectLabel"))
  val openApiModuleWithDeep2 = OpenApiModule(Option("/api/docs/swagger.json"), Option("model"), Option("#/definitions/Project"), Option(2))

  val page = Page(1, "page", "label", "description", 0, Option("markdown"), "relativePath", "path", 1)
  val directory = Directory(1, "directory", "label", "description", 0, "relativePath", "path", 1)
  val branch = Branch(1, "master", isStable = true, "suggestionsWS")
  val variable = Variable("${openApi.json.url}", "/api/docs/swagger.json")
  val project = Project("suggestionsWS", "Suggestions WebServices", "git@github.com:library/suggestionsWS.git", "master", Some("^(^master$)(^feature\\/.*$)"), Some("test/features"), variables = Option(Seq(variable)))
  val pageJoinProject = PageJoinProject(page, directory, branch, project)

  val required = Option(Seq("id", "name", "repositoryUrl", "stableBranch"))
  val openApiRows = Seq(
    OpenApiRow("id", "string", "", "id of the project", "theGardener"),
    OpenApiRow("name", "string", "", "name of the project", "theGardener"),
    OpenApiRow("repositoryUrl", "string", "", "location of the project", "https://github.com/KelkooGroup/theGardener"),
    OpenApiRow("stableBranch", "string", "", "stableBranch of the project", "master"),
    OpenApiRow("displayedBranches", "string", "", "branches that will be displayed", "qamasterfeature.*bugfix.*"),
    OpenApiRow("featuresRootPath", "string", "", "path that lead to the feature files", "test/features"),
    OpenApiRow("documentationRootPath", "string", "", "path that lead to the documentation files", "documentation"),
    OpenApiRow("variables", "array of Variable", "", "variables defined for this project", "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]"),
    OpenApiRow("hierarchy", "array of HierarchyNode", "", "Hierarchy matching the project", ""),
    OpenApiRow("branches", "array of Branch", "", "branches of the project", "")
  )
  val expectedResult =
    OpenApi("Project", required, openApiRows, Seq())

  val childrenModelDeep2 = Seq(OpenApi("Variable", Option(Seq("name", "value")),
    Seq(
      OpenApiRow("name", "string", "", "", ""),
      OpenApiRow("value", "string", "", "", "")
    ), Seq()),
    OpenApi("HierarchyNode", Option(Seq("childLabel", "childrenLabel", "id", "name", "slugName")),
      Seq(
        OpenApiRow("id", "string", "", "", ""),
        OpenApiRow("slugName", "string", "", "", ""),
        OpenApiRow("name", "string", "", "", ""),
        OpenApiRow("childrenLabel", "string", "", "", ""),
        OpenApiRow("childLabel", "string", "", "", ""),
        OpenApiRow("directoryPath", "string", "", "", "")
      ), Seq()),
    OpenApi("Branch", Option(Seq("features", "id", "isStable", "name", "projectId")),
      Seq(
        OpenApiRow("id", "integer", "", "", ""),
        OpenApiRow("name", "string", "", "", ""),
        OpenApiRow("isStable", "boolean", "", "", ""),
        OpenApiRow("projectId", "string", "", "", ""),
        OpenApiRow("features", "array of string", "", "", ""),
        OpenApiRow("rootDirectory", "Directory", "", "", "")
      ), Seq())
  )

  val openApiPathModuleBasic = OpenApiPathModule(Option("/api/docs/swagger.json"),Option(Seq( "/api/projects/{id}", "/api/directories")))
  val openApiPathModuleWithMethod = OpenApiPathModule(Option("/api/docs/swagger.json"),Option(Seq( "/api/projects/{id}", "/api/directories")),Option(Seq("GET")))


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

    "parse swagger.json file with base path module" in {
      Server.withRouterFromComponents()(cs => {
        case GET(p"/api/docs/swagger.json") =>
          cs.defaultActionBuilder(Ok(response1))
      }) { implicit port =>
        WsTestClient.withClient { client =>

          val openApiClient = new OpenApiClient(client)
          val result = Await.result(openApiClient.getOpenApiPathSpec(openApiPathModuleBasic, pageJoinProject), 10.seconds)
          result.mustBe(expectedResultBasicPath)
        }
      }
    }
  }

  val expectedResultBasicPath = ""
  val expectedResultPathWithMethods = ""

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
   "host" : "localhost:9000",
   "basePath" : "/",
   "tags" : [ {
     "name" : "HierarchyController"
   }, {
     "name" : "MenuController"
   }, {
     "name" : "GherkinController"
   }, {
     "name" : "Angular Configuration"
   }, {
     "name" : "DirectoryController"
   }, {
     "name" : "ProjectController"
   }, {
     "name" : "AdminController"
   }, {
     "name" : "PageController"
   } ],
   "paths" : {
     "/api/hierarchy" : {
       "get" : {
         "tags" : [ "HierarchyController" ],
         "summary" : "Get all hierarchies",
         "description" : "",
         "operationId" : "getAllHierarchies",
         "produces" : [ "application/json" ],
         "parameters" : [ ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/HierarchyNode"
             }
           }
         }
       },
       "post" : {
         "tags" : [ "HierarchyController" ],
         "summary" : "Add a  new Hierarchy",
         "description" : "",
         "operationId" : "addHierarchy",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "in" : "body",
           "name" : "body",
           "description" : "The hierarchy to add",
           "required" : true,
           "schema" : {
             "$ref" : "#/definitions/HierarchyNode"
           }
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
           }
         }
       }
     },
     "/api/hierarchy/{id}" : {
       "put" : {
         "tags" : [ "HierarchyController" ],
         "summary" : "Update an hierarchy",
         "description" : "",
         "operationId" : "updateHierarchy",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "id",
           "in" : "path",
           "description" : "Hierarchy id",
           "required" : true,
           "type" : "string"
         }, {
           "in" : "body",
           "name" : "body",
           "description" : "The hierarchy to update",
           "required" : true,
           "schema" : {
             "$ref" : "#/definitions/HierarchyNode"
           }
         } ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/HierarchyNode"
             }
            },
           "400" : {
             "description" : "Incorrect json"
           },
           "404" : {
             "description" : "Hierarchy not found"
           }
         }
       },
       "delete" : {
         "tags" : [ "HierarchyController" ],
         "summary" : "Delete an hierarchy",
         "description" : "",
         "operationId" : "deleteHierarchy",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "id",
           "in" : "path",
           "description" : "Hierarchy id",
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
             "description" : "Hierarchy not found"
           }
         }
       }
     },
     "/api/menu" : {
       "get" : {
         "tags" : [ "MenuController" ],
         "summary" : "Get all menu items",
         "description" : "",
         "operationId" : "getMenu",
         "produces" : [ "application/json" ],
         "parameters" : [ ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/MenuDTO"
             }
           }
         }
       }
     },
     "/api/menu/header" : {
       "get" : {
         "tags" : [ "MenuController" ],
         "summary" : "Get menu header",
         "description" : "",
         "operationId" : "getMenuHeader",
         "produces" : [ "application/json" ],
         "parameters" : [ ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/MenuDTO"
             }
           }
         }
       }
     },
     "/api/menu/submenu/{hierarchy}" : {
       "get" : {
         "tags" : [ "MenuController" ],
         "summary" : "Get submenu",
         "description" : "",
         "operationId" : "getSubMenu",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "hierarchy",
           "in" : "path",
           "required" : true,
           "type" : "string"
         } ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/MenuDTO"
             }
           },
           "404" : {
             "description" : "Menu not found"
           }
         }
       }
     },
     "/api/gherkin" : {
       "get" : {
         "tags" : [ "GherkinController" ],
         "summary" : "Get gherkin",
         "description" : "",
         "operationId" : "generateGherkin",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "project",
           "in" : "query",
           "required" : false,
           "type" : "string",
           "items" : {
             "type" : "string"
           }
         }, {
           "name" : "node",
           "in" : "query",
           "required" : false,
           "type" : "string",
           "items" : {
             "type" : "string"
           }
         } ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/DocumentationDTO"
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
     },
     "/api/admin/menu/refreshFromDatabase" : {
       "post" : {
         "tags" : [ "AdminController" ],
         "summary" : "Refresh menu in cache from the database",
         "description" : "",
         "operationId" : "refreshMenu",
         "produces" : [ "application/json" ],
         "parameters" : [ ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/ActionAnyContent"
             }
           }
         }
       }
     },
     "/api/admin/projects/refreshFromDatabase" : {
       "post" : {
         "tags" : [ "AdminController" ],
         "summary" : "Refresh all projects from the database, get refreshed projects",
         "description" : "",
         "operationId" : "refreshAllProjectsFromDatabase",
         "produces" : [ "application/json" ],
         "parameters" : [ ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "type" : "array",
               "items" : {
                 "type" : "string"
               }
             }
           }
         }
       }
     },
     "/api/admin/projects/{id}/refreshFromDatabase" : {
       "post" : {
         "tags" : [ "AdminController" ],
         "summary" : "Refresh a project from the database, get refreshed branches",
         "description" : "",
         "operationId" : "refreshProjectFromDatabase",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "id",
           "in" : "path",
           "required" : true,
           "type" : "string"
         } ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "type" : "array",
               "items" : {
                 "type" : "string"
               }
             }
           },
           "404" : {
             "description" : "Project not found"
           }
         }
       }
     },
     "/api/admin/projects/refreshFromDisk" : {
       "post" : {
         "tags" : [ "AdminController" ],
         "summary" : "Refresh all projects from the disk, get refreshed projects",
         "description" : "",
         "operationId" : "refreshAllProjectsFromDisk",
         "produces" : [ "application/json" ],
         "parameters" : [ ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "type" : "array",
               "items" : {
                 "type" : "string"
               }
             }
           }
         }
       }
     },
     "/api/admin/projects/{id}/refreshFromDisk" : {
       "post" : {
         "tags" : [ "AdminController" ],
         "summary" : "Refresh a project from the disk, get refreshed branches",
         "description" : "",
         "operationId" : "refreshProjectFromDisk",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "id",
           "in" : "path",
           "required" : true,
           "type" : "string"
         } ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "type" : "array",
               "items" : {
                 "type" : "string"
               }
             }
           },
           "404" : {
             "description" : "Project not found"
           }
         }
       }
     },
     "/api/admin/projects/{id}/synchronizeFromRemoteGitRepository" : {
       "post" : {
         "tags" : [ "AdminController" ],
         "summary" : "Synchronize a project from the remote git repository, get synchronized branches",
         "description" : "",
         "operationId" : "synchronizeProjectFromRemote",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "id",
           "in" : "path",
           "required" : true,
           "type" : "string"
         } ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "type" : "array",
               "items" : {
                 "type" : "string"
               }
             }
           },
           "404" : {
             "description" : "Project not found"
           }
         }
       }
     },
     "/api/admin/projects/{id}/refreshFromRemoteGitRepository" : {
       "post" : {
         "tags" : [ "AdminController" ],
         "summary" : "Refresh projects from the remote git repository",
         "description" : "",
         "operationId" : "refreshProjectFromRemote",
         "produces" : [ "application/json" ],
         "parameters" : [ {
           "name" : "id",
           "in" : "path",
           "required" : true,
           "type" : "string"
         } ],
         "responses" : {
           "200" : {
             "description" : "successful operation",
             "schema" : {
               "$ref" : "#/definitions/ActionAnyContent"
             }
           }
         }
       }
     },
     "/api/pages" : {
       "get" : {
         "tags" : [ "PageController" ],
         "summary" : "Get pages from path",
         "description" : "",
         "operationId" : "getPageFromPath",
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
                 "$ref" : "#/definitions/PageDTO"
               }
             }
           },
           "404" : {
             "description" : "Page not found"
           }
         }
       }
     }
   },
   "definitions" : {
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
     "Action" : {
       "type" : "object"
     },
     "ActionAnyContent" : {
       "type" : "object"
     },
     "Background" : {
       "type" : "object",
       "required" : [ "description", "id", "keyword", "name", "steps" ],
       "properties" : {
         "id" : {
           "type" : "integer",
           "format" : "int64"
         },
         "keyword" : {
           "type" : "string"
         },
         "name" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "steps" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/Step"
           }
         }
       }
     },
     "BranchMenuItemDTO" : {
       "type" : "object",
       "required" : [ "name", "path" ],
       "properties" : {
         "name" : {
           "type" : "string"
         },
         "path" : {
           "type" : "string"
         },
         "rootDirectory" : {
           "$ref" : "#/definitions/DirectoryMenuItemDTO"
         }
       }
     },
     "DirectoryDTO" : {
       "type" : "object",
       "required" : [ "description", "id", "label", "name", "order", "pages", "path" ],
       "properties" : {
         "id" : {
           "type" : "integer",
           "format" : "int64"
         },
         "path" : {
           "type" : "string"
         },
         "name" : {
           "type" : "string"
         },
         "label" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "order" : {
           "type" : "integer",
           "format" : "int32"
         },
         "pages" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/PageDTO"
           }
         }
       }
     },
     "DirectoryMenuItemDTO" : {
       "type" : "object",
       "required" : [ "children", "description", "id", "label", "name", "order", "path" ],
       "properties" : {
         "id" : {
           "type" : "integer",
           "format" : "int64"
         },
         "path" : {
           "type" : "string"
         },
         "name" : {
           "type" : "string"
         },
         "label" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "order" : {
           "type" : "integer",
           "format" : "int32"
         },
         "children" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/DirectoryMenuItemDTO"
           }
         }
       }
     },
     "Feature" : {
       "type" : "object",
       "required" : [ "branchId", "comments", "description", "id", "keyword", "name", "path", "scenarios", "tags" ],
       "properties" : {
         "id" : {
           "type" : "integer",
           "format" : "int64"
         },
         "branchId" : {
           "type" : "integer",
           "format" : "int64"
         },
         "path" : {
           "type" : "string"
         },
         "background" : {
           "$ref" : "#/definitions/Background"
         },
         "tags" : {
           "type" : "array",
           "items" : {
             "type" : "string"
           }
         },
         "language" : {
           "type" : "string"
         },
         "keyword" : {
           "type" : "string"
         },
         "name" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "scenarios" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/ScenarioDefinition"
           }
         },
         "comments" : {
           "type" : "array",
           "items" : {
             "type" : "string"
           }
         }
       }
     },
     "JsValue" : {
       "type" : "object"
     },
     "MenuDTO" : {
       "type" : "object",
       "required" : [ "childLabel", "childrenLabel", "hierarchy", "id", "name", "slugName" ],
       "properties" : {
         "id" : {
           "type" : "string"
         },
         "hierarchy" : {
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
         "projects" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/ProjectMenuItemDTO"
           }
         },
         "children" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/MenuDTO"
           }
         },
         "directory" : {
           "$ref" : "#/definitions/DirectoryDTO"
         }
       }
     },
     "OpenApi" : {
       "type" : "object",
       "required" : [ "childrenModels", "modelName", "openApiRows" ],
       "properties" : {
         "modelName" : {
           "type" : "string"
         },
         "required" : {
           "type" : "array",
           "items" : {
             "type" : "string"
           }
         },
         "openApiRows" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/OpenApiRow"
           }
         },
         "childrenModels" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/OpenApi"
           }
         }
       }
     },
     "OpenApiPath" : {
       "type" : "object",
       "required" : [ "openApiSpec" ],
       "properties" : {
         "openApiSpec" : {
           "$ref" : "#/definitions/JsValue"
         }
       }
     },
     "OpenApiRow" : {
       "type" : "object",
       "required" : [ "default", "description", "example", "openApiType", "title" ],
       "properties" : {
         "title" : {
           "type" : "string"
         },
         "openApiType" : {
           "type" : "string"
         },
         "default" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "example" : {
           "type" : "string"
         }
       }
     },
     "PageDTO" : {
       "type" : "object",
       "required" : [ "content", "description", "label", "name", "order", "path", "relativePath" ],
       "properties" : {
         "path" : {
           "type" : "string"
         },
         "relativePath" : {
           "type" : "string"
         },
         "name" : {
           "type" : "string"
         },
         "label" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "order" : {
           "type" : "integer",
           "format" : "int32"
         },
         "content" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/PageFragment"
           }
         }
       }
     },
     "PageFragment" : {
       "type" : "object",
       "required" : [ "data", "type" ],
       "properties" : {
         "type" : {
           "type" : "string"
         },
         "data" : {
           "$ref" : "#/definitions/PageFragmentContent"
         }
       }
     },
     "PageFragmentContent" : {
       "type" : "object",
       "properties" : {
         "markdown" : {
           "type" : "string"
         },
         "scenarios" : {
           "$ref" : "#/definitions/Feature"
         },
         "includeExternalPage" : {
           "type" : "string"
         },
         "openApi" : {
           "$ref" : "#/definitions/OpenApi"
         },
         "openApiPath" : {
           "$ref" : "#/definitions/OpenApiPath"
         }
       }
     },
     "ProjectMenuItemDTO" : {
       "type" : "object",
       "required" : [ "branches", "id", "label", "path", "stableBranch" ],
       "properties" : {
         "id" : {
           "type" : "string"
         },
         "path" : {
           "type" : "string"
         },
         "label" : {
           "type" : "string"
         },
         "stableBranch" : {
           "type" : "string"
         },
         "branches" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/BranchMenuItemDTO"
           }
         }
       }
     },
     "ScenarioDefinition" : {
       "type" : "object"
     },
     "Step" : {
       "type" : "object",
       "required" : [ "argument", "id", "keyword", "text" ],
       "properties" : {
         "id" : {
           "type" : "integer",
           "format" : "int64"
         },
         "keyword" : {
           "type" : "string"
         },
         "text" : {
           "type" : "string"
         },
         "argument" : {
           "type" : "array",
           "items" : {
             "type" : "array",
             "items" : {
               "type" : "string"
             }
           }
         }
       }
     },
     "BranchDocumentationDTO" : {
       "type" : "object",
       "required" : [ "features", "id", "isStable", "name" ],
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
         "features" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/Feature"
           }
         }
       }
     },
     "DocumentationDTO" : {
       "type" : "object",
       "required" : [ "childLabel", "children", "childrenLabel", "id", "name", "projects", "slugName" ],
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
         "projects" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/ProjectDocumentationDTO"
           }
         },
         "children" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/DocumentationDTO"
           }
         }
       }
     },
     "ProjectDocumentationDTO" : {
       "type" : "object",
       "required" : [ "branches", "id", "name" ],
       "properties" : {
         "id" : {
           "type" : "string"
         },
         "name" : {
           "type" : "string"
         },
         "branches" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/BranchDocumentationDTO"
           }
         }
       }
     },
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
     "Directory" : {
       "type" : "object",
       "required" : [ "branchId", "children", "description", "id", "label", "name", "order", "pages", "path", "relativePath" ],
       "properties" : {
         "id" : {
           "type" : "integer",
           "format" : "int64"
         },
         "name" : {
           "type" : "string"
         },
         "label" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "order" : {
           "type" : "integer",
           "format" : "int32"
         },
         "relativePath" : {
           "type" : "string"
         },
         "path" : {
           "type" : "string"
         },
         "branchId" : {
           "type" : "integer",
           "format" : "int64"
         },
         "pages" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/Page"
           }
         },
         "children" : {
           "type" : "array",
           "items" : {
             "$ref" : "#/definitions/Directory"
           }
         }
       }
     },
     "Page" : {
       "type" : "object",
       "required" : [ "dependOnOpenApi", "description", "directoryId", "id", "label", "name", "order", "path", "relativePath" ],
       "properties" : {
         "id" : {
           "type" : "integer",
           "format" : "int64"
         },
         "name" : {
           "type" : "string"
         },
         "label" : {
           "type" : "string"
         },
         "description" : {
           "type" : "string"
         },
         "order" : {
           "type" : "integer",
           "format" : "int32"
         },
         "markdown" : {
           "type" : "string"
         },
         "relativePath" : {
           "type" : "string"
         },
         "path" : {
           "type" : "string"
         },
         "directoryId" : {
           "type" : "integer",
           "format" : "int64"
         },
         "dependOnOpenApi" : {
           "type" : "boolean"
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
           "example" : "qamasterfeature.*bugfix.*",
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
}
