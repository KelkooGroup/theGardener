package controllers

import java.io.File

import com.github.ghik.silencer.silent
import controllers.AssetAccessError.{AssetNotAllowed, AssetNotFound}
import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc._
import repositories._
import services._

import scala.concurrent.{ExecutionContext, Future}

@silent("Interpolated")
@silent("missing interpolator")
@Api(value = "PageController", produces = "application/json")
class PageController @Inject()(projectService: ProjectService, pageService: PageService)(implicit ec: ExecutionContext) extends InjectedController {

  @ApiOperation(value = "Get pages from path", response = classOf[PageDTO], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Page not found")))
  def getPageFromPath(path: String): Action[AnyContent] = Action.async {
    pageService.computePageFromPath(path).flatMap {
      case Some(pageWithContent) =>
        val variables = projectService.getVariables(pageWithContent.page)
        Future.successful(Ok(Json.toJson(Seq(PageDTO(pageWithContent.page, pageService.replaceVariablesInMarkdown(pageWithContent.content, variables.getOrElse(Seq())))))))
      case None => Future.successful(NotFound(s"No Page $path"))
    }
  }

  @ApiOperation(value = "Get pages from path", response = classOf[PageDTO], responseContainer = "list")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Page not found")))
  def getSwaggerJson(): Action[AnyContent] = Action{
    Ok(Json.toJson("""
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
    "OpenApi" : {
      "type" : "object",
      "required" : [ "openApiRows" ],
      "properties" : {
        "openApiRows" : {
          "type" : "array",
          "items" : {
            "$ref" : "#/definitions/OpenApiRow"
          }
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
}""" )).withHeaders(
      "Access-Control-Allow-Origin" -> "*"
      , "Access-Control-Allow-Methods" -> "OPTIONS, GET, POST, PUT, DELETE, HEAD"   // OPTIONS for pre-flight
      , "Access-Control-Allow-Headers" -> "Content-Type, api_key, Authorization" //, "X-My-NonStd-Option"
      , "Access-Control-Allow-Credentials" -> "true"
    )
  }
}

sealed abstract class AssetAccessError(message: String) extends Throwable(message)


object AssetAccessError {

  case class AssetNotAllowed(message: String) extends AssetAccessError(message)

  case class AssetNotFound(message: String) extends AssetAccessError(message)

}

class PageAssetController @Inject()(config: Configuration, projectRepository: ProjectRepository)(implicit ec: ExecutionContext) extends InjectedController {

  val projectsRootDirectory = config.get[String]("projects.root.directory")

  def getImageFromPath(path: String): Action[AnyContent] = Action {
    val params = path.split(">")

    (for {
      projectId <- params.lift(0)
      branchName <- params.lift(1)
      relativePath <- params.lift(2)

      documentationRootPath <- projectRepository.findById(projectId).flatMap(_.documentationRootPath)
      assetFileAccess = accessToAsset(s"$projectsRootDirectory/$projectId/$branchName/$documentationRootPath", relativePath)
    } yield (relativePath, assetFileAccess)) match {

      case None => NotFound("Project not found or bad configuration")
      case Some((_, Left(AssetNotAllowed(message)))) => Forbidden(message)
      case Some((_, Left(AssetNotFound(message)))) => NotFound(message)
      case Some((_, Right(assetFile))) => Ok.sendFile(assetFile)
    }
  }

  def accessToAsset(documentationRootPath: String, assetRelativePath: String): Either[AssetAccessError, File] = {
    val assetFile = new File(s"$documentationRootPath/$assetRelativePath")
    val documentationCanonicalPath = new File(documentationRootPath).getCanonicalPath
    val assetCanonicalPath = assetFile.getCanonicalPath

    if (!assetCanonicalPath.contains(documentationCanonicalPath)) {
      Left(AssetNotAllowed(s"Asset $assetRelativePath not allowed"))
    } else if (!assetFile.exists()) {
      Left(AssetNotFound(s"Asset $assetRelativePath not found"))
    } else {
      Right(assetFile)
    }
  }

}
