Feature: Register a project
  As a user,
  I want to register my project into theGardener
  So that my project BDD features will be shared with all users

  Background:
    Given the database is empty
    And the cache is empty

  @level_1_specification @nominal_case @valid
  Scenario: setup a project
    Given no project settings are setup in theGardener
    And the root data path is "data/projects"
    When a user register a new project with
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    Then the projects settings are now
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |

  @level_2_technical_details @nominal_case @valid @register_project
  Scenario: setup a project
    Given no project settings are setup in theGardener
    When I perform a "POST" on following URL "/api/projects" with json body
        """
{
	"id": "suggestionsWS",
	"name": "Suggestions WebServices",
	"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
	"stableBranch": "master",
	"displayedBranches": "master",
    "featuresRootPath": "test/features",
    "documentationRootPath": "documentation"
}
        """
    Then I get a response with status "201"
    And I get the following json response body
       """
{
	"id": "suggestionsWS",
	"name": "Suggestions WebServices",
	"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
	"stableBranch": "master",
	"displayedBranches": "master",
    "featuresRootPath": "test/features",
    "documentationRootPath": "documentation"
}
     """
    Then the projects settings are now
      | id            | name                    | repositoryUrl                                        | stableBranch | displayedBranches | featuresRootPath | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | master            | test/features    | documentation         |


  @level_2_technical_details @nominal_case @valid
  Scenario: get a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    When I perform a "GET" on following URL "/api/projects/suggestionsWS"
    Then I get a response with status "200"
    And I get the following json response body
   """
{
     "id": "suggestionsWS",
     "name": "Suggestions WebServices",
     "repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
     "stableBranch": "master",
     "featuresRootPath": "test/features"
}
  """

  @level_2_technical_details @nominal_case @valid
  Scenario: get all projects
    Given we have the following projects
      | id             | name                     | repositoryUrl                                         | stableBranch | featuresRootPath |
      | suggestionsWS  | Suggestions WebServices  | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git  | master       | test/features    |
      | suggestionsWS2 | Suggestions WebServices2 | git@gitlab.corp.kelkoo.net:library/suggestionsWS2.git | master       | test/features    |
    When I perform a "GET" on following URL "/api/projects"
    Then I get a response with status "200"
    And  I get the following json response body
   """
[
    {
		"id": "suggestionsWS",
		"name": "Suggestions WebServices",
		"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
		"stableBranch": "master",
		"featuresRootPath": "test/features"
	},
	{
		"id": "suggestionsWS2",
		"name": "Suggestions WebServices2",
		"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS2.git",
		"stableBranch": "master",
		"featuresRootPath": "test/features"
	}
]
  """

  @level_2_technical_details @nominal_case @valid
  Scenario: update a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    When I perform a "PUT" on following URL "/api/projects/suggestionsWS" with json body
    """
{
    	"id": "suggestionsWS",
        "name": "Suggestions WebServices1",
        "repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
        "stableBranch": "master",
        "featuresRootPath": "test/features"
}
   """
    Then I get a response with status "200"
    And I get the following json response body
       """
{
     "id": "suggestionsWS",
     "name": "Suggestions WebServices1",
     "repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
     "stableBranch": "master",
     "featuresRootPath": "test/features"
}
  """
    And the projects settings are now
      | id            | name                     | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices1 | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |

  @level_2_technical_details @nominal_case @valid
  Scenario: delete a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    When I perform a "DELETE" on following URL "/api/projects/suggestionsWS"
    Then I get a response with status "200"
    And  the projects settings are now
      | id | name | repositoryUrl | stableBranch | featuresRootPath |


