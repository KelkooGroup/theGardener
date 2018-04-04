Feature: Show a feature
  As a user,
  I want to see my feature in theGardener
  So that my project feature is shared with all users

  Background:
    Given the project settings are setup in theGardener
      | id            | name                    | repository_url                                       | stable_branch | features_root_path |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master        | test/features      |

  @level_0_high_level @nominal_case @valid
  Scenario: show a simple feature
    Given a simple feature is available in my project
    When a user access to this feature in theGardener
    Then this feature is displayed properly

  @level_1_specification @nominal_case @valid
  Scenario: show a feature with one simple scenario with all required tags and meta data
    Given the file "data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
      | id                                                    | name                          | description                                                             | project                 |
      | suggestionsWS/master/provide_book_suggestions.feature | Provide some book suggestions | As a user, I want some book suggestions So that I can do some discovery | Suggestions WebServices |
    And the following scenarios are displayed
      | id | scenario                           | scenario_type | abstraction_level | case_type    | workflow_step |
      | 0  | providing several book suggestions | Scenario      | level_0           | nominal_case | ready         |
    And the scenario "0" is displayed
      | id | step  | value                                                                          |
      | 0  | Given | a user                                                                         |
      | 1  | When  | we ask for suggestions                                                         |
      | 2  | Then  | the suggestions are popular and available books adapted to the age of the user |

  @level_2_technical_details @nominal_case @valid
  Scenario: show a feature with one simple scenario with all required tags and meta data - html output
    Given the file "data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
    When I perform a GET on following URL "/feature/suggestionsWS/provide_book_suggestions.feature"
    Then the page contains
"""
    <div id="suggestionsWS_master_provide_book_suggestions_feature">
        Project: Suggestions WebServices<br/>
        Branch: master<br/>
"""
    And the page contains
"""
        <strong>Feature</strong>: Provide some book suggestions<br/>
        As a user,<br/>I want some book suggestions<br/>So that I can do some discovery<br/>
"""
    And the page contains
"""
            <div id="Scenario_0">
                Abstraction level: level_0_high_level, case: nominal_case, step: ready <br/>
            <strong>Scenario:</strong>
                providing several book suggestions <br/>
"""
    And the page contains
"""
                    <div id="Step_0">
                        <strong>Given:</strong>
                        a user

                    </div>
"""
    And the page contains
"""
                    <div id="Step_1">
                        <strong>When:</strong>
                        we ask for suggestions

                    </div>
"""
    And the page contains
"""
                    <div id="Step_2">
                    <strong>Then:</strong>
                    the suggestions are popular and available books adapted to the age of the user

                    </div>
"""


  @level_2_technical_details @nominal_case @valid
  Scenario: show a feature with one simple scenario with all required tags and meta data - json output
    Given the file "data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
    When I perform a GET on following URL "/api/feature/suggestionsWS/provide_book_suggestions.feature"
    Then I get the following json response body
"""
  {
    "id": "suggestionsWS/master/provide_book_suggestions.feature",
    "branch": "master",
    "name": "Provide some book suggestions",
    "description": "As a user,\nI want some book suggestions\nSo that I can do some discovery",
    "tags": [],
    "language": "en",
    "keyword": "Feature",
    "scenarios":
        [
          {
            "id": 0,
            "name": "providing several book suggestions",
            "abstractionLevel": "level_0_high_level",
            "caseType": "nominal_case",
            "workflowStep": "ready",

            "keyword": "Scenario",
            "description": "",
            "tags": [
                "level_0_high_level",
                "nominal_case",
                "ready"
            ],

            "steps": [
                        {
                        "id": 0,
                        "keyword": "Given",
                        "text": "a user",
                        "argument": []
                        },
                        {
                        "id": 1,
                        "keyword": "When",
                        "text": "we ask for suggestions",
                        "argument": []
                        },
                        {
                        "id": 2,
                        "keyword": "Then",
                        "text": "the suggestions are popular and available books adapted to the age of the user",
                        "argument": []
                        }
                      ]

          }
        ],
    "comments": []
  }
"""


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
      | id | step  | value                                                                | parameters          |
      | 0  | given | the user <param_0>                                                   | param_0:Tim         |
      | 1  | given | impossible to get information on the user                            |                     |
      | 2  | when  | we ask for <param_0> suggestions from <param_1> different categories | param_0:3,param_1:2 |
      | 3  | then  | the system is temporary not available                                |                     |

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
      | id | step  | value                                                                          | parameters          |
      | 0  | given | the user <param_0>                                                             | param_0:Tim         |
      | 1  | given | he is <param_0> years old                                                      | param_0:4           |
      | 2  | given | the popular categories for this age are                                        |                     |
      | 3  | when  | we ask for <param_0> suggestions from <param_1> different categories           | param_0:3,param_1:2 |
      | 4  | then  | the suggestions are popular and available books adapted to the age of the user |                     |
    Then the step "2" of scenario "0" is displayed with the arguments
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


