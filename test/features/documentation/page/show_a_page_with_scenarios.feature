Feature: Generate a documentation page with scenarios included

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
      | id                 | name                    | repositoryUrl                                              | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS      | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/      | master       | test/features    | doc                   |
      | suggestionsReports | Suggestions Reports     | target/remote/data/GetFeatures/library/suggestionsReports/ | master       | test/features    | doc                   |
    And the links between hierarchy nodes are
      | projectId          | hierarchyId |
      | suggestionsWS      | .01.        |
      | suggestionsReports | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId          |
      | 1  | master | true     | suggestionsWS      |
      | 2  | master | true     | suggestionsReports |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery
  Background:
     Given the database is empty
     And the cache is empty
     And No project is checkout

  @level_0_high_level @nominal_case @draft
  Scenario: providing several book suggestions
    Given a user
    When we ask for suggestions
    Then the suggestions are popular and available books adapted to the age of the user

  @level_1_specification @error_case @valid
  Scenario: one service on which the suggestion system depends on is down
      Given the user "Tim"
      And impossible to get information on the user
      When we ask for "3" suggestions from "2" different categories
      Then the system is temporary not available

  @level_1_specification @nominal_case @valid
  Scenario: suggestions of popular and available books adapted to the age of the user
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName    |
      | cat1       | Walt Disney     |
      | cat2       | Picture books   |
      | cat3       | Bedtime stories |
    When we ask for "3" suggestions from "2" different categories
    Then the suggestions are popular and available books adapted to the age of the user

    """
    And the project "suggestionsWS" is synchronized
    And we have those directories in the database
      | id | name | label              | description             | order | relativePath | path                        | branchId |
      | 1  | root | SuggestionsWS      | Suggestions WebServices | 0     | /            | suggestionsWS>master>/      | 1        |
      | 2  | root | suggestionsReports | Suggestions Reports     | 0     | /            | suggestionsReports>master>/ | 2        |
    And we have those pages in the database
      | id | name    | label       | description               | order | relativePath | path                               | markdown | directoryId |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context      |          | 1           |
      | 2  | context | The context | Why providing suggestions | 0     | /context     | suggestionsReports>master>/context |          | 2           |

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with all scenarios of a feature
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
**Feature**: Provide book suggestions


```thegardener
    {
      "scenarios" :
         {
            "feature": "/provide_book_suggestions.feature"
         }
    }
```

**Footer**

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
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions\n\n"
        }
      },
      {
        "type": "scenarios",
        "data": {
          "scenarios": {
            "id": 1,
            "branchId": 1,
            "path": "test/features/provide_book_suggestions.feature",
            "tags": [],
            "language": "en",
            "keyword": "Feature",
            "name": "As a user Tim, I want some book suggestions so that I can do some discovery",
            "description": "",
            "scenarios": [
              {
                "name": "suggestions of popular and available books adapted to the age of the user",
                "workflowStep": "valid",
                "description": "",
                "id": 3,
                "keyword": "Scenario",
                "abstractionLevel": "level_1_specification",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "the user \"Tim\"",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "And",
                    "text": "he is \"4\" years old",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "And",
                    "text": "the popular categories for this age are",
                    "argument": [
                      [
                        "categoryId",
                        "categoryName"
                      ],
                      [
                        "cat1",
                        "Walt Disney"
                      ],
                      [
                        "cat2",
                        "Picture books"
                      ],
                      [
                        "cat3",
                        "Bedtime stories"
                      ]
                    ]
                  },
                  {
                    "id": 3,
                    "keyword": "When",
                    "text": "we ask for \"3\" suggestions from \"2\" different categories",
                    "argument": []
                  },
                  {
                    "id": 4,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "level_1_specification",
                  "nominal_case",
                  "valid"
                ],
                "caseType": "nominal_case"
              },
              {
                "name": "providing several book suggestions",
                "workflowStep": "draft",
                "description": "",
                "id": 1,
                "keyword": "Scenario",
                "abstractionLevel": "level_0_high_level",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "a user",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "When",
                    "text": "we ask for suggestions",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "draft",
                  "level_0_high_level",
                  "nominal_case"
                ],
                "caseType": "nominal_case"
              },
              {
                "name": "one service on which the suggestion system depends on is down",
                "workflowStep": "valid",
                "description": "",
                "id": 2,
                "keyword": "Scenario",
                "abstractionLevel": "level_1_specification",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "the user \"Tim\"",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "And",
                    "text": "impossible to get information on the user",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "When",
                    "text": "we ask for \"3\" suggestions from \"2\" different categories",
                    "argument": []
                  },
                  {
                    "id": 3,
                    "keyword": "Then",
                    "text": "the system is temporary not available",
                    "argument": []
                  }
                ],
                "tags": [
                  "error_case",
                  "level_1_specification",
                  "valid"
                ],
                "caseType": "error_case"
              }
            ],
            "comments": []
          }
        }
      },
      {
        "type": "markdown",
        "data": {
          "markdown": "\n**Footer**\n"
        }
      }
    ]
  }
]
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with scenarios filtered on tags
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
**Feature**: Provide book suggestions


```thegardener
    {
      "scenarios" :
         {
            "feature": "/provide_book_suggestions.feature",
            "select": { "tags" : ["@level_0_high_level", "@nominal_case"]  }
         }
    }
```

**Footer**

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
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions\n\n"
        }
      },
      {
        "type": "scenarios",
        "data": {
          "scenarios": {
            "id": 1,
            "branchId": 1,
            "path": "test/features/provide_book_suggestions.feature",
            "tags": [],
            "language": "en",
            "keyword": "Feature",
            "name": "As a user Tim, I want some book suggestions so that I can do some discovery",
            "description": "",
            "scenarios": [
              {
                "name": "providing several book suggestions",
                "workflowStep": "draft",
                "description": "",
                "id": 1,
                "keyword": "Scenario",
                "abstractionLevel": "level_0_high_level",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "a user",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "When",
                    "text": "we ask for suggestions",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "draft",
                  "level_0_high_level",
                  "nominal_case"
                ],
                "caseType": "nominal_case"
              }
            ],
            "comments": []
          }
        }
      },
      {
        "type": "markdown",
        "data": {
          "markdown": "\n**Footer**\n"
        }
      }
    ]
  }
]
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with scenarios from another project
    Given we have the following markdown for the page "suggestionsReports>master>/context"
"""
**Feature**: Provide book suggestions


