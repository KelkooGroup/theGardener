#Feature_name: Show a feature
Feature: As a user,
  I want to see my feature in theGardener
  So that my project feature is shared with all users

  @level_0_high_level @nominal_case @draft
  Scenario: show a simple feature
    Given my project is setup in theGardener
    And a simple feature is available in my project
    When a user access to this feature in theGardener
    Then this feature is displayed properly


  @level_1_specification @nominal_case @draft
  Scenario: show a simple feature with one scenario with all required tags and meta data
    Given the projects host settings
      | id        | type | root_path                  | local_copy_root_path |
      | kk-gitlab | git  | git@gitlab.corp.kelkoo.net | data/git/kk-gitlab   |
    And the project settings
      | id            | name                    | id_host   | project_path          | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | kk-gitlab | library/suggestionsWS | master        | test/features      |
    And the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
#Feature_name: Provide some book suggestions
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the following feature is displayed
      | id                                             | name                          | description                                                             | project                 |
      | suggestionsWS/provide_book_suggestions.feature | Provide some book suggestions | As a user, I want some book suggestions so that I can do some discovery | Suggestions WebServices |
    And the following scenarios are displayed
      | id | scenario                           | abstraction_level | case    | step  |
      | 1  | providing several book suggestions | level_0           | nominal | valid |
    And the scenario "1" is displayed
      | step  | type   | value                                                                          |
      | given | simple | a user                                                                         |
      | when  | simple | we ask for suggestions                                                         |
      | then  | simple | the suggestions are popular and available books adapted to the age of the user |

  @level_1_specification @nominal_case @draft
  Scenario: show a simple feature with one scenario without required tags and meta data - show default values
    Given the projects host settings
      | id        | type | root_path                  | local_copy_root_path |
      | kk-gitlab | git  | git@gitlab.corp.kelkoo.net | data/git/kk-gitlab   |
    And the project settings
      | id            | name                    | id_host   | project_path          | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | kk-gitlab | library/suggestionsWS | master        | test/features      |
    And the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the following feature is displayed
      | id                                             | name                     | description                                                             | project                 |
      | suggestionsWS/provide_book_suggestions.feature | provide_book_suggestions | As a user, I want some book suggestions so that I can do some discovery | Suggestions WebServices |
    And the following scenarios are displayed
      | id | scenario                           | abstraction_level | case    | step  |
      | 1  | providing several book suggestions | level_1           | nominal | valid |
    And the scenario "1" is displayed
      | step  | type   | value                                                                          |
      | given | simple | a user                                                                         |
      | when  | simple | we ask for suggestions                                                         |
      | then  | simple | the suggestions are popular and available books adapted to the age of the user |
