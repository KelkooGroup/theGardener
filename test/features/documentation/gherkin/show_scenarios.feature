Feature: Generate documentation
  As a user,
  I want generate documentation based on the criterias I provide
  So that I can have a clear view of the projects specifications

  Background:
    Given the database is empty
    And the cache is empty
    And No project is checkout
    And the remote projects are empty
    And the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel |
      | .    | root       | Hierarchy root    | Views         | View       |
      | .01. | suggestion | Suggestion system | Projects      | Project    |
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |

  @level_2_technical_details @nominal_case @valid
  Scenario: generate documentation with a simple feature - json output
    Given the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_0_high_level @nominal_case @draft
  Scenario: providing several book suggestions
    Given a user
    When we ask for suggestions
    Then the suggestions are popular and available books adapted to the age of the user
    """
    And the database is synchronized on the project "suggestionsWS"
    When I perform a "GET" on following URL "/api/gherkin?project=_root_suggestion>suggestionsWS>master>provide_book_suggestions.feature"
    Then I get the following json response body
"""
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root",
	"childrenLabel": "Views",
	"childLabel": "View",
	"projects": [],
	"children": [{
		"id": ".01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project",
		"projects": [{
			"id": "suggestionsWS",
			"name": "Suggestions WebServices",
			"branches": [{
				"id": 1,
				"name": "master",
				"isStable": true,
				"features": [{
					"id": 1,
					"branchId": 1,
					"path": "test/features/provide_book_suggestions.feature",
					"tags": [],
					"language": "en",
					"keyword": "Feature",
					"name": "As a user Tim, I want some book suggestions so that I can do some discovery",
					"description": "",
					"scenarios": [{
						"keyword": "Scenario",
						"name": "providing several book suggestions",
						"description": "",
						"tags": ["draft", "level_0_high_level", "nominal_case"],
						"abstractionLevel": "level_0_high_level",
						"id": 1,
						"caseType": "nominal_case",
						"steps": [{
							"id": 0,
							"keyword": "Given",
							"text": "a user",
							"argument": []
						}, {
							"id": 1,
							"keyword": "When",
							"text": "we ask for suggestions",
							"argument": []
						}, {
							"id": 2,
							"keyword": "Then",
							"text": "the suggestions are popular and available books adapted to the age of the user",
							"argument": []
						}],
						"workflowStep": "draft"
					}],
					"comments": []
				}]
			}]
		}],
		"children": []
	}]
}

"""

  @level_2_technical_details @nominal_case @valid
  Scenario: generate documentation with a scenario with some parameters - json output
    Given the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user, I want some book suggestions so that I can do some discovery

@level_1_specification @error_case @valid
Scenario: one service on which the suggestion system depends on is down
    Given the user "Tim"
    And impossible to get information on the user
    When we ask for "3" suggestions from "2" different categories
    Then the system is temporary not available

    """
    And the database is synchronized on the project "suggestionsWS"
    When I perform a "GET" on following URL "/api/gherkin?project=_root_suggestion>suggestionsWS"
    Then I get the following json response body
