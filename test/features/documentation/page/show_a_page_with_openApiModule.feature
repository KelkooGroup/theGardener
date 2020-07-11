Feature: Generate a documentation page with OpenApi Module

  Background:
    Given the database is empty
    And the cache is empty
    And No project is checkout
    And the remote projects are empty
    And the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel |
      | .    | root       | Hierarchy root    | Views         | View       |
      | .01. | suggestion | Suggestion system | Projects      | Project    |
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | variables                                                                           |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   | [{"name":"${openApi.json.url}","value":"http://theGardener.com/api/docs/swagger.json"}] |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 1  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And we have those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown | directoryId |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | [empty]        | 1           |
    And we have the following swagger.json hosted on "http://theGardener.com/api/docs/swagger.json"
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
}
      """

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with the inclusion of an openApi model
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
```thegardener
{
  "page" :
     {
        "label": "Read documentation",
        "description": "How to read documentation provided by theGardener ?"
     }
}
```
```thegardener
{
  "openApi" :
     {
        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 1
     }
}
```
"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "openApi",
        "data": {
          "openApi": {
            "modelName": "Project",
            "required" : [ "id", "name", "repositoryUrl", "stableBranch" ],
            "openApiRows": [
                            {"title" : "id", "openApiType": "string", "default": "", "description": "id of the project", "example": "theGardener"},
                            {"title" : "name", "openApiType": "string", "default": "", "description": "name of the project", "example": "theGardener"},
                            {"title" : "repositoryUrl", "openApiType": "string", "default": "", "description": "location of the project", "example": "https://github.com/KelkooGroup/theGardener"},
                            {"title" : "stableBranch", "openApiType": "string", "default": "", "description": "stableBranch of the project", "example": "master"},
                            {"title" : "displayedBranches", "openApiType": "string", "default": "", "description": "branches that will be displayed", "example": "qa|master|feature.*|bugfix.*"},
                            {"title" : "featuresRootPath", "openApiType": "string", "default": "", "description": "path that lead to the feature files", "example": "test/features"},
                            {"title" : "documentationRootPath", "openApiType": "string", "default": "", "description": "path that lead to the documentation files", "example": "documentation"},
                            {"title" : "variables", "openApiType": "array of Variable", "default": "", "description": "variables defined for this project", "example": "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]"},
                            {"title" : "hierarchy", "openApiType": "array of HierarchyNode", "default": "", "description": "Hierarchy matching the project", "example": ""},
                            {"title" : "branches", "openApiType": "array of Branch", "default": "", "description": "branches of the project", "example": ""}
            ],
            "childrenModels": [],
            "errors":[]
          }
        }
      }
    ]
  }
]
"""
    And we have now those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown                                                                                                                                                                                                                                                                                                                                                                                              | directoryId | dependOnOpenApi |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n{\n  "page" :\n     {\n        "label": "Read documentation",\n        "description": "How to read documentation provided by theGardener ?"\n     }\n}\n```\n```thegardener\n{\n  "openApi" :\n     {\n        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",\n        "openApiType": "model",\n        "ref": "#/definitions/Project",\n        "deep": 1\n     }\n}\n``` | 1           | true            |


  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with the inclusion of an openApi model without openApiUrl and with a label
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
```thegardener
{
  "page" :
     {
        "label": "Read documentation",
        "description": "How to read documentation provided by theGardener ?"
     }
}
```
```thegardener
{
  "openApi" :
     {
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 1,
        "label": "ProjectLabel"
     }
}
```
"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "openApi",
        "data": {
          "openApi": {
            "modelName": "ProjectLabel",
            "required" : [ "id", "name", "repositoryUrl", "stableBranch" ],
            "openApiRows": [
                            {"title" : "id", "openApiType": "string", "default": "", "description": "id of the project", "example": "theGardener"},
                            {"title" : "name", "openApiType": "string", "default": "", "description": "name of the project", "example": "theGardener"},
                            {"title" : "repositoryUrl", "openApiType": "string", "default": "", "description": "location of the project", "example": "https://github.com/KelkooGroup/theGardener"},
                            {"title" : "stableBranch", "openApiType": "string", "default": "", "description": "stableBranch of the project", "example": "master"},
                            {"title" : "displayedBranches", "openApiType": "string", "default": "", "description": "branches that will be displayed", "example": "qa|master|feature.*|bugfix.*"},
                            {"title" : "featuresRootPath", "openApiType": "string", "default": "", "description": "path that lead to the feature files", "example": "test/features"},
                            {"title" : "documentationRootPath", "openApiType": "string", "default": "", "description": "path that lead to the documentation files", "example": "documentation"},
                            {"title" : "variables", "openApiType": "array of Variable", "default": "", "description": "variables defined for this project", "example": "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]"},
                            {"title" : "hierarchy", "openApiType": "array of HierarchyNode", "default": "", "description": "Hierarchy matching the project", "example": ""},
                            {"title" : "branches", "openApiType": "array of Branch", "default": "", "description": "branches of the project", "example": ""}
            ],
            "childrenModels": [],
            "errors":[]
          }
        }
      }
    ]
  }
]
"""
    And we have now those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown                                                                                                                                                                                                                                                                                                                                                                                              | directoryId | dependOnOpenApi |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n{\n  "page" :\n     {\n        "label": "Read documentation",\n        "description": "How to read documentation provided by theGardener ?"\n     }\n}\n```\n```thegardener\n{\n  "openApi" :\n     {\n        "openApiType": "model",\n        "ref": "#/definitions/Project",\n        "deep": 1,\n        "label": "ProjectLabel"\n     }\n}\n``` | 1           | true            |

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with the inclusion of an openApi model with deep > 1
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
```thegardener
{
  "page" :
     {
        "label": "Read documentation",
        "description": "How to read documentation provided by theGardener ?"
     }
}
```
```thegardener
{
  "openApi" :
     {
        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 2
     }
}
```
"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "openApi",
        "data": {
          "openApi": {
            "modelName": "Project",
            "required" : [ "id", "name", "repositoryUrl", "stableBranch" ],
            "openApiRows": [
                            {"title" : "id", "openApiType": "string", "default": "", "description": "id of the project", "example": "theGardener"},
                            {"title" : "name", "openApiType": "string", "default": "", "description": "name of the project", "example": "theGardener"},
                            {"title" : "repositoryUrl", "openApiType": "string", "default": "", "description": "location of the project", "example": "https://github.com/KelkooGroup/theGardener"},
                            {"title" : "stableBranch", "openApiType": "string", "default": "", "description": "stableBranch of the project", "example": "master"},
                            {"title" : "displayedBranches", "openApiType": "string", "default": "", "description": "branches that will be displayed", "example": "qa|master|feature.*|bugfix.*"},
                            {"title" : "featuresRootPath", "openApiType": "string", "default": "", "description": "path that lead to the feature files", "example": "test/features"},
                            {"title" : "documentationRootPath", "openApiType": "string", "default": "", "description": "path that lead to the documentation files", "example": "documentation"},
                            {"title" : "variables", "openApiType": "array of Variable", "default": "", "description": "variables defined for this project", "example": "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]"},
                            {"title" : "hierarchy", "openApiType": "array of HierarchyNode", "default": "", "description": "Hierarchy matching the project", "example": ""},
                            {"title" : "branches", "openApiType": "array of Branch", "default": "", "description": "branches of the project", "example": ""}
            ],
            "childrenModels": [
                                {
                                    "modelName": "Variable",
                                    "required" : [ "name", "value" ],
                                    "openApiRows": [
                                                    {"title" : "name", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "value", "openApiType": "string", "default": "", "description": "", "example": ""}
                                    ],
                                    "childrenModels": [],
                                    "errors":[]

                                },
                                {
                                    "modelName": "HierarchyNode",
                                    "required" : [ "childLabel", "childrenLabel", "id", "name", "slugName" ],
                                    "openApiRows": [
                                                    {"title" : "id", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "slugName", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "name", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "childrenLabel", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "childLabel", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "directoryPath", "openApiType": "string", "default": "", "description": "", "example": ""}
                                    ],
                                    "childrenModels": [],
                                    "errors":[]

                                },
                                {
                                    "modelName": "Branch",
                                    "required" : [ "features", "id", "isStable", "name", "projectId" ],
                                    "openApiRows": [
                                                    {"title" : "id", "openApiType": "integer", "default": "", "description": "", "example": ""},
                                                    {"title" : "name", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "isStable", "openApiType": "boolean", "default": "", "description": "", "example": ""},
                                                    {"title" : "projectId", "openApiType": "string", "default": "", "description": "", "example": ""},
                                                    {"title" : "features", "openApiType": "array of string", "default": "", "description": "", "example": ""},
                                                    {"title" : "rootDirectory", "openApiType": "Directory", "default": "", "description": "", "example": ""}
                                    ],
                                    "childrenModels": [],
                                    "errors":[]

                                }
            ],
            "errors":[]
          }
        }
      }
    ]
  }
]
"""
    And we have now those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown                                                                                                                                                                                                                                                                                                                                                                                              | directoryId | dependOnOpenApi |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n{\n  "page" :\n     {\n        "label": "Read documentation",\n        "description": "How to read documentation provided by theGardener ?"\n     }\n}\n```\n```thegardener\n{\n  "openApi" :\n     {\n        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",\n        "openApiType": "model",\n        "ref": "#/definitions/Project",\n        "deep": 2\n     }\n}\n``` | 1           | true            |


  @level_2_technical_details @nominal_case @valid
  Scenario: recompute a page depending on openApi everytime when the project synchronization is triggered
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
```thegardener
{
  "page" :
     {
        "label": "Read documentation",
        "description": "How to read documentation provided by theGardener ?"
     }
}
```
```thegardener
{
  "openApi" :
     {
        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 1
     }
}
```
"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "openApi",
        "data": {
          "openApi": {
            "modelName": "Project",
            "required" : [ "id", "name", "repositoryUrl", "stableBranch" ],
            "openApiRows": [
                            {"title" : "id", "openApiType": "string", "default": "", "description": "id of the project", "example": "theGardener"},
                            {"title" : "name", "openApiType": "string", "default": "", "description": "name of the project", "example": "theGardener"},
                            {"title" : "repositoryUrl", "openApiType": "string", "default": "", "description": "location of the project", "example": "https://github.com/KelkooGroup/theGardener"},
                            {"title" : "stableBranch", "openApiType": "string", "default": "", "description": "stableBranch of the project", "example": "master"},
                            {"title" : "displayedBranches", "openApiType": "string", "default": "", "description": "branches that will be displayed", "example": "qa|master|feature.*|bugfix.*"},
                            {"title" : "featuresRootPath", "openApiType": "string", "default": "", "description": "path that lead to the feature files", "example": "test/features"},
                            {"title" : "documentationRootPath", "openApiType": "string", "default": "", "description": "path that lead to the documentation files", "example": "documentation"},
                            {"title" : "variables", "openApiType": "array of Variable", "default": "", "description": "variables defined for this project", "example": "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]"},
                            {"title" : "hierarchy", "openApiType": "array of HierarchyNode", "default": "", "description": "Hierarchy matching the project", "example": ""},
                            {"title" : "branches", "openApiType": "array of Branch", "default": "", "description": "branches of the project", "example": ""}
            ],
            "childrenModels": [],
            "errors":[]
          }
        }
      }
    ]
  }
]
"""
    And we have now those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown                                                                                                                                                                                                                                                                                                                                                                                              | directoryId | dependOnOpenApi |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n{\n  "page" :\n     {\n        "label": "Read documentation",\n        "description": "How to read documentation provided by theGardener ?"\n     }\n}\n```\n```thegardener\n{\n  "openApi" :\n     {\n        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",\n        "openApiType": "model",\n        "ref": "#/definitions/Project",\n        "deep": 1\n     }\n}\n``` | 1           | true            |

    When the swagger.json hosted on "http://theGardener.com/api/docs/swagger.json" is now
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
    When I perform a "POST" on following URL "/api/admin/projects/suggestionsWS/refreshFromDatabase"
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "openApi",
        "data": {
          "openApi": {
            "modelName": "Project",
            "required" : [ "id", "name", "repositoryUrl", "stableBranch" ],
            "openApiRows": [
                            {"title" : "id", "openApiType": "string", "default": "", "description": "id of the project", "example": "theGardener"},
                            {"title" : "name", "openApiType": "string", "default": "", "description": "name of the project", "example": "theGardener"},
                            {"title" : "repositoryUrl", "openApiType": "string", "default": "", "description": "location of the project", "example": "https://github.com/KelkooGroup/theGardener"},
                            {"title" : "stableBranch", "openApiType": "string", "default": "", "description": "stableBranch of the project", "example": "master"},
                            {"title" : "displayedBranches", "openApiType": "string", "default": "", "description": "branches that will be displayed", "example": "qa|master|feature.*|bugfix.*"},
                            {"title" : "featuresRootPath", "openApiType": "string", "default": "", "description": "path that lead to the feature files", "example": "test/features"},
                            {"title" : "documentationRootPath", "openApiType": "string", "default": "", "description": "path that lead to the documentation files", "example": "documentation"},
                            {"title" : "variables", "openApiType": "array of Variable", "default": "", "description": "variables defined for this project", "example": "[{\"name\":\"${swagger.url}\",\"value\":\"http://dc1-pmbo-corp-srv-pp.corp.dc1.kelkoo.net:9001/docs\"}]"},
                            {"title" : "hierarchy", "openApiType": "array of HierarchyNode", "default": "", "description": "Hierarchy matching the project", "example": ""}
            ],
            "childrenModels": [],
            "errors":[]
          }
        }
      }
    ]
  }
]
"""


  @level_2_technical_details @error_case @valid
  Scenario: generate a documentation page with the inclusion of an openApi model with server not responding
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
```thegardener
{
  "page" :
     {
        "label": "Read documentation",
        "description": "How to read documentation provided by theGardener ?"
     }
}
```
```thegardener
{
  "openApi" :
     {
        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 1
     }
}
```
"""
    And swagger.json cannot be requested
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "openApi",
        "data": {
          "openApi": {
            "modelName": "",
            "required" : [],
            "openApiRows": [],
            "childrenModels": [],
            "errors":["ERROR HTTP"]
          }
        }
      }
    ]
  }
]
"""
    And we have now those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown                                                                                                                                                                                                                                                                                                                                                                                              | directoryId | dependOnOpenApi |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n{\n  "page" :\n     {\n        "label": "Read documentation",\n        "description": "How to read documentation provided by theGardener ?"\n     }\n}\n```\n```thegardener\n{\n  "openApi" :\n     {\n        "openApiUrl": "http://theGardener.com/api/docs/swagger.json",\n        "openApiType": "model",\n        "ref": "#/definitions/Project",\n        "deep": 1\n     }\n}\n``` | 1           | true            |
