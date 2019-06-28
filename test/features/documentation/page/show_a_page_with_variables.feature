Feature: Generate a documentation page using some variables

  Background:
    Given the database is empty
    And the cache is empty
    And No project is checkout
    And the remote projects are empty


  @level_2_technical_details @nominal_case @ready
  Scenario: replace content of the page by variables
    Given the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel |
      | .    | root       | Hierarchy root    | Views         | View       |
      | .01. | suggestion | Suggestion system | Projects      | Project    |
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | variables                                                                               | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | [{"${swagger.json}":"http://localhost:9000/docs/swagger.json"},{"${desc}":"Variables"}] | prod         | test/features    | doc                   |
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
      | 1  | suggestion | The suggestions | The suggestions... | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **${desc}**: ${swagger.json}, ${current.project}, ${current.branch}, ${stable.branch} | 2           |
    When I perform a "GET" on following URL "/api/pages?hierarchy=_root_suggestion&path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
{
  "page":
    {
      "path": "suggestionsWS>master>/context",
      "relativePath": "/context",
      "name" : "context",
      "label" : "The context",
      "description": "Why providing suggestions",
      "order": "0",
      "markdown": "**Variables**: http://localhost:9000/docs/swagger.json, suggestionsWS, master, prod"
    }
}
"""

