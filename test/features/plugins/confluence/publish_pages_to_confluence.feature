Feature: Publish pages to confluence

  This is integration test for now plugged to the DEV CONFLUENCE

  Background:
    Given the database is empty
    And the cache is empty
    And No project is checkout
    And the remote projects are empty
    And the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel | directoryPath       |
      | .    | root       | Hierarchy root    | Views         | View       |                     |
      | .01. | suggestion | Suggestion system | Projects      | Project    |                     |
      | .02. | lib        | Library           | Projects      | Project    | libraryDoc>master>/ |
    And we have the following projects
      | id            | name                                | repositoryUrl                                         | stableBranch | featuresRootPath | documentationRootPath | confluenceParentPageId |
      | suggestionsWS | Suggestions WebServices             | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    | doc                   | 109379952              |
      | libraryDoc    | library  system group documentation | target/remote/data/GetFeatures/library/libraryDoc/    | master       | test/features    | doc                   |                        |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name                                         | isStable | projectId     |
      | 1  | master                                       | true     | suggestionsWS |
      | 2  | feature/654-simple-full-text-search-on-pages | false    | suggestionsWS |
      | 3  | master                                       | true     | libraryDoc    |

  @level_2_technical_details @nominal_case @integration
  Scenario: publish pages to confluence
    Given we have those directories in the database
      | id | name        | label         | description             | order | relativePath           | path                                        | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /                      | suggestionsWS>master>/                      | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/          | suggestionsWS>master>/suggestions/          | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/                | suggestionsWS>master>/admin/                | 1        |
      | 4  | users       | Users         | User...                 | 0     | /admin/users/          | suggestionsWS>master>/admin/users/          | 1        |
      | 5  | profiles    | Profiles      | Profiles...             | 0     | /admin/users/profiles/ | suggestionsWS>master>/admin/users/profiles/ | 1        |
      | 6  | avatars     | Avatars       | Avatars...              | 0     | /admin/users/avatars/  | suggestionsWS>master>/admin/users/avatars/  | 1        |
      | 7  | rules       | Rules         | Rules...                | 1     | /admin/rules/          | suggestionsWS>master>/admin/rules/          | 1        |
    And we have those pages in the database
      | id | name       | label           | description                | order | relativePath                 | path                                              | markdown                                     | directoryId |
      | 1  | context    | The context     | Why providing suggestions  | 0     | /context                     | suggestionsWS>master>/context                     | **Feature**: Provide book suggestions UP101  | 1           |
      | 2  | context2   | The context2    | Why providing suggestions2 | 0     | /context2                    | suggestionsWS>master>/context2                    | **Feature**: Provide book suggestions UPDATE FLORIAN | 1           |
      | 3  | suggestion | The suggestions | The suggestions...         | 0     | /suggestions/suggestion      | suggestionsWS>master>/suggestions/suggestion      | **What's a suggestion ?**                    | 2           |
      | 4  | examples   | examples        | Some examples              | 1     | /suggestions/examples        | suggestionsWS>master>/suggestions/examples        | **Some suggestion examples**                 | 2           |
      | 5  | admin      | admin           | Administration             | 0     | /admin/admin                 | suggestionsWS>master>/admin/admin                 | **Page for the admin users UPDATE 454**      | 3           |
      | 6  | avatars    | avatars         | Avatars                    | 0     | /admin/users/avatars/avatars | suggestionsWS>master>/admin/users/avatars/avatars | **Page for the avatars** UPDATED 4           | 6           |
    And I perform a "POST" on following URL "/api/plugin/confluence/projects/suggestionsWS/refreshConfluence"
    Then I get a response with status "200"
    Then I get the following json response body
        """
        {
            "message":"Project suggestionsWS has been refreshed"
        }
        """

  @level_2_technical_details @nominal_case @integration
  Scenario: publish pages to confluence, unknown project
    Given we have those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 1  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And I perform a "POST" on following URL "/api/plugin/confluence/projects/unknown/refreshConfluence"
    Then I get a response with status "404"
    Then I get the following json response body
        """
        {
          "message":"Project unknown not found"
        }
        """


  @level_2_technical_details @nominal_case @integration
  Scenario: publish pages to confluence, project not setup
    Given we have those directories in the database
      | id | name | label      | description     | order | relativePath | path                | branchId |
      | 1  | root | libraryDoc | Lib WebServices | 0     | /            | libraryDoc>master>/ | 3        |
    And I perform a "POST" on following URL "/api/plugin/confluence/projects/libraryDoc/refreshConfluence"
    Then I get a response with status "500"
    Then I get the following json response body
        """
        {
          "message":"Project libraryDoc has no confluence page id"
        }
        """