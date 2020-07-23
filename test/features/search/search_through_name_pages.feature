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
    And I perform a "GET" on following URL "/api/pages/search?keywords=context"
    Then I get the following json response body
    """
[
  {
    "hierarchy":"/Suggestion system/master/context",
    "path":"suggestionsWS>master>/context",
    "branch":"master",
    "label":"The context",
    "description":"Why providing suggestions",
    "pageContent":"**Feature**: Provide book suggestions"
   }
]

    """

  @level_2_technical_details @nominal_case @ongoing
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
    When I perform a "GET" on following URL "/api/pages/search?keywords=context"
    Then I get the following json response body
    """
[
  {
    "hierarchy":"/Suggestion system/feature/654-simple-full-text-search-on-pages/context",
    "path":"suggestionsWS>feature/654-simple-full-text-search-on-pages>/context",
    "branch":"feature/654-simple-full-text-search-on-pages",
    "label":"The context",
    "description":"Why providing suggestions",
    "pageContent":"**Feature**: Provide book suggestions context"
  },
  {
    "hierarchy":"/Suggestion system/master/context",
    "path":"suggestionsWS>master>/context",
    "branch":"master",
    "label":"The context",
    "description":"Why providing suggestions",
    "pageContent":"**Feature**: Provide book suggestions"
   }
]
"""

  @level_2_technical_details @nominal_case @ongoing
  Scenario: get result of a search with multiple results
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
    When I perform a "GET" on following URL "/api/pages/search?keywords=suggestion"
    Then I get the following json response body
    """
[
  {
    "hierarchy":"/Suggestion system/master/suggestions/suggestion",
    "path":"suggestionsWS>master>/suggestions/suggestion",
    "branch":"master",
    "label":"The suggestions",
    "description":"The suggestions...",
    "pageContent":"**What's a suggestion ?**"
  },
  {
    "hierarchy":"/Suggestion system/master/context",
    "path":"suggestionsWS>master>/context",
    "branch":"master",
    "label":"The context",
    "description":"Why providing suggestions",
    "pageContent":"**Feature**: Provide book suggestions"
  },
  {
    "hierarchy":"/Suggestion system/master/suggestions/examples",
    "path":"suggestionsWS>master>/suggestions/examples",
    "branch":"master",
    "label":"examples",
    "description":"Some examples",
    "pageContent":"**Some suggestion examples**"
  }
]
"""





