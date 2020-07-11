Feature: Generate a documentation page with external links

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
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 1  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And we have those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown | directoryId |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | [empty]  | 1           |

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with the inclusion of the an external page
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
  "includeExternalPage" :
     {
        "url": "http://thegardener.kelkoogroup.net/api/docs/"
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
        "type": "includeExternalPage",
        "data": {
          "includeExternalPage": "http://thegardener.kelkoogroup.net/api/docs/"
        }
      }
    ]
  }
]
"""
