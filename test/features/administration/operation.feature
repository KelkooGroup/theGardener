Feature: Provide some tools to operate the application when something is not working properly

  Background:
    Given No project is checkout
    And the database is empty
    And the cache is empty
    And the menu reloaded count is reset
    And the pages computation from the database count is reset
    And the server "target/remote/data/Operation" host under the project "library/suggestionsWS" on the branch "master" the file "doc/thegardener.json"
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
    And the server "target/remote/data/Operation/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file           | content                               |
      | doc/context.md | **Feature**: Provide book suggestions |
    Then the file "target/data/git/suggestionsWS/master/doc/thegardener.json"
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
    Then the file "target/data/git/suggestionsWS/master/doc/context.md"
"""
**Feature**: Provide book suggestions LOCAL DIFF
"""
    Given  we have the following projects
      | id            | name                    | repositoryUrl                                       | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/Operation/library/suggestionsWS/ | master       | test/features    | doc                   |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 1  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And we have those pages in the database
      | id | name    | label   | description | order | relativePath | path                          | markdown                                      | directoryId |
      | 1  | context | context | context     | 0     | /context     | suggestionsWS>master>/context | **Feature**: Provide book suggestions DB DIFF | 1           |


  @level_2_technical_details @nominal_case @valid @data_refresh @refresh_menu
  Scenario: force refresh the menu
    When I perform a "POST" on following URL "/api/admin/menu/refreshFromDatabase"
    Then I get a response with status "200"
    And the menu has been reloaded

  @level_2_technical_details @nominal_case @valid @data_refresh @refresh_project_from_db
  Scenario: force a project refresh from the database
    Given the pages computation from the database count is reset
    When I perform a "POST" on following URL "/api/admin/projects/suggestionsWS/refreshFromDatabase"
    Then I get a response with status "200"
    And  I get the following response body
"""
{"message":"Branches refreshed from the database linked to project suggestionsWS are","elements":["master"]}
"""
    And the menu has been reloaded
    And the pages has been recomputed from the database for the project "suggestionsWS"

  @level_2_technical_details @nominal_case @valid @data_refresh @refresh_project_from_disk
  Scenario: force a project refresh from the disk
    When I perform a "POST" on following URL "/api/admin/projects/suggestionsWS/refreshFromDisk"
    Then I get a response with status "200"
    And  I get the following response body
"""
{"message":"Branches refreshed from the disk linked to project suggestionsWS are","elements":["master"]}
"""
    And the menu has been reloaded
    And we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 2  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And we have now those pages in the database
      | id | name    | label   | description | order | relativePath | path                          | markdown                                         | directoryId |
      | 2  | context | context | context     | 0     | /context     | suggestionsWS>master>/context | **Feature**: Provide book suggestions LOCAL DIFF | 2           |


  @level_1_specifications @nominal_case @valid @data_refresh
  Scenario: force all projects refresh from the disk
    When I perform a "POST" on following URL "/api/admin/projects/refreshFromDisk"
    Then I get a response with status "200"
    And the menu has been reloaded
    And  I get the following response body
"""
{"message":"Projects refreshed from the disk are","elements":["suggestionsWS"]}
"""

  @level_2_technical_details @nominal_case @valid @data_refresh @synchronize_project_from_git
  Scenario: force a project to synchronize from the remote git repository
    When I perform a "POST" on following URL "/api/admin/projects/suggestionsWS/synchronizeFromRemoteGitRepository"
    Then I get a response with status "200"
    And  I get the following response body
"""
{"message":"Branches synchronized from the remote git repository linked to project suggestionsWS are","elements":["master"]}
"""

  @level_2_technical_details @nominal_case @valid @data_refresh @refresh_project_from_git
  Scenario: force a project refresh from the remote git repository
    When I perform a "POST" on following URL "/api/admin/projects/suggestionsWS/refreshFromRemoteGitRepository"
    Then I get a response with status "200"
    And  I get the following response body
"""
{"message":"Branches refreshed from the remote git repository linked to project suggestionsWS are","elements":["master"]}
"""
    And the menu has been reloaded
    And we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 2  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 2  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 2        |
    And we have now those pages in the database
      | id | name    | label   | description | order | relativePath | path                          | markdown                              | directoryId |
      | 2  | context | context | context     | 0     | /context     | suggestionsWS>master>/context | **Feature**: Provide book suggestions | 2           |


