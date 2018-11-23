Feature: Provide criterias

  @level_2_technical_details @nominal_case @valid
  Scenario: provide criterias - json output
    Given the database is empty
    And the cache is empty
    And the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .02.       | biz        | Business view        | Units         | Unit         |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
      | .01.01.02. | user       | User system          | Projects      | Project      |
      | .01.01.03. | search     | Search system        | Projects      | Project      |

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
    And we have those branches in the database
      | id | name       | isStable | projectId          |
      | 1  | master     | true     | suggestionsWS      |
      | 2  | bugfix/351 | false    | suggestionsWS      |
      | 3  | master     | true     | suggestionsReports |
      | 4  | master     | true     | usersWS            |
    When I perform a "GET" on following URL "/api/criterias"
    Then I get the following json response body
"""
[
   {
      "id":".",
      "slugName":"root",
      "name":"Hierarchy root",
      "childrenLabel":"Views",
      "childLabel":"View",
      "projects":[]
   },
   {
      "id":".01.",
      "slugName":"eng",
      "name":"Engineering view",
      "childrenLabel":"System groups",
      "childLabel":"System group",
      "projects":[]
   },
   {
      "id":".01.01.",
      "slugName":"library",
      "name":"Library system group",
      "childrenLabel":"Systems",
      "childLabel":"System",
      "projects":[]
   },
   {
      "id":".01.01.01.",
      "slugName":"suggestion",
      "name":"Suggestion system",
      "childrenLabel":"Projects",
      "childLabel":"Project",
      "projects":[
         {
            "id":"suggestionsReports",
            "label":"Suggestions Reports",
            "stableBranch":"master",
            "branches":[
               "master"
            ]
         },
         {
            "id":"suggestionsWS",
            "label":"Suggestions WebServices",
            "stableBranch":"master",
            "branches":[
               "master",
               "bugfix/351"
            ]
         }
      ]
   },
   {
      "id":".01.01.02.",
      "slugName":"user",
      "name":"User system",
      "childrenLabel":"Projects",
      "childLabel":"Project",
      "projects":[
         {
            "id":"usersWS",
            "label":"Users WebServices",
            "stableBranch":"master",
            "branches":[
               "master"
            ]
         }
      ]
   },
   {
      "id":".01.01.03.",
      "slugName":"search",
      "name":"Search system",
      "childrenLabel":"Projects",
      "childLabel":"Project",
      "projects":[]
   },
   {
      "id":".02.",
      "slugName":"biz",
      "name":"Business view",
      "childrenLabel":"Units",
      "childLabel":"Unit",
      "projects":[
         {
            "id":"suggestionsWS",
            "label":"Suggestions WebServices",
            "stableBranch":"master",
            "branches":[
               "master",
               "bugfix/351"
            ]
         }
      ]
   }
]
"""