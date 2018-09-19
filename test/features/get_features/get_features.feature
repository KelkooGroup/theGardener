Feature: Get BDD features from a project
  As a user,
  I want theGardener to retrieve all features related to BDD of my project
  So that I can access to those features through theGardener


  Background:
    Given No project is checkout
    And the database is empty


  @level_0_high_level @nominal_case @valid
  Scenario: get bdd features from a project
    Given a project in theGardener hosted on a remote server
    When BDD features synchronization action is triggered
    Then the project BDD features of this project are retrieved from the remote server

  @level_1_specification @nominal_case @valid
  Scenario: get bdd features from a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery
@level_0_high_level @nominal_case @ready @general @literal
Scenario: providing several book suggestions
  Given a user Tim
  When we ask for some suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When BDD features synchronization action is triggered
    Then the file system store now the file "target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
      | id | path                                                                                | name                                                                        | description | branchId |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        |
    And we have now those scenario in the database
      | id | name                               | description | keyword  | workflowStep | caseType     | abstractionLevel   |
      | 1  | providing several book suggestions |             | Scenario | ready        | nominal_case | level_0_high_level |
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
    And we have now those tags for the scenario "1" in the database
      | level_0_high_level |
      | nominal_case       |
      | ready              |
      | general            |
      | literal            |

  @level_1_specification @nominal_case @valid
  Scenario: get bdd features from a project with a complex structure of feature files
    Given we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                                                       | content                           |
      | test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |
    When BDD features synchronization action is triggered
    Then the file system store now the files
      | file                                                                                            | content                           |
      | target/data/git/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | target/data/git/suggestionsWS/master/test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |


  @level_2_technical_details @nominal_case @valid
  Scenario: update bdd features from a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
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
      | id | path                                                                                | name                                                                        | description | branchId | keyword  |
      | 1  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        | Scenario |
    And we have those scenario for the feature "1" in the database
      | id | name                               | description | keyword  | workflowStep | caseType     | abstractionLevel   |
      | 1  | providing several book suggestions |             | Scenario | ready        | nominal_case | level_0_high_level |
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
    And we have those tags for the scenario "1" in the database
      | level_0_high_level |
      | nominal_case       |
      | ready              |
      | general            |
      | literal            |

    And the branch "master" of the project "suggestionsWS" is already checkout
    And the file "test/features/provide_book_suggestions.feature" of the server "target/data/GetFeatures" in the project "library/suggestionsWS" on the branch "master" is updated with content
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ongoing @general
Scenario: providing several book suggestions
  Given a user Tim
  When we ask for some suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When BDD features synchronization action is triggered
    Then the file system store now the file "target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ongoing @general
Scenario: providing several book suggestions
  Given a user Tim
  When we ask for some suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    And we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those features in the database
      | id | path                                                                                | name                                                                        | description | branchId | keyword  |
      | 2  | target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature | As a user Tim, I want some book suggestions so that I can do some discovery |             | 1        | Scenario |
    And we have now those scenario in the database
      | id | name                               | description | keyword  | workflowStep | caseType     | abstractionLevel   |
      | 2  | providing several book suggestions |             | Scenario | ongoing      | nominal_case | level_0_high_level |
    And we have now those stepsAsJSon for the scenario "2" in the database
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
    And we have now those tags for the scenario "2" in the database
      | level_0_high_level |
      | nominal_case       |
      | ongoing            |
      | general            |

  @level_1_specification @nominal_case @valid
  Scenario: Synchronize with a webhook a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery
    """
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then the file system store now the file "target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery
    """

  @level_1_specification @nominal_case @valid
  Scenario: Synchronize with a fixed interval all the projects
    Given we have the following configuration
      | path                               | value |
      | projects.synchronize.interval      | 60    |
      | projects.synchronize.initial.delay | 1     |
    And we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery
    """
    When the synchronization action is triggered by the scheduler
    Then the file system store now the file "target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery
    """

