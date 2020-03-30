Feature: On synchronize, compute pages and store them in the cache

  Background:
    Given the database is empty
    And the cache is empty
    And the menu reloaded count is reset
    And No project is checkout
    And the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel |
      | .    | root       | Hierarchy root    | Views         | View       |
      | .01. | suggestion | Suggestion system | Projects      | Project    |
    And we have the following projects
      | id            | name                    | repositoryUrl                               | stableBranch | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetPages/library/suggestionsWS/ | master       | doc                   |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/thegardener.json"
    """
{
  "directory" :
  {
    "label": "SuggestionsWS",
    "description": "Suggestions WebServices",
    "pages": [
      "context"
    ],
    "children" :[
    ]
  }
}
    """
    And the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file           | content                               |
      | doc/context.md | **Feature**: Provide book suggestions |
    And page computation count is reset

  @level_2_technical_details @nominal_case  @documentation @valid
  Scenario: compute a page only once when the page is served several times
    And the project "suggestionsWS" is synchronized
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "context",
    "description": "context",
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
    And page computation count is reset
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then page "suggestionsWS>master>/context" hasn't been computed


  @level_2_technical_details @nominal_case  @documentation  @valid
  Scenario: compute page only when the page has changed on the remote server
    And the project "suggestionsWS" is synchronized
    And page computation count is reset
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then page "suggestionsWS>master>/context" hasn't been computed
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "context",
    "description": "context",
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
    And the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file           | content                                  |
      | doc/context.md | **Feature**: Provide book suggestions V2 |
    And the project "suggestionsWS" is synchronized
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "context",
    "description": "context",
    "order": 0,
    "content": [
      {
        "type": "markdown",
        "data": {
          "markdown": "**Feature**: Provide book suggestions V2"
        }
      }
    ]
  }
]
"""
    Then the cache store "page_suggestionsWS>master>/context" with the value
"""
PageWithContent(Page(1,context,context,context,0,Some(**Feature**: Provide book suggestions V2),/context,suggestionsWS>master>/context,1,false),List(PageFragment(markdown,PageFragmentContent(Some(**Feature**: Provide book suggestions V2),None,None,None,None))))
"""


  @level_2_technical_details @nominal_case  @documentation  @valid
  Scenario: synchronize the pages of all the projects with a scheduler - synchronize with remote repositories and compute all pages
    And the synchronization action is triggered for all projects
    Then the cache store "page_suggestionsWS>master>/context" with the value
"""
PageWithContent(Page(1,context,context,context,0,Some(**Feature**: Provide book suggestions),/context,suggestionsWS>master>/context,1,false),List(PageFragment(markdown,PageFragmentContent(Some(**Feature**: Provide book suggestions),None,None,None,None))))
"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "context",
    "description": "context",
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
