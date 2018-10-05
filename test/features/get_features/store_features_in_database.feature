Feature: Store the feature files in the database
  As a dev,
  I want to store the feature files in a database
  So that I can propose several criterias to the end user when he want to search specific scenario


  @level_1_specification @nominal_case @valid
  Scenario Outline: show the different possible values of the annotation considered by theGardener
    Given No project is checkout
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

<annotation1> <annotation2> <annotation3>
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    And we have no branch in the database
    And we have no feature in the database
    And we have no scenario in the database
    And we have no tag in the database
    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                                                                | name                                                                        | description | branchId |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | name                               | keyword  | workflowStep               | caseType               | abstractionLevel               | description |
      | 1  | providing several book suggestions | Scenario | <considered_workflow_step> | <considered_case_type> | <considered_abstraction_level> |             |

    # TODO Add featureId in the scenario table

    Examples:
      | annotation1                | annotation2 | annotation3 | considered_abstraction_level | considered_case_type | considered_workflow_step |
      |                            |             |             | level_1_specification        | nominal_case         | valid                    |
      | @level_0_high_level        |             |             | level_0_high_level           | nominal_case         | valid                    |
      | @level_1_specification     |             |             | level_1_specification        | nominal_case         | valid                    |
      | @level_2_technical_details |             |             | level_2_technical_details    | nominal_case         | valid                    |
      | @nominal_case              |             |             | level_1_specification        | nominal_case         | valid                    |
      | @limit_case                |             |             | level_1_specification        | limit_case           | valid                    |
      | @error_case                |             |             | level_1_specification        | error_case           | valid                    |
      | @draft                     |             |             | level_1_specification        | nominal_case         | draft                    |
      | @ready                     |             |             | level_1_specification        | nominal_case         | ready                    |
      | @ongoing                   |             |             | level_1_specification        | nominal_case         | ongoing                  |
      | @valid                     |             |             | level_1_specification        | nominal_case         | valid                    |
      | @level_0_high_level        | @draft      |             | level_0_high_level           | nominal_case         | draft                    |
      | @level_0_high_level        | @limit_case |             | level_0_high_level           | limit_case           | valid                    |
      | @level_0_high_level        | @limit_case | @ongoing    | level_0_high_level           | limit_case           | ongoing                  |


  @level_1_specification @limit_case @valid
  Scenario Outline: show the different possible values of the annotation considered by theGardener
    Given No project is checkout
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

<annotation1>
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

    And we have no branch in the database
    And we have no feature in the database
    And we have no scenario in the database
    And we have no tag in the database
    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                                                                | name                                                                        | description | branchId |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | name                               | keyword  | workflowStep               | caseType               | abstractionLevel               | description |
      | 1  | providing several book suggestions | Scenario | <considered_workflow_step> | <considered_case_type> | <considered_abstraction_level> |             |

    Examples:
      | annotation1        | considered_abstraction_level | considered_case_type | considered_workflow_step |
      | @level_0           | level_0_high_level           | nominal_case         | valid                    |
      | @level_1           | level_1_specification        | nominal_case         | valid                    |
      | @level_2           | level_2_technical_details    | nominal_case         | valid                    |
      | @level_0_what_ever | level_0_high_level           | nominal_case         | valid                    |
      | @level_1_what_ever | level_1_specification        | nominal_case         | valid                    |
      | @level_2_what_ever | level_2_technical_details    | nominal_case         | valid                    |
      | @Level_0_what_ever | level_0_high_level           | nominal_case         | valid                    |
      | @level1_what_ever  | level_1_specification        | nominal_case         | valid                    |
      | @l0                | level_0_high_level           | nominal_case         | valid                    |
      | @level1            | level_1_specification        | nominal_case         | valid                    |
      | @Level2            | level_2_technical_details    | nominal_case         | valid                    |
      | @l0_what_ever      | level_0_high_level           | nominal_case         | valid                    |
      | @l1_what_ever      | level_1_specification        | nominal_case         | valid                    |
      | @l2_what_ever      | level_2_technical_details    | nominal_case         | valid                    |
      | @nominal           | level_1_specification        | nominal_case         | valid                    |
      | @limit             | level_1_specification        | limit_case           | valid                    |
      | @error             | level_1_specification        | error_case           | valid                    |
      | @draft             | level_1_specification        | nominal_case         | draft                    |
      | @ready             | level_1_specification        | nominal_case         | ready                    |
      | @ongoing           | level_1_specification        | nominal_case         | ongoing                  |
      | @valid             | level_1_specification        | nominal_case         | valid                    |
      | @Nominal           | level_1_specification        | nominal_case         | valid                    |
      | @Draft             | level_1_specification        | nominal_case         | draft                    |

  @level_1_specification @nominal_case @valid
  Scenario: transform a scenario in Gherkin language to theGardener representation
    Given No project is checkout
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_0_high_level @nominal_case @draft
  Scenario: providing several book suggestions
    Given a user
    When we ask for suggestions
    Then the suggestions are popular and available books adapted to the age of the user
    """
    And we have no branch in the database
    And we have no feature in the database
    And we have no scenario in the database
    And we have no tag in the database
    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                                                                | name                                                                        | description | branchId |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | name                               | keyword  | workflowStep | caseType     | abstractionLevel   | description |
      | 1  | providing several book suggestions | Scenario | draft        | nominal_case | level_0_high_level |             |
    And we have now those stepsAsJSon for the scenario "1" in the database
"""
[
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
"""

  @level_1_specification @nominal_case @valid
  Scenario: transform a scenario with parameters in Gherkin language to theGardener representation
    Given No project is checkout
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_1_specification @error_case @valid
  Scenario: one service on which the suggestion system depends on is down
    Given the user "Tim"
    And impossible to get information on the user
    When we ask for "3" suggestions from "2" different categories
    Then the system is temporary not available
    """
    And we have no branch in the database
    And we have no feature in the database
    And we have no scenario in the database
    And we have no tag in the database
    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                                                                | name                                                                        | description | branchId |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | name                                                          | keyword  | workflowStep | caseType   | abstractionLevel      | description |
      | 1  | one service on which the suggestion system depends on is down | Scenario | valid        | error_case | level_1_specification |             |
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

  @level_1_specification @nominal_case @valid
  Scenario: transform a multi lines scenario with parameters in Gherkin language to theGardener representation
    Given No project is checkout
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
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

    And we have no branch in the database
    And we have no feature in the database
    And we have no scenario in the database
    And we have no tag in the database
    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                                                                | name                                                                        | description | branchId |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |

#     TODO The Feature name should be "Provide book suggestions"  based on the name of the file provide_book_suggestions.feature
    And we have now those scenario in the database
      | id | name                                                                      | keyword  | workflowStep | caseType     | abstractionLevel      | description |
      | 1  | suggestions of popular and available books adapted to the age of the user | Scenario | valid        | nominal_case | level_1_specification |             |
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

  @level_1_specification @nominal_case @ready
  Scenario: transform a outline scenario with parameters in Gherkin language to theGardener representation
    Given No project is checkout
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
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

    And we have no branch in the database
    And we have no feature in the database
    And we have no scenario in the database
    And we have no tag in the database
    When BDD features synchronization action is triggered
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                                                                | name                                                                        | description | branchId |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | name                        | keyword  | workflowStep | caseType   | abstractionLevel      | description |
      | 1  | unknown user, no suggestion | Scenario | valid        | error_case | level_1_specification |             |
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
#### TODO add a scenario to show how a step with a multiline parameter : for instance the output of a Json WS
#### TODO add a scenario to show what happen when a feature file is renamed or moved
#### TODO add a scenario to show what happen when the scenario name change

