Feature: Register a project
  As a user,
  I want to register my project into theGardener
  So that my project BDD features will be shared with all users

  @level_0_high_level @nominal_case @valid
  Scenario: register a project
    Given a git server that host a project
    When a user register this project in theGardener
    Then those project settings are stored in theGardener system


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

  @level_2_technical_details @nominal_case @valid
  Scenario: setup a project
    Given no project settings are setup in theGardener
    When I perform a POST on following URL "/api/project"
        """
{
	"id": "suggestionsWS",
	"name": "Suggestions WebServices",
	"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
	"stableBranch": "master",
	"featuresRootPath": "test/features"
}
        """
    Then I get a response with status "201"
    And  I get the following json response body
       """
{
	"id": "suggestionsWS",
	"name": "Suggestions WebServices",
	"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
	"stableBranch": "master",
	"featuresRootPath": "test/features"
}
     """
    Then the projects settings are now
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |


  @level_2_technical_details @nominal_case @valid
  Scenario: Get a project
    Given   we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    |
    When    I perform a GET on following URL "/api/project/suggestionsWS"
    Then    I get a response with status "200"
    And     I get the following json response body
   """
{
     "id": "suggestionsWS",
     "name": "Suggestions WebServices",
     "repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
     "stableBranch": "master",
     "featuresRootPath": "test/features"

}
   """



