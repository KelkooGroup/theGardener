Feature: Link a project to the hierarchy
  As an administrator,
  I want to define at which node of the hierarchy a project belongs
  So that BDD features can be well organized

# TODO :
#    - Switch to ongoing one scenario, run OnGoingBDDTest, and solve the errors until you got a success
#    - By following Project example, you will have at some point to :
#       - Create theGardener/test/steps/DefineHierarchySteps.scala
#       - Update theGardener/conf/evolutions/default/1.sql to create hierarchy table
#       - Update theGardener/test/steps/CommonSteps.scala  CommonSteps.cleanDatabase
#       - Update theGardener/conf/routes
#       - Create theGardener/app/repository/HierarchyRepository.scala
#       - Create theGardener/app/models/Hierarchy.scala
#       - Update theGardener/app/controllers/Api.scala to create HierarchyController

  Background:
    Given the hierarchy nodes are
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
      | .01.02.    | bakery     | Bakery system group  |
      | .02.       | product    | Product view         |
    And we have the following projects
      | id                 | name                    | repositoryUrl                                             | stableBranch | featuresRootPath |
      | suggestionsWS      | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git      | master       | test/features    |
      | suggestionsReports | Suggestions Reports     | git@gitlab.corp.kelkoo.net:library/suggestionsReports.git | master       | test/features    |
    And there is no links from projects to hierarchy nodes


  @level_2_technical_details @nominal_case @draft
  Scenario: Link a project to a hierarchy node
    When I perform a "POST" on following URL "/api/project/suggestionsWS/belongsTo/.01.01.01."
    Then I get a response with status "201"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system"
	}
]
  """
    And the links between hierarchy nodes are now
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |

  @level_2_technical_details @nominal_case @draft
  Scenario: Link a project to several hierarchy nodes
    Given the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
    When I perform a "POST" on following URL "/api/project/suggestionsWS/belongsTo/.02."
    Then I get a response with status "201"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system"
	},
    {
		"id": ".02.",
		"slugName": "product",
		"name": "Product view"
	}
]
  """
    And the links between hierarchy nodes are now
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
      | suggestionsWS | .02.        |

  @level_2_technical_details @nominal_case @draft
  Scenario: Get hierarchy nodes related to a project
    Given the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
      | suggestionsWS | .02.        |
    When I perform a "GET" on following URL "/api/project/suggestionsWS/belongsTo"
    Then I get a response with status "200"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system"
	},
    {
		"id": ".02.",
		"slugName": "product",
		"name": "Product view"
	}
]
  """


  @level_2_technical_details @nominal_case @draft
  Scenario: Delete a link between a project and a hierarchy node
    Given the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
      | suggestionsWS | .02.        |
    When I perform a "DELETE" on following URL "/api/project/suggestionsWS/belongsTo/.02."
    Then I get a response with status "200"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system"
	}
]
  """
    And the links between hierarchy nodes are now
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
