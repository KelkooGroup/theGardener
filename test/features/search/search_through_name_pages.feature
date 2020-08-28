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

  @level_2_technical_details @nominal_case @valid
  Scenario: get result of a search
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
    And the lucene index is loaded from the database
    And I perform a "GET" on following URL "/api/pages/search?keyword=context"
    Then I get a response with status "200"
    Then I get the following json response body
    """
{
  "items": [
    {
      "page": {
        "id": "/suggestion/suggestionsWS>master>/context",
        "hierarchy": "/suggestion",
        "path": "suggestionsWS>master>/context",
        "breadcrumb": "Suggestion system / Suggestions WebServices / The context",
        "project": "Suggestions WebServices",
        "branch": "master",
        "label": "The context",
        "description": "Why providing suggestions",
        "pageContent": "**Feature**: Provide book suggestions"
      },
      "highlights": [
        {
          "fragment": "Suggestion system / Suggestions WebServices / The <em>context</em>",
          "word": "context"
        },
        {
          "fragment": "The <em>context</em>",
          "word": "context"
        }
      ]
    }
  ]
}
    """

  @level_2_technical_details @nominal_case @valid
  Scenario: get result of a search with multiple results
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
    And the lucene index is loaded from the database
    When I perform a "GET" on following URL "/api/pages/search?keyword=suggestions"
    Then I get a response with status "200"
    Then I get the following json response body
    """
{
  "items": [
    {
      "page": {
        "id": "/suggestion/suggestionsWS>master>/suggestions/suggestion",
        "hierarchy": "/suggestion",
        "path": "suggestionsWS>master>/suggestions/suggestion",
        "breadcrumb": "Suggestion system / Suggestions WebServices / suggestions / The suggestions",
        "project": "Suggestions WebServices",
        "branch": "master",
        "label": "The suggestions",
        "description": "The suggestions...",
        "pageContent": "**What's a suggestion ?**"
      },
      "highlights": [
        {
          "fragment": "Suggestion system / <em>Suggestions</em> WebServices / <em>suggestions</em> / The <em>suggestions</em>",
          "word": "Suggestions"
        },
        {
          "fragment": "The <em>suggestions</em>",
          "word": "suggestions"
        },
        {
          "fragment": "The <em>suggestions</em>...",
          "word": "suggestions"
        }
      ]
    },
    {
      "page": {
        "id": "/suggestion/suggestionsWS>master>/context",
        "hierarchy": "/suggestion",
        "path": "suggestionsWS>master>/context",
        "breadcrumb": "Suggestion system / Suggestions WebServices / The context",
        "project": "Suggestions WebServices",
        "branch": "master",
        "label": "The context",
        "description": "Why providing suggestions",
        "pageContent": "**Feature**: Provide book suggestions"
      },
      "highlights": [
        {
          "fragment": "Suggestion system / <em>Suggestions</em> WebServices / The context",
          "word": "Suggestions"
        },
        {
          "fragment": "Why providing <em>suggestions</em>",
          "word": "suggestions"
        },
        {
          "fragment": "**Feature**: Provide book <em>suggestions</em>",
          "word": "suggestions"
        }
      ]
    },
    {
      "page": {
        "id": "/suggestion/suggestionsWS>master>/suggestions/examples",
        "hierarchy": "/suggestion",
        "path": "suggestionsWS>master>/suggestions/examples",
        "breadcrumb": "Suggestion system / Suggestions WebServices / suggestions / examples",
        "project": "Suggestions WebServices",
        "branch": "master",
        "label": "examples",
        "description": "Some examples",
        "pageContent": "**Some suggestion examples**"
      },
      "highlights": [
        {
          "fragment": "Suggestion system / <em>Suggestions</em> WebServices / <em>suggestions</em> / examples",
          "word": "Suggestions"
        }
      ]
    },
    {
      "page": {
        "id": "/suggestion/suggestionsWS>master>/admin/admin",
        "hierarchy": "/suggestion",
        "path": "suggestionsWS>master>/admin/admin",
        "breadcrumb": "Suggestion system / Suggestions WebServices / admin / admin",
        "project": "Suggestions WebServices",
        "branch": "master",
        "label": "admin",
        "description": "admin",
        "pageContent": "**Page for the admin users**"
      },
      "highlights": [
        {
          "fragment": "Suggestion system / <em>Suggestions</em> WebServices / admin / admin",
          "word": "Suggestions"
        }
      ]
    }
  ]
}
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: only pages from stable branches are indexed in the search
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
    And the lucene index is loaded from the database
    When I perform a "GET" on following URL "/api/pages/search?keyword=context"
    Then I get a response with status "200"
    Then I get the following json response body
    """
{
  "items": [
    {
      "page": {
        "id": "/suggestion/suggestionsWS>master>/context",
        "hierarchy": "/suggestion",
        "path": "suggestionsWS>master>/context",
        "breadcrumb": "Suggestion system / Suggestions WebServices / The context",
        "project": "Suggestions WebServices",
        "branch": "master",
        "label": "The context",
        "description": "Why providing suggestions",
        "pageContent": "**Feature**: Provide book suggestions"
      },
      "highlights": [
        {
          "fragment": "Suggestion system / Suggestions WebServices / The <em>context</em>",
          "word": "context"
        },
        {
          "fragment": "The <em>context</em>",
          "word": "context"
        }
      ]
    }
  ]
}

"""



