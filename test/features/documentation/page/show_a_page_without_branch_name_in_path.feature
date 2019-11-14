Feature: Generate a documentation page without branch in path

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
      | id            | name                    | repositoryUrl                                         | stableBranch | displayedBranches | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | master            | test/features    | doc                   |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |


  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page without branch in path
    Given we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
    And we have those pages in the database
      | id | name       | label           | description               | order | relativePath            | path                                         | markdown                              | directoryId |
      | 1  | context    | The context     | Why providing suggestions | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 1           |
      | 2  | suggestion | The suggestions | The suggestions...        | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 2           |
      | 3  | examples   | examples        | Some examples             | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 2           |
      | 4  | admin      | admin           | admin                     | 0     | /admin/admin            | suggestionsWS>master>/admin/admin            | **Page for the admin users**          | 3           |
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions"
        }
      }
    ]
  }
]
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page without branch in path
    Given we have those branches in the database
      | id | name       | isStable | projectId     |
      | 1  | master     | true     | suggestionsWS |
      | 2  | bugfix/351 | false    | suggestionsWS |
    And we have those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 1  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And we have those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown                                                                                                                                        | directoryId |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n { "page" : { "label" : "The context", "description": "Why providing suggestions"}}\n```\n**Feature**: Provide book suggestions | 1           |
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions"
        }
      }
    ]
  }
]
"""
