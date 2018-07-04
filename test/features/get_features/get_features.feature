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
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When BDD features synchronization action is triggered
    Then the file system store now the file "data/projects/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """

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


  @level_2_technical_details @nominal_case @Ongoing
  Scenario: update bdd features from a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    And the file system store the file "data/projects/suggestionsWS/master/test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery
    """
    And the server "gitlab.corp.kelkoo.net" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
  Given a user
  When we ask for suggestions
  Then the suggestions are popular and available books adapted to the age of the user
    """
    When BDD features synchronization action is triggered
    Then the file system store now the file "data/projects/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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


