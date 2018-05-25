Feature: Register a project
  As a user,
  I want to register my project into theGardener
  So that my project BDD features will be shared with all users

  @level_0_high_level @nominal_case @draft
  Scenario: register a project
    Given a git server that host a project
    When a user register this project in theGardener
    Then those project settings are stored in theGardener system


  @level_1_specification @nominal_case @draft
  Scenario: setup a project
    Given no project settings are setup in theGardener
    And the root data path is "data/projects"
    When a user register a new project with
      | id            | name                    | repository_url                                       | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master        | test/features      |
    Then the projects settings are now
      | id            | name                    | repository_url                                       | stable_branch | features_root_path | local_copy_root_path        |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master        | test/features      | data/projects/suggestionsWS |


# The implementation of the when can be a post on a resource with some JSON content
# The implementation of the then can be a table in the data table. We can use anorm lib as we have enough knowledge in different projects


