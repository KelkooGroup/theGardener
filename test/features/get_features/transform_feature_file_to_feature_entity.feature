Feature: Transform features in Gherkin language to theGardener representation
  As a user,
  I want theGardener to retrieve all features related to BDD of my project
  So that I can access to those features through theGardener

  Background:
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    And we have no branch in the database
    And we have no feature in the database
    And we have no scenario in the database
    And we have no tag in the database

  @level_1_specification @nominal_case @draft
  Scenario Outline: show the different possible values of the annotation considered by theGardener
    Given the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

<annotation1> <annotation2> <annotation3>
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                           | name                                                                        | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | description                        | scenarioType | workflowStep               | caseType               | abstractionLevel               | featureId |
      | 1  | providing several book suggestions | Scenario     | <considered_workflow_step> | <considered_case_type> | <considered_abstraction_level> | 1         |

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
      | @draft                     |             |             | level_1                      | nominal_case         | ready                    |
      | @ongoing                   |             |             | level_1                      | nominal_case         | ongoing                  |
      | @valid                     |             |             | level_1                      | nominal_case         | valid                    |
      | @level_0_high_level        | @draft      |             | level_0                      | nominal_case         | draft                    |
      | @level_0_high_level        | @limit_case |             | level_0                      | limit_case           | valid                    |
      | @level_0_high_level        | @limit_case | @ongoing    | level_0                      | limit_case           | ongoing                  |


  @level_1_specification @limit_case @draft
  Scenario Outline: show the different possible values of the annotation considered by theGardener
    Given the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

<annotation1>
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                           | name                                                                        | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | description                        | scenarioType | workflowStep               | caseType               | abstractionLevel               | featureId |
      | 1  | providing several book suggestions | Scenario     | <considered_workflow_step> | <considered_case_type> | <considered_abstraction_level> | 1         |

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
      | @draft             | level_1                      | nominal_case         | ready                    |
      | @ongoing           | level_1                      | nominal_case         | ongoing                  |
      | @valid             | level_1                      | nominal_case         | valid                    |
      | @Nominal           | level_1                      | nominal_case         | valid                    |
      | @Draft             | level_1                      | nominal_case         | draft                    |

  @level_1_specification @nominal_case @draft
  Scenario: transform a scenario in Gherkin language to theGardener representation
    Given the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_0_high_level @nominal_case @draft
  Scenario: providing several book suggestions
    Given a user
    When we ask for suggestions
    Then the suggestions are popular and available books adapted to the age of the user
    """

    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                           | name                                                                        | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | description                        | scenarioType | workflowStep | caseType     | abstractionLevel | featureId |
      | 1  | providing several book suggestions | Scenario     | ready        | nominal_case | level_0          | 1         |
    And we have now those stepsAsJSon for the scenario "1" in the database
"""
[
                        {
                        "id": 0,
                        "keyword": "Given",
                        "text": "a user Tim",
                        "argument": []
                        },
                        {
                        "id": 1,
                        "keyword": "When",
                        "text": "we ask for some suggestions",
                        "argument": []
                        },
                        {
                        "id": 2,
                        "keyword": "Then",
                        "text": "the suggestions are popular and available books adapted to the age of the user",
                        "argument": []
                        }
]
"""

  @level_1_specification @nominal_case @draft
  Scenario: transform a scenario with parameters in Gherkin language to theGardener representation
    Given the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_1_specification @error_case @valid
  Scenario: one service on which the suggestion system depends on is down
    Given the user "Tim"
    And impossible to get information on the user
    When we ask for "3" suggestions from "2" different categories
    Then the system is temporary not available
    """

    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                           | name                                                                        | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | description                        | scenarioType | workflowStep | caseType     | abstractionLevel | featureId |
      | 1  | providing several book suggestions | Scenario     | valid        | error_case | level_1          | 1         |
    And we have now those stepsAsJSon for the scenario "1" in the database
"""
[
                      {
                        "id": 0,
                        "keyword": "Given",
                        "text": "the user \"Tim\"",
                        "argument": []
                      },
                      {
                        "id": 1,
                        "keyword": "And",
                        "text": "impossible to get information on the user",
                        "argument": []
                      },
                      {
                        "id": 2,
                        "keyword": "When",
                        "text": "we ask for \"3\" suggestions from \"2\" different categories",
                        "argument": []
                      },
                      {
                        "id": 3,
                        "keyword": "Then",
                        "text": "the system is temporary not available",
                        "argument": []
                      }
]
"""

  @level_1_specification @nominal_case @draft
  Scenario: transform a multi lines scenario with parameters in Gherkin language to theGardener representation
    Given the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_1_specification @nominal_case @valid
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

    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                           | name                                                                        | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | description                        | scenarioType | workflowStep | caseType     | abstractionLevel | featureId |
      | 1  | providing several book suggestions | Scenario     | valid        | nominal_case | level_1          | 1         |
    And we have now those stepsAsJSon for the scenario "1" in the database
"""
[
                        {
                          "id": 0,
                          "keyword": "Given",
                          "text": "the user \"Tim\"",
                          "argument": []
                        },
                        {
                          "id": 1,
                          "keyword": "And",
                          "text": "he is \"4\" years old",
                          "argument": []
                        },
                        {
                          "id": 2,
                          "keyword": "And",
                          "text": "the popular categories for this age are",
                          "argument": [
                            [
                              "categoryId",
                              "categoryName"
                            ],
                            [
                              "cat1",
                              "Walt Disney"
                            ],
                            [
                              "cat2",
                              "Picture books"
                            ],
                            [
                              "cat3",
                              "Bedtime stories"
                            ]
                          ]
                        },
                        {
                          "id": 3,
                          "keyword": "When",
                          "text": "we ask for \"3\" suggestions from \"2\" different categories",
                          "argument": []
                        },
                        {
                          "id": 4,
                          "keyword": "Then",
                          "text": "the suggestions are popular and available books adapted to the age of the user",
                          "argument": []
                        }
]
"""

  @level_1_specification @nominal_case @draft
  Scenario: transform a outline scenario with parameters in Gherkin language to theGardener representation
    Given the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

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

    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                           | name                                                                        | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | description                        | scenarioType | workflowStep | caseType     | abstractionLevel | featureId |
      | 1  | providing several book suggestions | Scenario     | valid        | error_case | level_1          | 1         |
    And we have now those stepsAsJSon for the scenario "1" in the database
"""
[
                        {
                          "id": 0,
                          "keyword": "Given",
                          "text": "the user \"<user_name>\"",
                          "argument": []
                        },
                        {
                          "id": 1,
                          "keyword": "And",
                          "text": "he is unknown",
                          "argument": []
                        },
                        {
                          "id": 2,
                          "keyword": "When",
                          "text": "we ask for \"<number_suggestions>\" suggestions",
                          "argument": []
                        },
                        {
                          "id": 3,
                          "keyword": "Then",
                          "text": "there is no suggestions",
                          "argument": []
                        }
]
"""
    And we have now those examplesAsJSon for the scenario "1" in the database
"""
[
                        {
                          "id": 0,
                          "tags": [],
                          "keyword": "Examples",
                          "description": "",
                          "tableHeader": [
                            "user_name",
                            "number_suggestions"
                          ],
                          "tableBody": [
                            [
                              "Lise",
                              "2"
                            ],
                            [
                              "Tim",
                              "1"
                            ]
                          ]
                        }
]
"""


#### TODO add a scenario to show what happen when the feature file is broken
#### TODO add a scenario to show how a back ground is represented => feature.backgroundAsJson