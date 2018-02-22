#Feature_name: Setup a hosted project
Feature: As a user,
  I want to setup my project into theGardener
  So that my project features are shared with all users


  @level_0_high_level @nominal_case @draft
  Scenario: setup a projects server
    Given a git server that host several projects
    When a user setup this git server in theGardener
    Then this user can add hosted projects of this git server in theGardener


  @level_0_high_level @nominal_case @draft
  Scenario: setup a hosted project
    Given the git server is properly setup
    When a user setup a new hosted project in theGardener
    Then the project sources are retrieved from the git server


  @level_1_specification @nominal_case @draft
  Scenario: setup a projects server
    Given no projects servers are setup
    And the root data path is "data"
    When a user setup a new projects server with
      | id        | type | root_path                       |
      | kk-gitlab | git  | git@gitlab.corp.kelkoogroup.net |
    Then the projects server settings are now
      | id        | type | root_path                       | local_copy_root_path |
      | kk-gitlab | git  | git@gitlab.corp.kelkoogroup.net | data/git/kk-gitlab   |


  @level_1_specification @nominal_case @draft
  Scenario: setup a hosted project
    Given the projects server settings
      | id        | type | root_path                       | local_copy_root_path |
      | kk-gitlab | git  | git@gitlab.corp.kelkoogroup.net | data/git/kk-gitlab   |
    And no project are setup
    And the server "kk-gitlab" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When a user setup a new hosted project in theGardener with
      | id            | name                    | id_host   | project_path          | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | kk-gitlab | library/suggestionsWS | master        | test/features      |
    Then the project settings are now
      | id            | name                    | id_host   | project_path          | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | kk-gitlab | library/suggestionsWS | master        | test/features      |
    And the file system store the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

  @level_1_specification @nominal_case @draft
  Scenario: setup a hosted project with a complex structure of feature files
    Given the projects server settings
      | id        | type | root_path                       | local_copy_root_path |
      | kk-gitlab | git  | git@gitlab.corp.kelkoogroup.net | data/git/kk-gitlab   |
    And no project are setup
    And the server "kk-gitlab" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                                                       | content                                 |
      | test/features/suggestions/provide_book_suggestions.feature | #Feature_name: Provide book suggestions |
      | test/features/setup/setup_suggestions.feature              | #Feature_name: Setup book suggestions   |
    When a user setup a new hosted project in theGardener with
      | id            | name                    | id_host   | project_path          | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | kk-gitlab | library/suggestionsWS | master        | test/features      |
    Then the project settings are now
      | id            | name                    | id_host   | project_path          | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | kk-gitlab | library/suggestionsWS | master        | test/features      |
    And the file system store the files
      | files                                                                                              | content                                 |
      | data/git/kk-gitlab/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature | #Feature_name: Provide book suggestions |
      | data/git/kk-gitlab/suggestionsWS/master/test/features/setup/setup_suggestions.feature              | #Feature_name: Setup book suggestions   |

