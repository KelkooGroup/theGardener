Feature: Provide criterias


  @level_2_technical_details @nominal_case @valid
  Scenario: provide menu - json output
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
    And we have those features in the database
      | id | path                                                    | name | description | branchId | keyword  |
      | 1  | test/features/provide/provide_book_suggestions.feature  |      |             | 1        | Scenario |
      | 2  | test/features/provide/provide_other_suggestions.feature |      |             | 1        | Scenario |
      | 3  | test/features/admin/admin_book_suggestions.feature      |      |             | 1        | Scenario |
    When I perform a "GET" on following URL "/api/menu"
    Then I get the following json response body
"""
{
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [],
  "children": [
    {
      "id": ".01.",
      "slugName": "eng",
      "name": "Engineering view",
      "childrenLabel": "System groups",
      "childLabel": "System group",
      "projects": [],
      "children": [
        {
          "id": ".01.01.",
          "slugName": "library",
          "name": "Library system group",
          "childrenLabel": "Systems",
          "childLabel": "System",
          "projects": [],
          "children": [
            {
              "id": ".01.01.01.",
              "slugName": "suggestion",
              "name": "Suggestion system",
              "childrenLabel": "Projects",
              "childLabel": "Project",
              "projects": [
                {
                  "id": "suggestionsReports",
                  "label": "Suggestions Reports",
                  "stableBranch": "master",
                  "branches": [
                    {
                      "name": "master",
                      "features": []
                    }
                  ]
                },
                {
                  "id": "suggestionsWS",
                  "label": "Suggestions WebServices",
                  "stableBranch": "master",
                  "branches": [
                    {
                      "name": "master",
                      "features": [
                        "admin/admin_book_suggestions.feature",
                        "provide/provide_book_suggestions.feature",
                        "provide/provide_other_suggestions.feature"
                      ]
                    },
                    {
                      "name": "bugfix/351",
                      "features": []
                    }
                  ]
                }
              ],
              "children": []
            },
            {
              "id": ".01.01.02.",
              "slugName": "user",
              "name": "User system",
              "childrenLabel": "Projects",
              "childLabel": "Project",
              "projects": [
                {
                  "id": "usersWS",
                  "label": "Users WebServices",
                  "stableBranch": "master",
                  "branches": [
                    {
                      "name": "master",
                      "features": []
                    }
                  ]
                }
              ],
              "children": []
            },
            {
              "id": ".01.01.03.",
              "slugName": "search",
              "name": "Search system",
              "childrenLabel": "Projects",
              "childLabel": "Project",
              "projects": [],
              "children": []
            }
          ]
        }
      ]
    },
    {
      "id": ".02.",
      "slugName": "biz",
      "name": "Business view",
      "childrenLabel": "Units",
      "childLabel": "Unit",
      "projects": [
        {
          "id": "suggestionsWS",
          "label": "Suggestions WebServices",
          "stableBranch": "master",
          "branches": [
            {
              "name": "master",
              "features": [
                "admin/admin_book_suggestions.feature",
                "provide/provide_book_suggestions.feature",
                "provide/provide_other_suggestions.feature"
              ]
            },
            {
              "name": "bugfix/351",
              "features": []
            }
          ]
        }
      ],
      "children": []
    }
  ]
}
"""

## TODO  Add hierarchy (concat of hierarchy slugname) and path (path from the entity project)
  # Will replace the previous resource to serve directories in a tree with a selector by element
  # The field pathFromProject will be used to access to pages

## TODO Once the scenario are implemented we can factories steps with background steps
  @level_2_technical_details @nominal_case @ready
  Scenario: provide menu - json output
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
    And we have those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
    And we have those pages in the database
      | id | name       | label      | description | order | relativePath            | path                                         | markdown                              | directoryId |
      | 1  | context    | context    | context     | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 1           |
      | 2  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 2           |
      | 3  | examples   | examples   | examples    | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 2           |
      | 4  | admin      | admin      | admin       | 0     | /admin/admin            | suggestionsWS>master>/admin/admin            | **Page for the admin users**          | 3           |

    When I perform a "GET" on following URL "/api/menu"
    Then I get the following json response body
