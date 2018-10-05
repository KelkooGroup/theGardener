Feature: Generate documentation
  As a user,
  I want generate documentation based on the criterias I provide
  So that I can have a clear view of the projects specifications

  Background:
    Given the database is empty
    And the hierarchy nodes are
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
    And we have the following projects
      | id                 | name                    | repositoryUrl                                              | stableBranch | featuresRootPath |
      | suggestionsWS      | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/      | master       | test/features    |
      | suggestionsReports | Suggestions Reports     | target/remote/data/GetFeatures/library/suggestionsReports/ | master       | test/features    |
      | usersWS            | Users WebServices       | target/remote/data/GetFeatures/library/usersWS/            | master       | test/features    |
    And the links between hierarchy nodes are
      | projectId          | hierarchyId |
      | suggestionsWS      | .01.01.01.  |
      | suggestionsWS      | .02.        |
      | suggestionsReports | .01.01.01.  |
      | usersWS            | .01.01.02.  |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_0_high_level @nominal_case @draft
  Scenario: providing several book suggestions
    Given a user
    When we ask for suggestions
    Then the suggestions are popular and available books adapted to the age of the user

  @level_1_specification @error_case @valid
  Scenario: one service on which the suggestion system depends on is down
      Given the user "Tim"
      And impossible to get information on the user
      When we ask for "3" suggestions from "2" different categories
      Then the system is temporary not available

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
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsReports" on the branch "master" the file "test/features/provide_suggestions_reports.feature"
    """
Feature: As an admin, I want some reports on suggestions

  @level_0_high_level @nominal_case @draft
  Scenario: providing suggestions reports
    Given an admin
    When we ask for suggestions reports
    Then the suggestions reports are provided
    """
    And the server "target/remote/data/GetFeatures" host under the project "library/usersWS" on the branch "master" the file "test/features/register_user.feature"
    """
Feature: As an admin, I want register a user

  @level_0_high_level @nominal_case @draft
  Scenario: register a user
    Given an admin
    When we ask for a user registration
    Then the user is registered
    """
    And the database is synchronized on the project "suggestionsWS"
    And the database is synchronized on the project "suggestionsReports"
    And the database is synchronized on the project "usersWS"


  @level_1_specification @nominal_case @ready
  Scenario: generate documentation with all scenarios of a project
    When I perform a "GET" on following URL "/api/generateDocumentation?project=_eng_library_suggestion>suggestionWS"
    Then I get the following scenarios
      | hierarchy  | project       | feature                          | scenario                                                                  |
      | .01.01.01. | suggestionsWS | provide_book_suggestions.feature | providing several book suggestions                                        |
      | .01.01.01. | suggestionsWS | provide_book_suggestions.feature | one service on which the suggestion system depends on is down             |
      | .01.01.01. | suggestionsWS | provide_book_suggestions.feature | suggestions of popular and available books adapted to the age of the user |

  @level_1_specification @nominal_case @ready
  Scenario: generate documentation with all scenarios of all projects under a hierarchy
    When I perform a "GET" on following URL "/api/generateDocumentation?node=_eng_library_suggestion"
    Then I get the following scenarios
      | hierarchy               | project            | feature                             | scenario                                                                  |
      | _eng_library_suggestion | suggestionsWS      | provide_book_suggestions.feature    | providing several book suggestions                                        |
      | _eng_library_suggestion | suggestionsWS      | provide_book_suggestions.feature    | one service on which the suggestion system depends on is down             |
      | _eng_library_suggestion | suggestionsWS      | provide_book_suggestions.feature    | suggestions of popular and available books adapted to the age of the user |
      | _eng_library_suggestion | suggestionsReports | provide_suggestions_reports.feature | providing suggestions reports                                             |

  @level_1_specification @nominal_case @ready
  Scenario: generate documentation with all scenarios of all projects under a high level hierarchy
    When I perform a "GET" on following URL "/api/generateDocumentation?node=_eng_library"
    Then I get the following scenarios
      | hierarchy               | project            | feature                             | scenario                                                                  |
      | _eng_library_suggestion | suggestionsWS      | provide_book_suggestions.feature    | providing several book suggestions                                        |
      | _eng_library_suggestion | suggestionsWS      | provide_book_suggestions.feature    | one service on which the suggestion system depends on is down             |
      | _eng_library_suggestion | suggestionsWS      | provide_book_suggestions.feature    | suggestions of popular and available books adapted to the age of the user |
      | _eng_library_suggestion | suggestionsReports | provide_suggestions_reports.feature | providing suggestions reports                                             |
      | _eng_library_user       | usersWS            | register_user.feature               | register a user                                                           |


# TODO ADD a scenario to select a specific branch
# TODO ADD a scenario to select a specific tag
