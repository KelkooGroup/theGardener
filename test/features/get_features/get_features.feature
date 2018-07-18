Feature: Get BDD features from a project
  As a user,
  I want theGardener to retrieve all features related to BDD of my project
  So that I can access to those features through theGardener


  Background:
    Given No project is checkout


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
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When BDD features synchronization action is triggered
    Then the file system store now the file "target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

  @level_1_specification @nominal_case @valid
  Scenario: get bdd features from a project with a complex structure of feature files
    Given we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                                                       | content                           |
      | test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |
    When BDD features synchronization action is triggered
    Then the file system store now the files
      | file                                                                                            | content                           |
      | target/data/git/suggestionsWS/master/test/features/suggestions/provide_book_suggestions.feature | Feature: Provide book suggestions |
      | target/data/git/suggestionsWS/master/test/features/setup/setup_suggestions.feature              | Feature: Setup book suggestions   |


  @level_1_technical_details @nominal_case @valid
  Scenario: update bdd features from a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery
    """
    And the branch "master" of the project "suggestionsWS" is already checkout
    And the file "test/features/provide_book_suggestions.feature" of the server "target/data/GetFeatures" in the project "library/suggestionsWS" on the branch "master" is updated with content
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When BDD features synchronization action is triggered
    Then the file system store now the file "target/data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """



## [PULL] One implementation can be a scheduler that pull from time to time all the remote server.
## [PUSH] Another implementation, in addition to the first one, is to define a resource on theGardener that can be triggered by a webHook when some code is pushed on the remote server.


