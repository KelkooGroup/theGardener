Feature: Setup a project
  As a user,
  I want to setup my project into theGardener
  So that my project features are shared with all users

  @level_0_high_level @nominal_case @draft
  Scenario: setup a project
    Given a git server that host a project
    When a user setup this project in theGardener
    Then the project sources are retrieved from the git server


  @level_1_specification @nominal_case @draft
  Scenario: setup a project
    Given no project settings are setup in theGardener
    And the root data path is "data/projects"
    And the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When a user setup a new project with
      | id            | name                    | repository_url                                       | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master        | test/features      |
    Then the projects settings are now
      | id            | name                    | repository_url                                       | stable_branch | features_root_path | local_copy_root_path        |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master        | test/features      | data/projects/suggestionsWS |
    And the file system store the file "data/projects/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

  @level_1_specification @nominal_case @draft
  Scenario: setup a project with a complex structure of feature files
    Given no project settings are setup in theGardener
    And the root data path is "data/projects"
    And the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                                                       | content                           |
      | test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |
    When a user setup a new project with
      | id            | name                    | repository_url                                       | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master        | test/features      |
    Then the projects settings are now
      | id            | name                    | repository_url                                       | stable_branch | features_root_path | local_copy_root_path        |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master        | test/features      | data/projects/suggestionsWS |
    And the file system store the files
      | files                                                                                         | content                           |
      | data/projects/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | data/projects/suggestionsWS/master/test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |


