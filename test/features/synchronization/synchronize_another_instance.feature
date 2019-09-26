Feature: Once resources are synchronized, trigger the synchronization of another instance

  Background:
    Given No project is checkout
    And the database is empty
    And the cache is empty
    And the configuration
      | path          | value                  |
      | replica.url | http://localhost:9009  |
    And we have the following projects
      | id            | name                    | repositoryUrl                                  | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the server "target/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
"""
Feature: As a user, I want some book suggestions so that I can do some discovery
"""
    And the replica call count is reset

  @level_1_specification @nominal_case @valid @documentation @ongoing
  Scenario: Once resources are synchronized, trigger the synchronization of another instance

    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then the webhook "http://localhost:9009/api/projects/suggestionsWS/synchronize" is triggered

