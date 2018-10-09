Feature: Generate documentation
  As a user,
  I want generate documentation based on the criterias I provide
  So that I can have a clear view of the projects specifications

  Background:
    Given the database is empty
    And the hierarchy nodes are
      | id   | slugName   | name              |
      | .    | root       | Hierarchy root    |
      | .01. | suggestion | Suggestion system |
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | test/features    |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |

  @level_2_technical_details @nominal_case @ready
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
    When I perform a "GET" on following URL "/api/generateDocumentation?project=_eng_library_suggestion>suggestionWS"
    Then I get the following json response body
"""
{
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [
  ],
  "children": [
    {
      "id": ".01.",
      "slugName": "suggestion",
      "name": "Suggestion system",
      "childrenLabel": "Projects",
      "childLabel": "Project",
      "projects": [
        {
          "id": "suggestionsWS",
          "name": "Suggestions WebServices",
          "branches": [
            {
              "id": 1,
              "name": "master",
              "isStable": true,
              "features": [
                {
                  "id": 1,
                  "path": "test/features/provide_book_suggestions.feature",
                  "name": "Provide book suggestions",
                  "description": "As a user,\nI want some book suggestions\nSo that I can do some discovery",
                  "tags": [
                  ],
                  "language": "en",
                  "keyword": "Feature",
                  "scenarios": [
                    {
                      "id": 0,
                      "name": "providing several book suggestions",
                      "abstractionLevel": "level_0_high_level",
                      "caseType": "nominal_case",
                      "workflowStep": "ready",
                      "keyword": "Scenario",
                      "description": "",
                      "tags": [
                        "level_0_high_level",
                        "nominal_case",
                        "ready"
                      ],
                      "steps": [
                        {
                          "id": 0,
                          "keyword": "Given",
                          "text": "a user",
                          "argument": [
                          ]
                        },
                        {
                          "id": 1,
                          "keyword": "When",
                          "text": "we ask for suggestions",
                          "argument": [
                          ]
                        },
                        {
                          "id": 2,
                          "keyword": "Then",
                          "text": "the suggestions are popular and available books adapted to the age of the user",
                          "argument": [
                          ]
                        }
                      ]
                    }
                  ],
                  "comments": [
                  ]
                }
              ]
            }
          ]
        }
      ],
      "children": [
      ]
    }
  ]
}

"""

  @level_2_technical_details @nominal_case @ready
  Scenario: generate documentation with a scenario with some parameters - json output
    Given the file "data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
    When I perform a "GET" on following URL "/api/generateDocumentation?project=_eng_library_suggestion>suggestionWS"
    Then I get the following json response body
"""
{
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [
  ],
  "children": [
    {
      "id": ".01.",
      "slugName": "suggestion",
      "name": "Suggestion system",
      "childrenLabel": "Projects",
      "childLabel": "Project",
      "projects": [
        {
          "id": "suggestionsWS",
          "name": "Suggestions WebServices",
          "branches": [
            {
              "id": 1,
              "name": "master",
              "isStable": true,
              "features": [
                {
                  "id": 1,
                  "path": "test/features/provide_book_suggestions.feature",
                  "name": "Provide book suggestions",
                  "description": "As a user,\nI want some book suggestions\nSo that I can do some discovery",
                  "tags": [
                  ],
                  "language": "en",
                  "keyword": "Feature",
                  "scenarios": [
                    {
                      "id": 0,
                      "name": "providing several book suggestions",
                      "abstractionLevel": "level_0_high_level",
                      "caseType": "error_case",
                      "workflowStep": "valid",
                      "keyword": "Scenario",
                      "description": "",
                      "tags": [
                        "level_1_specification",
                        "error_case",
                        "valid"
                      ],
                      "steps": [
                          {
                            "id": 0,
                            "keyword": "Given",
                            "text": "the user \"Tim\"",
                            "argument": [
                            ]
                          },
                          {
                            "id": 1,
                            "keyword": "And",
                            "text": "impossible to get information on the user",
                            "argument": [
                            ]
                          },
                          {
                            "id": 2,
                            "keyword": "When",
                            "text": "we ask for \"3\" suggestions from \"2\" different categories",
                            "argument": [
                            ]
                          },
                          {
                            "id": 3,
                            "keyword": "Then",
                            "text": "the system is temporary not available",
                            "argument": [
                            ]
                          }
                      ]
                    }
                  ],
                  "comments": [
                  ]
                }
              ]
            }
          ]
        }
      ],
      "children": [
      ]
    }
  ]
}

"""

  @level_2_technical_details @nominal_case @ready
  Scenario: generate documentation with a scenario with a multi lines step - json output
    Given the file "data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
    When I perform a "GET" on following URL "/api/generateDocumentation?project=_eng_library_suggestion>suggestionWS"
    Then I get the following json response body
