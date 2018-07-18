Feature: Define hierarchy
  As an administrator,
  I want to define the projects hierarchy into theGardener
  So that BDD features can be well organized

  Background:
    Given the database is empty


  @level_2_technical_details @nominal_case @valid
  Scenario: Add a hierarchy node
    Given no hierarchy nodes is setup in theGardener
    When I perform a "POST" on following URL "/api/hierarchy" with json body
        """
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root"
}
        """
    Then I get a response with status "201"
    And I get the following json response body
        """
{
	"id": ".",
	"slugName": "root",
	"name": "Hierarchy root"
}
        """
    And the hierarchy nodes are now
      | id | slugName | name           |
      | .  | root     | Hierarchy root |

  @level_2_technical_details @nominal_case @valid
  Scenario: Get all hierarchy
    Given the hierarchy nodes are
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
      | .01.02.    | other      | Other system group   |
      | .01.03.    | another    | Another system group |
    When I perform a "GET" on following URL "/api/hierarchy"
    Then I get a response with status "200"
    And I get the following json response body
   """
[
    {
		"id": ".",
		"slugName": "root",
		"name": "Hierarchy root"
	},
    {
		"id": ".01.",
		"slugName": "eng",
		"name": "Engineering view"
	},
    {
		"id": ".01.01.",
		"slugName": "library",
		"name": "Library system group"
	},
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system"
	},
    {
		"id": ".01.01.02.",
		"slugName": "user",
		"name": "User system"
	},
    {
		"id": ".01.01.03.",
		"slugName": "search",
		"name": "Search system"
	},
    {
		"id": ".01.02.",
		"slugName": "other",
		"name": "Other system group"
	},
    {
		"id": ".01.03.",
		"slugName": "another",
		"name": "Another system group"
	}
]
  """

  @level_2_technical_details @nominal_case @valid
  Scenario: Update a hierarchy node
    Given the hierarchy nodes are
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
      | .01.02.    | other      | Other system group   |
      | .01.03.    | another    | Another system group |
    When I perform a "PUT" on following URL "/api/hierarchy/.01.02." with json body
        """
{
	"id": ".01.02.",
	"slugName": "bakery",
    "name": "Bakery system group"
}
        """
    Then I get a response with status "200"
    And I get the following json response body
        """
{
	"id": ".01.02.",
	"slugName": "bakery",
	"name": "Bakery system group"
}
        """
    And the hierarchy nodes are now
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
      | .01.02.    | bakery     | Bakery system group  |
      | .01.03.    | another    | Another system group |

  @level_2_technical_details @nominal_case @valid
  Scenario: Delete a hierarchy node
    Given the hierarchy nodes are
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
      | .01.02.    | bakery     | Bakery system group  |
      | .01.03.    | another    | Another system group |
    When I perform a "DELETE" on following URL "/api/hierarchy/.01.03."
    Then I get a response with status "200"
    And the hierarchy nodes are now
      | id         | slugName   | name                 |
      | .          | root       | Hierarchy root       |
      | .01.       | eng        | Engineering view     |
      | .01.01.    | library    | Library system group |
      | .01.01.01. | suggestion | Suggestion system    |
      | .01.01.02. | user       | User system          |
      | .01.01.03. | search     | Search system        |
      | .01.02.    | bakery     | Bakery system group  |
