import {Component, OnInit} from '@angular/core';

declare const SwaggerUIBundle: any;

@Component({
  selector: 'app-open-api-end-points',
  templateUrl: './open-api-end-points.component.html',
  styleUrls: ['./open-api-end-points.component.css']
})
export class OpenApiEndPointsComponent implements OnInit {

 // @Input() openApiPathJson: string;

  constructor() { }

   jsonObject = {
    "swagger" : "2.0",
    "info" : {
    },
    "host" : "localhost:9000",
    "basePath" : "/",
    "tags" : [{
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
  };


  ngOnInit(): void {
    SwaggerUIBundle({
      dom_id: '#swagger-ui',
      layout: 'BaseLayout',
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIBundle.SwaggerUIStandalonePreset
      ],
      spec: this.jsonObject,
      docExpansion: 'list',
      operationsSorter: 'alpha',
      defaultModelsExpandDepth: -1,
      enableCORS: false
    });
  }
}
