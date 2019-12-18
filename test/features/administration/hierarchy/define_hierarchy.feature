Feature: Define hierarchy
  As an administrator,
  I want to define the projects hierarchy into theGardener
  So that BDD features can be well organized

  Background:
    Given the database is empty
    And the cache is empty


  @level_2_technical_details @nominal_case @valid @define_hierarchy
  Scenario: Add a hierarchy node
    Given no hierarchy nodes is setup in theGardener
    When I perform a "POST" on following URL "/api/hierarchy" with json body
        """
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root",
	"childrenLabel": "Projects",
	"childLabel": "Project"
}
        """
    Then I get a response with status "201"
    And I get the following json response body
        """
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root",
	"childrenLabel": "Projects",
	"childLabel": "Project"
}
        """
    And the hierarchy nodes are now
      | id | slugName | name           | childrenLabel | childLabel |
      | .  | root     | Hierarchy root | Projects      | Project    |

  @level_2_technical_details @nominal_case @valid @define_hierarchy
  Scenario: Get all hierarchy
    Given the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
      | .01.01.02. | user       | User system          | Projects      | Project      |
      | .01.01.03. | search     | Search system        | Projects      | Project      |
      | .01.02.    | other      | Other system group   | Systems       | System       |
      | .01.03.    | another    | Another system group | Systems       | System       |
    When I perform a "GET" on following URL "/api/hierarchy"
    Then I get a response with status "200"
    And I get the following json response body
   """
[
    {
		"id": ".",
		"slugName": "root",
		"name": "Hierarchy root",
		"childrenLabel": "Views",
		"childLabel": "View"
	},
    {
		"id": ".01.",
		"slugName": "eng",
		"name": "Engineering view",
		"childrenLabel": "System groups",
		"childLabel": "System group"
	},
    {
		"id": ".01.01.",
		"slugName": "library",
		"name": "Library system group",
		"childrenLabel": "Systems",
		"childLabel": "System"
	},
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
		"childrenLabel": "Projects",
		"childLabel": "Project"
	},
    {
		"id": ".01.01.02.",
		"slugName": "user",
		"name": "User system",
		"childrenLabel": "Projects",
		"childLabel": "Project"
	},
    {
		"id": ".01.01.03.",
		"slugName": "search",
		"name": "Search system",
		"childrenLabel": "Projects",
		"childLabel": "Project"
	},
    {
		"id": ".01.02.",
		"slugName": "other",
		"name": "Other system group",
		"childrenLabel": "Systems",
		"childLabel": "System"
	},
    {
		"id": ".01.03.",
		"slugName": "another",
		"name": "Another system group",
		"childrenLabel": "Systems",
		"childLabel": "System"
	}
]
  """

  @level_2_technical_details @nominal_case @valid
  Scenario: Update a hierarchy node
    Given the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
      | .01.01.02. | user       | User system          | Projects      | Project      |
      | .01.01.03. | search     | Search system        | Projects      | Project      |
      | .01.02.    | other      | Other system group   | Systems       | System       |
      | .01.03.    | another    | Another system group | Systems       | System       |
    When I perform a "PUT" on following URL "/api/hierarchy/.01.02." with json body
        """
{
	"id": ".01.02.",
	"slugName": "bakery",
    "name": "Bakery system group",
    "childrenLabel": "Systems",
    "childLabel": "System"
}
        """
    Then I get a response with status "200"
    And I get the following json response body
        """
{
	"id": ".01.02.",
	"slugName": "bakery",
	"name": "Bakery system group",
    "childrenLabel": "Systems",
    "childLabel": "System"
}
        """
    And the hierarchy nodes are now
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
      | .01.01.02. | user       | User system          | Projects      | Project      |
      | .01.01.03. | search     | Search system        | Projects      | Project      |
      | .01.02.    | bakery     | Bakery system group  | Systems       | System       |
      | .01.03.    | another    | Another system group | Systems       | System       |

  @level_2_technical_details @nominal_case @valid
  Scenario: Delete a hierarchy node
    Given the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
      | .01.01.02. | user       | User system          | Projects      | Project      |
      | .01.01.03. | search     | Search system        | Projects      | Project      |
      | .01.02.    | other      | Other system group   | Systems       | System       |
      | .01.03.    | another    | Another system group | Systems       | System       |
    When I perform a "DELETE" on following URL "/api/hierarchy/.01.03."
    Then I get a response with status "200"
    And the hierarchy nodes are now
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
      | .01.01.02. | user       | User system          | Projects      | Project      |
      | .01.01.03. | search     | Search system        | Projects      | Project      |
      | .01.02.    | other      | Other system group   | Systems       | System       |