```thegardener
    {
      "scenarios" :
         {
            "project" : "suggestionsWS",
            "feature": "/provide_book_suggestions.feature",
            "select": { "tags" : ["@level_0_high_level", "@nominal_case"]  }
         }
    }
```

**Footer**

"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsReports>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsReports>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions\n\n"
        }
      },
      {
        "type": "scenarios",
        "data": {
          "scenarios": {
            "id": 1,
            "branchId": 1,
            "path": "test/features/provide_book_suggestions.feature",
            "tags": [],
            "language": "en",
            "keyword": "Feature",
            "name": "As a user Tim, I want some book suggestions so that I can do some discovery",
            "description": "",
            "scenarios": [
              {
                "name": "providing several book suggestions",
                "workflowStep": "draft",
                "description": "",
                "id": 1,
                "keyword": "Scenario",
                "abstractionLevel": "level_0_high_level",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "a user",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "When",
                    "text": "we ask for suggestions",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "draft",
                  "level_0_high_level",
                  "nominal_case"
                ],
                "caseType": "nominal_case"
              }
            ],
            "comments": []
          }
        }
      },
      {
        "type": "markdown",
        "data": {
          "markdown": "\n**Footer**\n"
        }
      }
    ]
  }
]
"""


  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with several scenarios modules
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
**Feature**: Provide book suggestions


```thegardener
    {
      "scenarios" :
         {
            "feature": "/provide_book_suggestions.feature",
            "select": { "tags" : ["@level_1_specification", "@nominal_case"]  }
         }
    }
```

**Errors :**

```thegardener
    {
      "scenarios" :
         {
            "feature": "/provide_book_suggestions.feature",
            "select": { "tags" : ["@level_1_specification", "@error_case"]  }
         }
    }
```


**Footer**

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
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions\n\n"
        }
      },
      {
        "type": "scenarios",
        "data": {
          "scenarios": {
            "id": 1,
            "branchId": 1,
            "path": "test/features/provide_book_suggestions.feature",
            "tags": [],
            "language": "en",
            "keyword": "Feature",
            "name": "As a user Tim, I want some book suggestions so that I can do some discovery",
            "description": "",
            "scenarios": [
              {
                "name": "suggestions of popular and available books adapted to the age of the user",
                "workflowStep": "valid",
                "description": "",
                "id": 3,
                "keyword": "Scenario",
                "abstractionLevel": "level_1_specification",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "the user \"Tim\"",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "And",
                    "text": "he is \"4\" years old",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "And",
                    "text": "the popular categories for this age are",
                    "argument": [
                      [
                        "categoryId",
                        "categoryName"
                      ],
                      [
                        "cat1",
                        "Walt Disney"
                      ],
                      [
                        "cat2",
                        "Picture books"
                      ],
                      [
                        "cat3",
                        "Bedtime stories"
                      ]
                    ]
                  },
                  {
                    "id": 3,
                    "keyword": "When",
                    "text": "we ask for \"3\" suggestions from \"2\" different categories",
                    "argument": []
                  },
                  {
                    "id": 4,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "level_1_specification",
                  "nominal_case",
                  "valid"
                ],
                "caseType": "nominal_case"
              }
            ],
            "comments": []
          }
        }
      },
      {
        "type": "markdown",
        "data": {
          "markdown": "\n**Errors :**\n"
        }
      },
      {
        "type": "scenarios",
        "data": {
          "scenarios": {
            "id": 1,
            "branchId": 1,
            "path": "test/features/provide_book_suggestions.feature",
            "tags": [],
            "language": "en",
            "keyword": "Feature",
            "name": "As a user Tim, I want some book suggestions so that I can do some discovery",
            "description": "",
            "scenarios": [
              {
                "name": "one service on which the suggestion system depends on is down",
                "workflowStep": "valid",
                "description": "",
                "id": 2,
                "keyword": "Scenario",
                "abstractionLevel": "level_1_specification",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "the user \"Tim\"",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "And",
                    "text": "impossible to get information on the user",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "When",
                    "text": "we ask for \"3\" suggestions from \"2\" different categories",
                    "argument": []
                  },
                  {
                    "id": 3,
                    "keyword": "Then",
                    "text": "the system is temporary not available",
                    "argument": []
                  }
                ],
                "tags": [
                  "error_case",
                  "level_1_specification",
                  "valid"
                ],
                "caseType": "error_case"
              }
            ],
            "comments": []
          }
        }
      },
      {
        "type": "markdown",
        "data": {
          "markdown": "\n\n**Footer**\n"
        }
      }
    ]
  }
]
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with scenarios filtered on tags with background
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
**Feature**: Provide book suggestions


```thegardener
    {
      "scenarios" :
         {
            "feature": "/provide_book_suggestions.feature",
            "select": { "tags" : ["@level_0_high_level", "@nominal_case"]  },
            "includeBackground": true
         }
    }
```

**Footer**

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
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions\n\n"
        }
      },
      {
        "type": "scenarios",
        "data": {
          "scenarios": {
            "id": 1,
            "branchId": 1,
            "path": "test/features/provide_book_suggestions.feature",
            "background":{
              "id":0,
              "keyword":"Background",
              "name":"",
              "description":"",
              "steps":[
                  {
                    "id":0,
                    "keyword":"Given",
                    "text":"the database is empty",
                    "argument":[]
                  },
                  {
                    "id":1,
                    "keyword":"And",
                    "text":"the cache is empty",
                    "argument":[]
                  },
                  {
                    "id":2,
                    "keyword":"And",
                    "text":"No project is checkout",
                    "argument":[]
                  }
              ]
            },
            "tags": [],
            "language": "en",
            "keyword": "Feature",
            "name": "As a user Tim, I want some book suggestions so that I can do some discovery",
            "description": "",
            "scenarios": [
              {
                "name": "providing several book suggestions",
                "workflowStep": "draft",
                "description": "",
                "id": 1,
                "keyword": "Scenario",
                "abstractionLevel": "level_0_high_level",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "a user",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "When",
                    "text": "we ask for suggestions",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "draft",
                  "level_0_high_level",
                  "nominal_case"
                ],
                "caseType": "nominal_case"
              }
            ],
            "comments": []
          }
        }
      },
      {
        "type": "markdown",
        "data": {
          "markdown": "\n**Footer**\n"
        }
      }
    ]
  }
]
"""

  @level_2_technical_details @limit_case @valid
  Scenario: generate a documentation page with markdown escape on scenarios module
    Given we have the following markdown for the page "suggestionsWS>master>/context"
"""
**Feature**: Provide book suggestions

````
```thegardener
    {
      "scenarios" :
         {
            "feature": "/provide_book_suggestions.feature"
         }
    }
```
````

**Footer**

"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following response body
"""
[{"path":"suggestionsWS>master>/context","relativePath":"/context","name":"context","label":"The context","description":"Why providing suggestions","order":0,"content":[{"type":"markdown","data":{"markdown":"**Feature**: Provide book suggestions\n\n````\n```thegardener\n    {\n      \"scenarios\" :\n         {\n            \"feature\": \"/provide_book_suggestions.feature\"\n         }\n    }\n```\n````\n\n**Footer**\n"}}]}]
"""
