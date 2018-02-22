#Feature_name: Show a feature
Feature: As a user,
  I want to see my feature in theGardener
  So that my project feature is shared with all users

  Background:
    Given the projects host settings
      | id        | type | root_path                       | local_copy_root_path |
      | kk-gitlab | git  | git@gitlab.corp.kelkoogroup.net | data/git/kk-gitlab   |
    And the project settings
      | id            | name                    | id_host   | project_path          | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | kk-gitlab | library/suggestionsWS | master        | test/features      |

  @level_0_high_level @nominal_case @draft
  Scenario: show a simple feature
    Given my project is setup in theGardener
    And a simple feature is available in my project
    When a user access to this feature in theGardener
    Then this feature is displayed properly


  @level_1_specification @nominal_case @draft
  Scenario: show a simple feature with one scenario with all required tags and meta data
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
      | id | scenario                           | abstraction_level | case_type    | workflow_step |
      | 1  | providing several book suggestions | level_0           | nominal_case | valid         |
    And the scenario "1" is displayed
      | step  | type   | value                                                                          |
      | given | simple | a user                                                                         |
      | when  | simple | we ask for suggestions                                                         |
      | then  | simple | the suggestions are popular and available books adapted to the age of the user |

  @level_1_specification @nominal_case @draft
  Scenario Outline: show the different possible values of the annotation considered by theGardener
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

<annotation1> <annotation2> <annotation3>
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the following scenarios are displayed
      | id | abstraction_level              | case_type              | workflow_step              |
      | 1  | <considered_abstraction_level> | <considered_case_type> | <considered_workflow_step> |

    Examples:
      | annotation1                | annotation2 | annotation3 | considered_abstraction_level | considered_case_type | considered_workflow_step |
      |                            |             |             | level_1                      | nominal_case         | valid                    |
      | @level_0_high_level        |             |             | level_0                      | nominal_case         | valid                    |
      | @level_1_specification     |             |             | level_1                      | nominal_case         | valid                    |
      | @level_2_technical_details |             |             | level_2                      | nominal_case         | valid                    |
      | @nominal_case              |             |             | level_1                      | nominal_case         | valid                    |
      | @limit_case                |             |             | level_1                      | limit_case           | valid                    |
      | @error_case                |             |             | level_1                      | error_case           | valid                    |
      | @draft                     |             |             | level_1                      | nominal_case         | draft                    |
      | @ready                     |             |             | level_1                      | nominal_case         | ready                    |
      | @ongoing                   |             |             | level_1                      | nominal_case         | ongoing                  |
      | @valid                     |             |             | level_1                      | nominal_case         | valid                    |
      | @level_0_high_level        | @draft      |             | level_0                      | nominal_case         | draft                    |
      | @level_0_high_level        | @limit_case |             | level_1                      | limit_case           | valid                    |
      | @level_0_high_level        | @limit_case | @ongoing    | level_1                      | limit_case           | ongoing                  |

  @level_1_specification @limit_case @draft
  Scenario Outline: show the different possible values of the annotation considered by theGardener
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

<annotation1>
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the following scenarios are displayed
      | id | abstraction_level              | case_type              | workflow_step              |
      | 1  | <considered_abstraction_level> | <considered_case_type> | <considered_workflow_step> |

    Examples:
      | annotation1        | considered_abstraction_level | considered_case_type | considered_workflow_step |
      | @level_0           | level_0                      | nominal_case         | valid                    |
      | @level_1           | level_1                      | nominal_case         | valid                    |
      | @level_2           | level_2                      | nominal_case         | valid                    |
      | @level_0_what_ever | level_0                      | nominal_case         | valid                    |
      | @level_1_what_ever | level_1                      | nominal_case         | valid                    |
      | @level_2_what_ever | level_2                      | nominal_case         | valid                    |
      | @Level_0_what_ever | level_0                      | nominal_case         | valid                    |
      | @level1_what_ever  | level_1                      | nominal_case         | valid                    |
      | @l0                | level_0                      | nominal_case         | valid                    |
      | @level1            | level_1                      | nominal_case         | valid                    |
      | @Level2            | level_2                      | nominal_case         | valid                    |
      | @l0_what_ever      | level_0                      | nominal_case         | valid                    |
      | @l1_what_ever      | level_1                      | nominal_case         | valid                    |
      | @l2_what_ever      | level_2                      | nominal_case         | valid                    |
      | @nominal           | level_1                      | nominal_case         | valid                    |
      | @limit             | level_1                      | limit_case           | valid                    |
      | @error             | level_1                      | error_case           | valid                    |
      | @draft             | level_1                      | nominal_case         | draft                    |
      | @ready             | level_1                      | nominal_case         | ready                    |
      | @ongoing           | level_1                      | nominal_case         | ongoing                  |
      | @valid             | level_1                      | nominal_case         | valid                    |
      | @Nominal           | level_1                      | nominal_case         | valid                    |
      | @Draft             | level_1                      | nominal_case         | draft                    |

  @level_1_specification @nominal_case @draft
  Scenario Outline: show a simple feature with one scenario - show the default name of the scenario
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/<file_name>"
    """
<first_line>
Feature: As a user, I want some book suggestions so that I can do some discovery

Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the following feature is displayed
      | id                                             | name                       |
      | suggestionsWS/provide_book_suggestions.feature | <considered_scenario_name> |


    Examples:
      | first_line                                   | file_name                        | considered_scenario_name      |
      |                                              | provide_book_suggestions.feature | Provide book suggestions      |
      |                                              | provideBookSuggestions.feature   | Provide book suggestions      |
      | #Feature_name: Provide some book suggestions | provide_book_suggestions.feature | Provide some book suggestions |

  @level_1_specification @limit_case @draft
  Scenario Outline: show a simple feature with one scenario - show the default name of the scenario
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/<file_name>"
    """
<first_line>
Feature: As a user, I want some book suggestions so that I can do some discovery

Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the following feature is displayed
      | id                                             | name                       |
      | suggestionsWS/provide_book_suggestions.feature | <considered_scenario_name> |


    Examples:
      | first_line                                   | file_name                        | considered_scenario_name      |
      | #Feature_name: Provide some book suggestions | provide_book_suggestions.feature | Provide some book suggestions |
      | #feature_name: Provide some book suggestions | provide_book_suggestions.feature | Provide book suggestions      |
      | #featureName: Provide some book suggestions  | provide_book_suggestions.feature | Provide book suggestions      |

  @level_1_specification @error_case @draft
  Scenario: try to show a feature based on an incorrect feature file
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

Scenar: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then no feature is display