"""
{
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [
  ],
  "children": [
    {
      "id": ".01.",
      "slugName": "suggestion",
      "name": "Suggestion system",
      "childrenLabel": "Projects",
      "childLabel": "Project",
      "projects": [
        {
          "id": "suggestionsWS",
          "name": "Suggestions WebServices",
          "branches": [
            {
              "id": 1,
              "name": "master",
              "isStable": true,
              "features": [
                {
                  "id": 1,
                  "path": "test/features/provide_book_suggestions.feature",
                  "name": "Provide book suggestions",
                  "description": "As a user,\nI want some book suggestions\nSo that I can do some discovery",
                  "tags": [
                  ],
                  "language": "en",
                  "keyword": "Feature",
                  "scenarios": [
                        {
                          "id": 1,
                          "name": "providing several book suggestions with popular categories",
                          "abstractionLevel": "level_1",
                          "caseType": "nominal_case",
                          "workflowStep": "valid",
                          "keyword": "Scenario",
                          "description": "",
                          "tags": [
                            "level_1",
                            "nominal_case",
                            "valid"
                          ],
                          "steps": [
                            {
                              "id": 0,
                              "keyword": "Given",
                              "text": "the user \"Tim\"",
                              "argument": [
                              ]
                            },
                            {
                              "id": 1,
                              "keyword": "And",
                              "text": "he is \"4\" years old",
                              "argument": [
                              ]
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
                              "argument": [
                              ]
                            },
                            {
                              "id": 4,
                              "keyword": "Then",
                              "text": "the suggestions are popular and available books adapted to the age of the user",
                              "argument": [
                              ]
                            }
                          ]
                        }
                  ],
                  "comments": [
                  ]
                }
              ]
            }
          ]
        }
      ],
      "children": [
      ]
    }
  ]
}

"""

  @level_2_technical_details @nominal_case @draft
  Scenario: generate documentation with a outline scenario - json output
    Given the file "data/git/suggestionsWS/master/test/features/provide_book_suggestions.feature"
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
    When I perform a "GET" on following URL "/api/generateDocumentation?project=_eng_library_suggestion>suggestionWS"
    Then I get the following json response body
"""
{
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [
  ],
  "children": [
    {
      "id": ".01.",
      "slugName": "suggestion",
      "name": "Suggestion system",
      "childrenLabel": "Projects",
      "childLabel": "Project",
      "projects": [
        {
          "id": "suggestionsWS",
          "name": "Suggestions WebServices",
          "branches": [
            {
              "id": 1,
              "name": "master",
              "isStable": true,
              "features": [
                {
                  "id": 1,
                  "path": "test/features/provide_book_suggestions.feature",
                  "name": "Provide book suggestions",
                  "description": "As a user,\nI want some book suggestions\nSo that I can do some discovery",
                  "tags": [
                  ],
                  "language": "en",
                  "keyword": "Feature",
                  "scenarios": [
                        {
                          "id": 1,
                          "name": "providing several book suggestions with popular categories",
                          "abstractionLevel": "level_1",
                          "caseType": "nominal_case",
                          "workflowStep": "valid",
                          "keyword": "Scenario",
                          "description": "",
                          "tags": [
                            "level_1",
                            "nominal_case",
                            "valid"
                          ],
                          "steps": [
                            {
                              "id": 0,
                              "keyword": "Given",
                              "text": "the user \"Tim\"",
                              "argument": [
                              ]
                            },
                            {
                              "id": 1,
                              "keyword": "And",
                              "text": "he is \"4\" years old",
                              "argument": [
                              ]
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
                              "argument": [
                              ]
                            },
                            {
                              "id": 4,
                              "keyword": "Then",
                              "text": "the suggestions are popular and available books adapted to the age of the user",
                              "argument": [
                              ]
                            }
                          ]
                        }
                  ],
                  "comments": [
                  ]
                }
              ]
            }
          ]
        }
      ],
      "children": [
      ]
    }
  ]
}

"""

  @level_2_technical_details @nominal_case @draft
  Scenario: generate documentation with a simple feature - html output
    When I perform a "GET" on following URL "/app/documentation/generate/output?project=_eng_library_suggestion>suggestionWS"
    Then the page contains
"""
    <div id="Feature">
        Project: Suggestions WebServices<br/>
        Branch: master<br/>
"""
    And the page contains
"""
        <strong>Feature</strong>: Provide some book suggestions<br/>
        As a user,<br/>I want some book suggestions<br/>So that I can do some discovery<br/>
"""
    And the page contains
"""
            <div id="Scenario_0">
                Abstraction level: level_0_high_level, case: nominal_case, step: ready <br/>
            <strong>Scenario:</strong>
                providing several book suggestions <br/>
"""
    And the page contains
"""
                    <div id="Step_0">
                        <strong>Given:</strong>
                        a user

                    </div>
"""
    And the page contains
"""
                    <div id="Step_1">
                        <strong>When:</strong>
                        we ask for suggestions

                    </div>
"""
    And the page contains
"""
                    <div id="Step_2">
                    <strong>Then:</strong>
                    the suggestions are popular and available books adapted to the age of the user

                    </div>
"""
