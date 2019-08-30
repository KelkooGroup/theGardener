Feature: Include images in the documentation
  As a user,
  I want to include images in the documentation
  So that the end user can have a better documentation

  Background:
    Given the database is empty
    And the cache is empty
    And No project is checkout
    And the remote projects are empty
    And the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel |
      | .    | root       | Hierarchy root    | Views         | View       |
      | .01. | suggestion | Suggestion system | Projects      | Project    |


  @level_2_technical_details @nominal_case @valid
  Scenario: access to images - using the path
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets?path=suggestionsWS>master>/assets/images/archi.png"
    Then I get a response with status "200"
    And  I get the following response body
"""
~~ IMAGE ~~
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: access to images - using the relative path - as would do a developer in its IDE from doc/api - assets is a sub directory from documentation
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And the file "target/data/git/suggestionsWS/master/doc/api/archi.md"
"""
"""
    And the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets?path=suggestionsWS>master>/api/../assets/images/archi.png"
    Then I get a response with status "200"
    And  I get the following response body
"""
~~ IMAGE ~~
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: access to images - using the relative path - as would do a developer in its IDE from doc/api/suggestions - assets is a sub directory from documentation
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And the file "target/data/git/suggestionsWS/master/doc/api/suggestionsWS/archi.md"
"""
"""
    And the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets?path=suggestionsWS>master>/api/suggestionsWS/../../assets/images/archi.png"
    Then I get a response with status "200"
    And  I get the following response body
"""
~~ IMAGE ~~
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: change url provided by the developer to access to the image through the API - Inline-style
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   |
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
      | id | name       | label           | description        | order | relativePath            | path                                         | markdown                                                | directoryId |
      | 1  | suggestion | The suggestions | The suggestions... | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **Image** : ![Architecture](../assets/images/archi.png) | 1           |
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/suggestions/suggestion"
    Then I get a response with status "200"
    And  I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/suggestions/suggestion",
    "relativePath": "/suggestions/suggestion",
    "name": "suggestion",
    "label": "The suggestions",
    "description": "The suggestions...",
    "order": 0,
    "markdown": "**Image** : ![Architecture](http://localhost:9000/api/assets?path=suggestionsWS>master>/suggestions/../assets/images/archi.png)"
  }
]
"""


  @level_2_technical_details @nominal_case @valid
  Scenario: change url provided by the developer to access to the image through the API - Reference-style
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   |
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
      | id | name       | label           | description        | order | relativePath            | path                                         | markdown                                                | directoryId |
      | 1  | suggestion | The suggestions | The suggestions... | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **Image** : ![Architecture][archi]\n[archi]: ../assets/images/archi.png | 1           |
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/suggestions/suggestion"
    Then I get a response with status "200"
    And  I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/suggestions/suggestion",
    "relativePath": "/suggestions/suggestion",
    "name": "suggestion",
    "label": "The suggestions",
    "description": "The suggestions...",
    "order": 0,
    "markdown": "**Image** : ![Architecture][archi]\n[archi]: http://localhost:9000/api/assets?path=suggestionsWS>master>/suggestions/../assets/images/archi.png"
  }
]
"""

