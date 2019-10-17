Feature: get and update variables of a project

  Background:
    Given the database is empty
    And the cache is empty

  @level_2_technical_details @nominal_case @valid
  Scenario: get variables from a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath | documentationRootPath | variables                                                                                                             |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    | doc                   | [{"name":"${swagger.json}","value":"http://localhost:9000/docs/swagger.json"},{"name":"${desc}","value":"Variables"}] |
    When I perform a "GET" on following URL "/api/projects/suggestionsWS/variables"
    Then I get a response with status "200"
    And I get the following json response body
   """
[
  {
    "name":"${swagger.json}",
    "value":"http://localhost:9000/docs/swagger.json"
  },
  {
    "name":"${desc}",
    "value":"Variables"
  }
]
  """

  @level_2_technical_details @nominal_case @valid
  Scenario: update variables of a project
    Given we have the following projects
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath | documentationRootPath | variables                                                                      |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    | doc                   | [{"name":"${swagger.json}","value":"http://localhost:9000/docs/swagger.json"}] |
    When I perform a "POST" on following URL "/api/projects/suggestionsWS/variables" with json body
        """
[
  {
    "name":"${desc}",
    "value":"Variables"
  }
]
        """
    Then I get a response with status "200"
    And I get the following json response body
       """
{
	"id": "suggestionsWS",
	"name": "Suggestions WebServices",
	"repositoryUrl": "git@gitlab.corp.kelkoo.net:library/suggestionsWS.git",
	"stableBranch": "master",
	"featuresRootPath": "test/features",
	"documentationRootPath": "doc",
	"variables": [{"name":"${swagger.json}","value":"http://localhost:9000/docs/swagger.json"},{"name":"${desc}","value":"Variables"}]
}
     """
    Then the projects settings are now
      | id            | name                    | repositoryUrl                                        | stableBranch | featuresRootPath | documentationRootPath | variables                                                                                                             |
      | suggestionsWS | Suggestions WebServices | git@gitlab.corp.kelkoo.net:library/suggestionsWS.git | master       | test/features    | doc                   | [{"name":"${swagger.json}","value":"http://localhost:9000/docs/swagger.json"},{"name":"${desc}","value":"Variables"}] |
