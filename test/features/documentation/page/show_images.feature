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


  @level_2_technical_details @nominal_case @ready
  Scenario: access to images - using the path
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | assetsRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   | doc/assets     |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    Then the file system store the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets?path=suggestionsWS>master>/images/archi.png"
    Then I get the following response body
"""
~~ IMAGE ~~
"""

  @level_2_technical_details @nominal_case @ready
  Scenario: access to images - using the relative path - as would do a developer in its IDE from doc/api - assets is a sub directory from documentation
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | assetsRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   | doc/assets     |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    Then the file system store the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets/relative?project=suggestionsWS&branch=master&relativePath=../assets/images/archi.png"
    Then I get the following response body
"""
~~ IMAGE ~~
"""

  @level_2_technical_details @nominal_case @ready
  Scenario: access to images - using the relative path - as would do a developer in its IDE from doc/api/suggestions - assets is a sub directory from documentation
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | assetsRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   | doc/assets     |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    Then the file system store the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets/relative?project=suggestionsWS&branch=master&relativePath=../../assets/images/archi.png"
    Then I get the following response body
"""
~~ IMAGE ~~
"""


  @level_2_technical_details @nominal_case @ready
  Scenario: access to images - using the relative path - as would do a developer in its IDE from doc/api - assets is not a sub directory from documentation
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | assetsRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc/api               | doc/assets     |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    Then the file system store the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets/relative?project=suggestionsWS&branch=master&relativePath=../assets/images/archi.png"
    Then I get the following response body
"""
~~ IMAGE ~~
"""


  @level_2_technical_details @nominal_case @ready
  Scenario: access to images - using the relative path - as would do a developer in its IDE from doc/api/suggestions - assets is not a sub directory from documentation
    Given we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | assetsRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc/api               | doc/assets     |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    Then the file system store the file "target/data/git/suggestionsWS/master/doc/assets/images/archi.png"
"""
~~ IMAGE ~~
"""
    When I perform a "GET" on following URL "/api/assets/relative?project=suggestionsWS&branch=master&relativePath=../../assets/images/archi.png"
    Then I get the following response body
"""
~~ IMAGE ~~
"""


  @level_2_technical_details @nominal_case @ready
  Scenario: change url provided by the developer to access to the image through the API
    Given the application is running under "http://localhost:9000"
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | assetsRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   | doc/assets     |
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
      | 1  | suggestion | The suggestions | The suggestions... | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **Image** : ![Architecture](../assets/images/archi.png) | 2           |
    When I perform a "GET" on following URL "/api/pages?hierarchy=_root_suggestion&path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
{
  "path": "suggestionsWS>master>/context",
  "pages": [
    {
      "relativePath": "/context",
      "name" : "context",
      "label" : "The context",
      "description": "Why providing suggestions",
      "order": "0",
      "markdown": "*Image** : ![Architecture](http://localhost:9000/api/assets/relative?project=suggestionsWS&branch=master&relativePath=../assets/images/archi.png)"
    }
  ]
}
"""
