package services.clients

import com.github.ghik.silencer.silent
import models.{Branch, Directory, OpenApi, OpenApiRow, Page, PageJoinProject, Project, Variable}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.Results._
import play.api.routing.sird._
import play.api.test.WsTestClient
import play.core.server.Server
import services.OpenApiModule

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
  val project = Project("suggestionsWS", "Suggestions WebServices", "git@github.com:library/suggestionsWS.git", "master", Some("^(^master$)|(^feature\\/.*$)"), Some("test/features"), variables = Option(Seq(variable)))
  val pageJoinProject = PageJoinProject(page, directory, branch, project)

  val expectedResultError= OpenApi("",Option(Seq()),Seq(),Seq(),Seq("Request to /api/docs/swagger.json failed with code 503"))

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
    "name" : "MenuController"
  }, {
    "name" : "GherkinController"
  }, {
    "name" : "HierarchyController"
  }, {
    "name" : "DirectoryController"
  }, {
    "name" : "Angular Configuration"
  }, {
    "name" : "AdminController"
  }, {
    "name" : "PageController"
  }, {
    "name" : "ProjectController"
  } ],
  "paths" : {
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
    "parse swagger.json file with base module" in {

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

    "parse swagger.json file with module without openApiUrl using ${openApi.json.url} variable." in {
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

    "parse swagger.json file with module with deep = 2" in {
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

    "parse swagger.json file with module without deep" in {
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

    "parse swagger.json file with module with a label" in {
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
          result.mustBe(expectedResultError)
        }
      }
    }
  }
}