"""
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root",
	"childrenLabel": "Views",
	"childLabel": "View",
	"projects": [],
	"children": [{
		"id": ".01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project",
		"projects": [{
			"id": "suggestionsWS",
			"name": "Suggestions WebServices",
			"branches": [{
				"id": 1,
				"name": "master",
				"isStable": true,
				"features": [{
					"id": 1,
					"branchId": 1,
					"path": "test/features/provide_book_suggestions.feature",
					"tags": [],
					"language": "en",
					"keyword": "Feature",
					"name": "As a user, I want some book suggestions so that I can do some discovery",
					"description": "",
					"scenarios": [{
						"keyword": "Scenario",
						"name": "one service on which the suggestion system depends on is down",
						"description": "",
						"tags": ["error_case", "level_1_specification", "valid"],
						"abstractionLevel": "level_1_specification",
						"id": 1,
						"caseType": "error_case",
						"steps": [{
							"id": 0,
							"keyword": "Given",
							"text": "the user \"Tim\"",
							"argument": []
						}, {
							"id": 1,
							"keyword": "And",
							"text": "impossible to get information on the user",
							"argument": []
						}, {
							"id": 2,
							"keyword": "When",
							"text": "we ask for \"3\" suggestions from \"2\" different categories",
							"argument": []
						}, {
							"id": 3,
							"keyword": "Then",
							"text": "the system is temporary not available",
							"argument": []
						}],
						"workflowStep": "valid"
					}],
					"comments": []
				}]
			}]
		}],
		"children": []
	}]
}
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: generate documentation with a scenario with a multi lines step - json output
    Given the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
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
    And the database is synchronized on the project "suggestionsWS"
    When I perform a "GET" on following URL "/api/gherkin?project=_root_suggestion>suggestionsWS"
    Then I get a response with status "200"
    And I get the following json response body
"""
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root",
	"childrenLabel": "Views",
	"childLabel": "View",
	"projects": [],
	"children": [{
		"id": ".01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project",
		"projects": [{
			"id": "suggestionsWS",
			"name": "Suggestions WebServices",
			"branches": [{
				"id": 1,
				"name": "master",
				"isStable": true,
				"features": [{
					"id": 1,
					"branchId": 1,
					"path": "test/features/provide_book_suggestions.feature",
					"tags": [],
					"language": "en",
					"keyword": "Feature",
					"name": "As a user, I want some book suggestions so that I can do some discovery",
					"description": "",
					"scenarios": [{
						"keyword": "Scenario",
						"name": "suggestions of popular and available books adapted to the age of the user",
						"description": "",
						"tags": [],
						"abstractionLevel": "level_1_specification",
						"id": 1,
						"caseType": "nominal_case",
						"steps": [{
							"id": 0,
							"keyword": "Given",
							"text": "the user \"Tim\"",
							"argument": []
						}, {
							"id": 1,
							"keyword": "And",
							"text": "he is \"4\" years old",
							"argument": []
						}, {
							"id": 2,
							"keyword": "And",
							"text": "the popular categories for this age are",
							"argument": [
								["categoryId", "categoryName"],
								["cat1", "Walt Disney"],
								["cat2", "Picture books"],
								["cat3", "Bedtime stories"]
							]
						}, {
							"id": 3,
							"keyword": "When",
							"text": "we ask for \"3\" suggestions from \"2\" different categories",
							"argument": []
						}, {
							"id": 4,
							"keyword": "Then",
							"text": "the suggestions are popular and available books adapted to the age of the user",
							"argument": []
						}],
						"workflowStep": "valid"
					}],
					"comments": []
				}]
			}]
		}],
		"children": []
	}]
}
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: generate documentation with a outline scenario - json output
    Given the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
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
    And the database is synchronized on the project "suggestionsWS"
    When I perform a "GET" on following URL "/api/gherkin?project=_root_suggestion>suggestionsWS"
    Then I get the following json response body
"""
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root",
	"childrenLabel": "Views",
	"childLabel": "View",
	"projects": [],
	"children": [{
		"id": ".01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project",
		"projects": [{
			"id": "suggestionsWS",
			"name": "Suggestions WebServices",
			"branches": [{
				"id": 1,
				"name": "master",
				"isStable": true,
				"features": [{
					"id": 1,
					"branchId": 1,
					"path": "test/features/provide_book_suggestions.feature",
					"tags": [],
					"language": "en",
					"keyword": "Feature",
					"name": "As a user, I want some book suggestions so that I can do some discovery",
					"description": "",
					"scenarios": [{
						"keyword": "Scenario Outline",
						"name": "unknown user, no suggestion",
						"examples": [{
							"id": 0,
							"tags": [],
							"keyword": "Examples",
							"description": "",
							"tableHeader": ["user_name", "number_suggestions"],
							"tableBody": [
								["Lise", "2"],
								["Tim", "1"]
							]
						}],
						"description": "",
						"tags": ["error_case", "level_1_specification", "valid"],
						"abstractionLevel": "level_1_specification",
						"id": 1,
						"caseType": "error_case",
						"steps": [{
							"id": 0,
							"keyword": "Given",
							"text": "the user \"<user_name>\"",
							"argument": []
						}, {
							"id": 1,
							"keyword": "And",
							"text": "he is unknown",
							"argument": []
						}, {
							"id": 2,
							"keyword": "When",
							"text": "we ask for \"<number_suggestions>\" suggestions",
							"argument": []
						}, {
							"id": 3,
							"keyword": "Then",
							"text": "there is no suggestions",
							"argument": []
						}],
						"workflowStep": "valid"
					}],
					"comments": []
				}]
			}]
		}],
		"children": []
	}]
}
"""