"""
{
  "id": ".",
  "hierarchy": "_",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "projects": [],
  "children": [
    {
      "id": ".01.",
      "hierarchy": "_eng",
      "slugName": "eng",
      "name": "Engineering view",
      "childrenLabel": "System groups",
      "childLabel": "System group",
      "projects": [],
      "children": [
        {
          "id": ".01.01.",
          "hierarchy": "_eng_library",
          "slugName": "library",
          "name": "Library system group",
          "childrenLabel": "Systems",
          "childLabel": "System",
          "projects": [],
          "children": [
            {
              "id": ".01.01.01.",
              "hierarchy": "_eng_library_suggestion",
              "slugName": "suggestion",
              "name": "Suggestion system",
              "childrenLabel": "Projects",
              "childLabel": "Project",
              "projects": [
                {
                  "id": "suggestionsReports",
                  "path": "suggestionsReports",
                  "label": "Suggestions Reports",
                  "stableBranch": "master",
                  "branches": [
                    {
                      "name": "master",
                      "path": "suggestionsReports>master"
                    }
                  ]
                },
                {
                  "id": "suggestionsWS",
                  "path": "suggestionsWS",
                  "label": "Suggestions WebServices",
                  "stableBranch": "master",
                  "branches": [
                    {
                      "name": "master",
                      "path": "suggestionsWS>master",
                      "rootDirectory": {
                        "id": 1,
                        "path": "suggestionsWS>master>/",
                        "name": "root",
                        "label": "SuggestionsWS",
                        "description": "Suggestions WebServices",
                        "order": 0,
                        "path": "/",
                        "pages": [
                           "context"
                        ],
                        "children": [
                          {
                            "id": 2,
                            "path": "suggestionsWS>master>/suggestions/",
                            "name": "suggestions",
                            "label": "Suggestions",
                            "description": "Suggestions...",
                            "order": 0,
                            "path": "/suggestions/",
                            "pages": [
                               "suggestionsWS>master>/suggestions/suggestion",
                               "suggestionsWS>master>/suggestions/examples"
                            ],
                            "children": []
                          },
                          {
                            "id": 3,
                            "path": "suggestionsWS>master>/admin/",
                            "name": "admin",
                            "label": "Admin",
                            "description": "Administration...",
                            "order": 1,
                            "path": "/admin/",
                            "pages": [
                               "suggestionsWS>master>/admin/admin"
                            ],
                            "children": []
                          }
                        ]
                      }
                    },
                    {
                      "name": "bugfix/351",
                      "path": "suggestionsWS>bugfix/351"
                    }
                  ]
                }
              ]
            },
            {
              "id": ".01.01.02.",
              "hierarchy": "_eng_library_user",
              "slugName": "user",
              "name": "User system",
              "childrenLabel": "Projects",
              "childLabel": "Project",
              "projects": [
                {
                  "id": "usersWS",
                  "path": "usersWS",
                  "label": "Users WebServices",
                  "stableBranch": "master",
                  "branches": [
                    {
                      "name": "master",
                      "path": "usersWS>master"
                    }
                  ]
                }
              ]
            },
            {
              "id": ".01.01.03.",
              "hierarchy": "_eng_library_search",
              "slugName": "search",
              "name": "Search system",
              "childrenLabel": "Projects",
              "childLabel": "Project",
              "projects": []
            }
          ]
        }
      ]
    },
    {
      "id": ".02.",
      "hierarchy": "_eng_library_biz",
      "slugName": "biz",
      "name": "Business view",
      "childrenLabel": "Units",
      "childLabel": "Unit",
      "projects": [
        {
          "id": "suggestionsWS",
          "path": "suggestionsWS",
          "label": "Suggestions WebServices",
          "stableBranch": "master",
          "branches": [
            {
              "name": "master",
              "path": "suggestionsWS>master"
            },
            {
              "name": "bugfix/351",
              "path": "suggestionsWS>bugfix/351"
            }
          ]
        }
      ],
      "children": []
    }
  ]
}
"""

