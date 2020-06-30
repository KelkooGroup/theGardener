Feature: search through pages by name
  As a user,
  I want to be able to search a page by its name to find more easily what i need

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
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name                                         | isStable | projectId     |
      | 1  | master                                       | true     | suggestionsWS |
      | 2  | feature/654-simple-full-text-search-on-pages | false    | suggestionsWS |

  @level_2_technical_details @nominal_case @ongoing
  Scenario: get result of a search by name
    Given we have those directories in the database
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
    And we have the following document in the lucene index
      | hierarchy | path                                         | branch | label           | description               | pageContent                           |
      | projet    | suggestionsWS>master>/context                | master | The context     | Why providing suggestions | **Feature**: Provide book suggestions |
      | project   | suggestionsWS>master>/suggestions/suggestion | master | The suggestions | The suggestions...        | **What's a suggestion ?**             |
      | project   | suggestionsWS>master>/suggestions/examples   | master | examples        | Some examples             | **Some suggestion examples**          |
      | project   | suggestionsWS>master>/admin/admin            | master | admin           | admin                     | **Page for the admin users**          |
    And I perform a "GET" on following URL "/api/pages/search?keywords=context"
    Then I get the following json response body
    """
[
  {
    "id": 1,
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "markdown": "**Feature**: Provide book suggestions",
    "relativePath": "/context",
    "path": "suggestionsWS>master>/context",
    "directoryId": 1,
    "dependOnOpenApi": false
  }
]
"""

  @level_2_technical_details @ongoing
  Scenario: get result of a search by name with multiple results
    Given we have those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
      | 4  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 2        |
    And we have those pages in the database
      | id | name       | label           | description               | order | relativePath            | path                                                                | markdown                                      | directoryId |
      | 1  | context    | The context     | Why providing suggestions | 0     | /context                | suggestionsWS>master>/context                                       | **Feature**: Provide book suggestions         | 1           |
      | 2  | suggestion | The suggestions | The suggestions...        | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion                        | **What's a suggestion ?**                     | 2           |
      | 3  | examples   | examples        | Some examples             | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples                          | **Some suggestion examples**                  | 2           |
      | 4  | admin      | admin           | admin                     | 0     | /admin/admin            | suggestionsWS>master>/admin/admin                                   | **Page for the admin users**                  | 3           |
      | 5  | context    | The context     | Why providing suggestions | 0     | /context                | suggestionsWS>feature/654-simple-full-text-search-on-pages>/context | **Feature**: Provide book suggestions context | 4           |

    When I perform a "GET" on following URL "/api/pages/search?keywords=context"
    Then I get the following json response body
    """
[
  {
    "id": 5,
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "markdown": "**Feature**: Provide book suggestions context",
    "relativePath": "/context",
    "path": "suggestionsWS>feature/654-simple-full-text-search-on-pages>/context",
    "directoryId": 4,
    "dependOnOpenApi": false
  },
  {
    "id": 1,
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "markdown": "**Feature**: Provide book suggestions",
    "relativePath": "/context",
    "path": "suggestionsWS>master>/context",
    "directoryId": 1,
    "dependOnOpenApi": false
  }
]
"""

  @level_2_technical_details @ongoing
  Scenario: get result of a search by markdown
    Given we have those directories in the database
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
    When I perform a "GET" on following URL "/api/pages/search?keywords=suggestion"
    Then I get the following json response body
    """
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions"
  },
  {
    "path": "suggestionsWS>master>/suggestions/suggestion ",
    "relativePath": "/suggestions/suggestion ",
    "name": "suggestion",
    "label": "The suggestions",
    "description": "The suggestions..."
  },
  {
    "path": "suggestionsWS>master>/suggestions/examples ",
    "relativePath": "/suggestions/examples ",
    "name": "examples",
    "label": "examples",
    "description": "Some examples"
  }
]
"""





