Feature: Get BDD features from a project
  As a user,
  I want theGardener to retrieve all features related to BDD of my project
  So that I can access to those features through theGardener


  Background:
   # Given the configuration settings
    #  | key                           | value         |
     # | projects_local_copy_root_path | data/projects |


  @level_0_high_level @nominal_case @draft
  Scenario: get bdd features from a project
    Given a project in theGardener hosted on a remote server
    When BDD features synchronization action is triggered
    Then the project BDD features of this project are retrieved from the remote server

  @level_1_specification @nominal_case @draft
  Scenario: get bdd features from a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    And the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
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
    Then the file system store now the file "data/projects/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready @general @literal
Scenario: providing several book suggestions
  Given a user Tim
  When we ask for some suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    And we have now those branches in the database
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
    And we have now those tags in the database
      | scenarioId | tag     |
      | 1          | general |
      | 1          | literal |

  @level_1_specification @nominal_case @draft
  Scenario: get bdd features from a project with a complex structure of feature files
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    And the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                                                       | content                           |
      | test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |
    When BDD features synchronization action is triggered
    Then the file system store now the files
      | files                                                                                         | content                           |
      | data/projects/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | data/projects/suggestionsWS/master/test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |


  @level_2_technical_details @nominal_case @ready
  Scenario: update bdd features from a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    And the file "data/projects/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready @general @literal
Scenario: providing several book suggestions
  Given a user Tim
  When we ask for some suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have those features in the database
      | id | path                                           | name                                                                        | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have those scenario in the database
      | id | description                        | scenarioType | workflowStep | caseType     | abstractionLevel | featureId |
      | 1  | providing several book suggestions | Scenario     | ready        | nominal_case | level_0          | 1         |
    And we have those stepsAsJSon for the scenario "1" in the database
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
    And we have those tags in the database
      | scenarioId | tag     |
      | 1          | general |
      | 1          | literal |
    And the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ongoing @general
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When BDD features synchronization action is triggered
    Then the file system store now the file "data/projects/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready @general
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    And we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                           | name                                                                    | description | branchId |
      | 1  | test/features/provide_book_suggestions.feature | As a user, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | description                        | scenarioType | workflowStep | caseType     | abstractionLevel | featureId |
      | 1  | providing several book suggestions | Scenario     | ongoing      | nominal_case | level_0          | 1         |
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
    And we have now those tags in the database
      | scenarioId | tag     |
      | 1          | general |


## [PULL] One implementation can be a scheduler that pull from time to time all the remote server.
## [PUSH] Another implementation, in addition to the first one, is to define a resource on theGardener that can be triggered by a webHook when some code is pushed on the remote server.