## TODO Once the scenario are implemented we can factories steps with background steps
  @level_1_specification @nominal_case @ready
  Scenario: provide menu - filter branches by a regexp
    Given the database is empty
    And the cache is empty
    And the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | displayedBranches  | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | master\|version_.* | test/features    |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
    And we have those branches in the database
      | id | name          | isStable | projectId     |
      | 1  | master        | true     | suggestionsWS |
      | 2  | version_2.0.1 | false    | suggestionsWS |
      | 3  | bugfix/3135   | false    | suggestionsWS |
      | 4  | version_1.0.0 | false    | suggestionsWS |
      | 5  | bugfix/351    | false    | suggestionsWS |
    When I perform a "GET" on following URL "/api/menu"
    Then I get the following branches under the project "suggestionsWS"
      | id | name          |
      | 1  | master        |
      | 2  | version_2.0.1 |
      | 4  | version_1.0.0 |


  @level_2_technical_details @nominal_case @ready
  Scenario: provide menu - pages on menu - json output
    Given the database is empty
    And the cache is empty
    And the hierarchy nodes are
      | id      | slugName | name                 | childrenLabel | childLabel   | pagesSelector                            |
      | .       | root     | Hierarchy root       | Views         | View         |                                          |
      | .01.    | eng      | Engineering view     | System groups | System group | theGardenerPages>master>/eng/engineering |
      | .01.01. | library  | Library system group | Systems       | System       | theGardenerPages>master>/library/        |
    And we have the following projects
      | id               | name                        | repositoryUrl                               | stableBranch | featuresRootPath |
      | theGardenerPages | Pages for theGardener nodes | target/remote/data/GetMenu/theGardenerPages | master       | doc              |
    And we have those branches in the database
      | id | name   | isStable | projectId        |
      | 1  | master | true     | theGardenerPages |
    And we have those directories in the database
      | id | name    | label | description | order | relativePath | path                              | branchId |
      | 1  | root    |       |             | 0     | /            | theGardenerPages>master>/         | 1        |
      | 2  | eng     |       |             | 0     | /eng/        | theGardenerPages>master>/eng/     | 1        |
      | 3  | library |       |             | 0     | /library/    | theGardenerPages>master>/library/ | 1        |
    And we have those pages in the database
      | id | name        | label       | description     | order | relativePath            | path                                     | markdown               | directoryId |
      | 1  | engineering | Engineering | The Engineering | 0     | /context                | theGardenerPages>master>/eng/engineering | **engineering**        | 2           |
      | 2  | suggestion  | suggestion  | suggestion      | 0     | /suggestions/suggestion | theGardenerPages>master>/eng/context    | **What's a library ?** | 3           |
      | 3  | examples    | examples    | examples        | 1     | /suggestions/examples   | theGardenerPages>master>/eng/examples   | **Some book examples** | 3           |
    When I perform a "GET" on following URL "/api/menu"
    Then I get the following json response body
"""
{
  "id": ".",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "pages": [
    "theGardenerPages>master>/eng/engineering"
  ],
  "projects": [],
  "children": [
    {
      "id": ".01.",
      "slugName": "eng",
      "name": "Engineering view",
      "childrenLabel": "System groups",
      "childLabel": "System group",
      "pages": [
        "theGardenerPages>master>/eng/context",
        "theGardenerPages>master>/eng/examples"
      ],
      "projects": [],
      "children": [
        {
          "id": ".01.01.",
          "slugName": "library",
          "name": "Library system group",
          "childrenLabel": "Systems",
          "childLabel": "System",
          "pages": [],
          "projects": [],
          "children": []
        }
      ]
    }
  ]
}
"""
