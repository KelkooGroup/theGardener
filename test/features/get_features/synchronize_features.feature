Feature: Synchronize BDD features from a project on a remote server
  As a user,
  I want theGardener to synchronize all features related to BDD of my project on a remote server when those ones has been changed
  So that I can access to the last version of those features through theGardener


  Background:
    Given No project is checkout
    And the database is empty


  @level_1_specification @nominal_case @valid
  Scenario: Synchronize the features of a project with a webhook
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
  Scenario: Synchronize the features of all the projects with a scheduler
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

