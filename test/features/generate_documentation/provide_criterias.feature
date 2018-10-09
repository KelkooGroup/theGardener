Feature: Provide criterias

  Background:
    Given the database is empty
    And the hierarchy nodes are
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
    And we have the following projects
      | id                 | name                    | repositoryUrl                                              | stableBranch | featuresRootPath |
      | suggestionsWS      | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/      | master       | test/features    |
      | suggestionsReports | Suggestions Reports     | target/remote/data/GetFeatures/library/suggestionsReports/ | master       | test/features    |
      | usersWS            | Users WebServices       | target/remote/data/GetFeatures/library/usersWS/            | master       | test/features    |
    And the links between hierarchy nodes are
      | projectId          | hierarchyId |
      | suggestionsWS      | .01.01.01.  |
      | suggestionsWS      | .02.        |
      | suggestionsReports | .01.01.01.  |
      | usersWS            | .01.01.02.  |

# TODO ADD add scenarios on route /api/criterias