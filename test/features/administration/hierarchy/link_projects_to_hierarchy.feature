Feature: Link a project to the hierarchy
  As an administrator,
  I want to define at which node of the hierarchy a project belongs
  So that BDD features can be well organized

  Background:
    Given the database is empty
    And the cache is empty
    And the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
      | .01.01.02. | user       | User system          | Projects      | Project      |
      | .01.01.03. | search     | Search system        | Projects      | Project      |
      | .01.02.    | other      | Other system group   | Systems       | System       |
      | .01.03.    | another    | Another system group | Systems       | System       |
      | .02.       | product    | Product view         | System groups | System group |
    And we have the following projects
      | id                 | name                    | repositoryUrl                                             | stableBranch | featuresRootPath |
      | suggestionsWS      | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git      | master       | test/features    |
      | suggestionsReports | Suggestions Reports     | git@gitlab.corp.kelkoo.net:library/suggestionsReports.git | master       | test/features    |
    And there is no links from projects to hierarchy nodes


  @level_2_technical_details @nominal_case @valid @put_project_in_hierarchy
  Scenario: Link a project to a hierarchy node
    When I perform a "POST" on following URL "/api/projects/suggestionsWS/hierarchy/.01.01.01."
    Then I get a response with status "201"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project"
	}
]
  """
    And the links between hierarchy nodes are now
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |

  @level_2_technical_details @nominal_case @valid
  Scenario: Link a project to several hierarchy nodes
    Given the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
    When I perform a "POST" on following URL "/api/projects/suggestionsWS/hierarchy/.02."
    Then I get a response with status "201"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project"
	},
    {
		"id": ".02.",
		"slugName": "product",
		"name": "Product view",
		"childrenLabel": "System groups",
		"childLabel": "System group"
	}
]
  """
    And the links between hierarchy nodes are now
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
      | suggestionsWS | .02.        |

  @level_2_technical_details @nominal_case @valid
  Scenario: Get hierarchy nodes related to a project
    Given the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
      | suggestionsWS | .02.        |
    When I perform a "GET" on following URL "/api/projects/suggestionsWS"
    Then I get a response with status "200"
    And I get the following json response body
   """
{
	"id": "suggestionsWS",
	"name": "Suggestions WebServices",
	"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
	"stableBranch": "master",
	"featuresRootPath": "test/features",
	"hierarchy": [{
			"id": ".01.01.01.",
			"slugName": "suggestion",
			"name": "Suggestion system",
			"childrenLabel": "Projects",
			"childLabel": "Project"
		},
		{
			"id": ".02.",
			"slugName": "product",
			"name": "Product view",
			"childrenLabel": "System groups",
			"childLabel": "System group"
		}
	]
}
  """

  @level_2_technical_details @nominal_case @valid
  Scenario: Get hierarchy nodes related to a project
    Given the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
      | suggestionsWS | .02.        |
    When I perform a "GET" on following URL "/api/projects/suggestionsWS/hierarchy"
    Then I get a response with status "200"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project"
	},
    {
		"id": ".02.",
		"slugName": "product",
		"name": "Product view",
		"childrenLabel": "System groups",
		"childLabel": "System group"
	}
]
  """


  @level_2_technical_details @nominal_case @valid
  Scenario: Delete a link between a project and a hierarchy node
    Given the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
      | suggestionsWS | .02.        |
    When I perform a "DELETE" on following URL "/api/projects/suggestionsWS/hierarchy/.02."
    Then I get a response with status "200"
    And I get the following json response body
   """
[
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project"
	}
]
  """
    And the links between hierarchy nodes are now
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
