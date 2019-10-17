Feature: Generate a documentation page using some variables

  Background:
    Given the database is empty
    And the cache is empty
    And No project is checkout
    And the remote projects are empty


  @level_2_technical_details @nominal_case @valid
  Scenario: replace content of the page by variables
    Given the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel |
      | .    | root       | Hierarchy root    | Views         | View       |
      | .01. | suggestion | Suggestion system | Projects      | Project    |
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | variables                                                                                                             |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | prod         | test/features    | doc                   | [{"name":"${swagger.json}","value":"http://localhost:9000/docs/swagger.json"},{"name":"${desc}","value":"Variables"}] |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have those directories in the database
      | id | name        | label       | description    | order | relativePath  | path                               | branchId |
      | 1  | suggestions | Suggestions | Suggestions... | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
    And we have those pages in the database
      | id | name       | label           | description        | order | relativePath            | path                                         | markdown                                                                              | directoryId |
      | 1  | suggestion | The suggestions | The suggestions... | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **${desc}**: ${swagger.json}, ${project.current}, ${branch.current}, ${branch.stable} | 1           |
    When I perform a "GET" on following URL "/api/pages?hierarchy=_root_suggestion&path=suggestionsWS>master>/suggestions/suggestion"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/suggestions/suggestion",
    "relativePath": "/suggestions/suggestion",
    "name" : "suggestion",
    "label" : "The suggestions",
    "description": "The suggestions...",
    "order": 0,
    "content": [
      {
        "type": "markdown",
        "data": {
          "markdown": "**Variables**: http://localhost:9000/docs/swagger.json, Suggestions WebServices, master, prod"
        }
      }
    ]
  }
]
"""
