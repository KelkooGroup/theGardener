Feature: Show a feature
  As a user,
  I want to see my feature in theGardener
  So that my project feature is shared with all users

  Background:
    Given the projects server settings
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
  Scenario: show a feature with one simple scenario with all required tags and meta data
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: Provide some book suggestions
  As a user,
  I want some book suggestions
  So that I can do some discovery

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
      | id | scenario                           | scenario_type | abstraction_level | case_type    | workflow_step |
      | 0  | providing several book suggestions | scenario      | level_0           | nominal_case | valid         |
    And the scenario "0" is displayed
      | id | step  | type   | value                                                                          |
      | 0  | given | simple | a user                                                                         |
      | 1  | when  | simple | we ask for suggestions                                                         |
      | 2  | then  | simple | the suggestions are popular and available books adapted to the age of the user |

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
      | 0  | <considered_abstraction_level> | <considered_case_type> | <considered_workflow_step> |

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
      | 0  | <considered_abstraction_level> | <considered_case_type> | <considered_workflow_step> |

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

  @level_1_specification @nominal_case @draft
  Scenario: show a feature with one scenario with some parameters
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_1_specification @error_case @valid
Scenario: one service on which the suggestion system depends on is down
    Given the user "Tim"
    And impossible to get information on the user
    When we ask for "3" suggestions from "2" different categories
    Then the system is temporary not available

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the scenario "0" is displayed
      | id | step  | type   | value                                                                | parameters          |
      | 0  | given | simple | the user <param_0>                                                   | param_0:Tim         |
      | 1  | given | simple | impossible to get information on the user                            |                     |
      | 2  | when  | simple | we ask for <param_0> suggestions from <param_1> different categories | param_0:3,param_1:2 |
      | 3  | then  | simple | the system is temporary not available                                |                     |

  @level_1_specification @nominal_case @draft
  Scenario: show a feature with one scenario with a multi lines step
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

  Scenario: suggestions of popular and available books adapted to the age of the user
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName    |
      | cat1       | Walt Disney     |
      | cat2       | Picture books   |
      | cat3       | Bedtime stories |
    When we ask for "3" suggestions from "2" different categories
    Then the suggestions are popular and available books adapted to the age of the user

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the scenario "0" is displayed
      | id | step  | type       | value                                                                          | parameters          |
      | 0  | given | simple     | the user <param_0>                                                             | param_0:Tim         |
      | 1  | given | simple     | he is <param_0> years old                                                      | param_0:4           |
      | 2  | given | multilines | the popular categories for this age are                                        |                     |
      | 3  | when  | simple     | we ask for <param_0> suggestions from <param_1> different categories           | param_0:3,param_1:2 |
      | 4  | then  | simple     | the suggestions are popular and available books adapted to the age of the user |                     |
    Then the scenario "0" is displayed with the multi lines step "2"
      | column0    | column1         |
      | categoryId | categoryName    |
      | cat1       | Walt Disney     |
      | cat2       | Picture books   |
      | cat3       | Bedtime stories |

  @level_1_specification @nominal_case @draft
  Scenario: show a feature with one outline scenario
    Given the file "data/git/kk-gitlab/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_1_specification @error_case @valid
Scenario Outline: unknown user, no suggestion
    Given the user "<user_name>"
    And he is unknown
    When we ask for "<number_suggestions>" suggestions
    Then there is no suggestions

Examples:
      | user_name | number_suggestions |
      | Lise      | 2                  |
      | Tim       | 1                  |

    """
    When a user access to the feature "provide_book_suggestions.feature" of the project "suggestionsWS"
    Then the following scenarios are displayed
      | id | scenario                    | scenario_type    | abstraction_level | case_type  | workflow_step |
      | 0  | unknown user, no suggestion | scenario_outline | level_1           | error_case | valid         |
    And the scenario "0" is displayed
      | id | step  | type   | value                                      | parameters                         |
      | 0  | given | simple | the user "<outline_param_0>"               | outline_param_0:user_name          |
      | 1  | given | simple | he is unknown                              |                                    |
      | 2  | when  | simple | we ask for "<outline_param_0>" suggestions | outline_param_0:number_suggestions |
      | 3  | then  | simple | there is no suggestions                    |                                    |
    And the scenario "0" examples are displayed
      | column0   | column1            |
      | user_name | number_suggestions |
      | Lise      | 2                  |
      | Tim       | 1                  |


