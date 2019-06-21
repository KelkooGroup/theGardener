Feature: Generate a documentation page
  As a user,
  I want generate documentation based on the criterias I provide
  So that I can have a clear view of the projects specifications

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
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |

  @level_2_technical_details @nominal_case @draft
  Scenario: generate a documentation page
    Given we have those directories in the database
      | id | name        | label         | description             | order | path          | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | 1        |
    And we have those pages in the database
      | id | name       | label       | description               | order | path                    | markdown                              | directoryId |
      | 1  | context    | The context | Why providing suggestions | 0     | /context                | **Feature**: Provide book suggestions | 1           |
      | 2  | suggestion | suggestion  | suggestion                | 0     | /suggestions/suggestion | **What's a suggestion ?**             | 2           |
      | 3  | examples   | examples    | examples                  | 1     | /suggestions/examples   | **Some suggestion examples**          | 2           |
      | 4  | admin      | admin       | admin                     | 0     | /admin/admin            | **Page for the admin users**          | 3           |

    When I perform a "GET" on following URL "/api/pages?filter=_root_suggestion>suggestionsWS>master>/context"
    Then I get the following json response body
"""
{
  "project": "suggestionsWS",
  "branch": "master",
  "directory": "/",
  "pages": [
    {
      "page": "/context",
      "name" : "context",
      "label" : "The context",
      "description": "Why providing suggestions",
      "order": "0",
      "markdown": "**Feature**: Provide book suggestions"
    }
  ]
}
"""


  @level_2_technical_details @nominal_case @draft
  Scenario: generate a documentation pages of a directory
    Given we have those directories in the database
      | id | name        | label         | description             | order | path          | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | 1        |
    And we have those pages in the database
      | id | name       | label           | description               | order | path                    | markdown                              | directoryId |
      | 1  | context    | The context     | Why providing suggestions | 0     | /context                | **Feature**: Provide book suggestions | 1           |
      | 2  | suggestion | The suggestions | The suggestions...        | 0     | /suggestions/suggestion | **What's a suggestion ?**             | 2           |
      | 3  | examples   | examples        | Some examples             | 1     | /suggestions/examples   | **Some suggestion examples**          | 2           |
      | 4  | admin      | admin           | admin                     | 0     | /admin/admin            | **Page for the admin users**          | 3           |

    When I perform a "GET" on following URL "/api/pages?filter=_root_suggestion>suggestionsWS>master>/suggestions/"
    Then I get the following json response body
"""
{
  "project": "suggestionsWS",
  "branch": "master",
  "directory": "/suggestions/",
  "pages": [
    {
      "page": "/suggestion",
      "name" : "The suggestions",
      "label" : "The context",
      "description": "The suggestions...",
      "order": "0",
      "markdown": "**What's a suggestion ?**"
    },
    {
      "page": "/suggestion",
      "name" : "examples",
      "label" : "examples",
      "description": "Some examples",
      "order": "1",
      "markdown": "**Page for the admin users**"
    }
  ]
}
"""