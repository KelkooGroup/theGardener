Feature: Provide menu

  Background:
    Given the database is empty
    And the cache is empty

  @level_2_technical_details @nominal_case @valid @documentation
  Scenario: provide menu
    Given the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   | directoryPath       |
      | .          | root       | Hierarchy root       | Views         | View         |                     |
      | .01.       | eng        | Engineering view     | System groups | System group |                     |
      | .02.       | biz        | Business view        | Units         | Unit         |                     |
      | .01.01.    | library    | Library system group | Systems       | System       | libraryDoc>master>/ |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |                     |
      | .01.01.02. | user       | User system          | Projects      | Project      |                     |
      | .01.01.03. | search     | Search system        | Projects      | Project      |                     |
    And we have the following projects
      | id                 | name                                | repositoryUrl                                              | stableBranch | featuresRootPath |
      | suggestionsWS      | Suggestions WebServices             | target/remote/data/GetFeatures/library/suggestionsWS/      | master       | test/features    |
      | suggestionsReports | Suggestions Reports                 | target/remote/data/GetFeatures/library/suggestionsReports/ | master       | test/features    |
      | usersWS            | Users WebServices                   | target/remote/data/GetFeatures/library/usersWS/            | master       | test/features    |
      | libraryDoc         | library  system group documentation | target/remote/data/GetFeatures/library/libraryDoc/         | master       |                  |
    And the links between hierarchy nodes are
      | projectId          | hierarchyId |
      | suggestionsWS      | .01.01.01.  |
      | suggestionsReports | .01.01.01.  |
      | usersWS            | .01.01.02.  |
      | usersWS            | .02.        |
    And we have those branches in the database
      | id | name       | isStable | projectId          |
      | 1  | master     | true     | suggestionsWS      |
      | 2  | bugfix/351 | false    | suggestionsWS      |
      | 3  | master     | true     | suggestionsReports |
      | 4  | master     | true     | usersWS            |
      | 5  | master     | true     | libraryDoc         |
    And we have those directories in the database
      | id | name        | label         | description                        | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices            | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...                     | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...                  | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
      | 4  | libraryDoc  | libraryDoc    | library system group documentation | 0     | /             | libraryDoc>master>/                | 5        |
    When I perform a "GET" on following URL "/api/menu"
    Then I get a response with status "200"
    And  I get the following json response body
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
          "directory" : {
              "id": 4,
              "path": "libraryDoc>master>/",
              "name": "libraryDoc",
              "label": "libraryDoc",
              "description": "library system group documentation",
              "order": 0,
              "pages": []
          },
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
                        "children": [
                          {
                            "id": 2,
                            "path": "suggestionsWS>master>/suggestions/",
                            "name": "suggestions",
                            "label": "Suggestions",
                            "description": "Suggestions...",
                            "order": 0,
                            "children": []
                          },
                          {
                            "id": 3,
                            "path": "suggestionsWS>master>/admin/",
                            "name": "admin",
                            "label": "Admin",
                            "description": "Administration...",
                            "order": 1,
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
              ],
              "children": []
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
              ],
              "children": []
            },
            {
              "id": ".01.01.03.",
              "hierarchy": "_eng_library_search",
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
      "hierarchy": "_biz",
      "slugName": "biz",
      "name": "Business view",
      "childrenLabel": "Units",
      "childLabel": "Unit",
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
      ],
      "children": []
    }
  ]
}

"""

  @level_2_technical_details @nominal_case @valid
  Scenario: provide menu - filter branches by a regexp
    Given the hierarchy nodes are
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
    Then I get a response with status "200"
    And  I get the following json response body
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
                      "name": "version_2.0.1",
                      "path": "suggestionsWS>version_2.0.1"
                    },
                    {
                      "name": "version_1.0.0",
                      "path": "suggestionsWS>version_1.0.0"
                    }
                  ]
                }
              ],
              "children": []
            }
          ]
        }
      ]
    }
  ]
}
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: provide menu header
    Given the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   | directoryPath     |
      | .          | root       | Hierarchy root       | Views         | View         | meta>master>/     |
      | .01.       | eng        | Engineering view     | System groups | System group | meta>master>/eng/ |
      | .02.       | biz        | Business view        | Units         | Unit         |                   |
      | .01.01.    | library    | Library system group | Systems       | System       |                   |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |                   |
      | .01.01.02. | user       | User system          | Projects      | Project      |                   |
      | .01.01.03. | search     | Search system        | Projects      | Project      |                   |
    And we have the following projects
      | id   | name | repositoryUrl                                | stableBranch | featuresRootPath |
      | meta | meta | target/remote/data/GetFeatures/library/meta/ | master       |                  |
    And we have those branches in the database
      | id | name   | isStable | projectId |
      | 1  | master | true     | meta      |
    And we have those directories in the database
      | id | name | label | description | order | relativePath | path              | branchId |
      | 1  | root | root  | root        | 0     | /            | meta>master>/     | 1        |
      | 2  | eng  | eng   | eng         | 0     | /eng/        | meta>master>/eng/ | 1        |
    When I perform a "GET" on following URL "/api/menu/header"
    Then I get a response with status "200"
    And  I get the following json response body
"""
{
  "id": ".",
  "hierarchy": "_",
  "slugName": "root",
  "name": "Hierarchy root",
  "childrenLabel": "Views",
  "childLabel": "View",
  "directory" : {
      "id": 1,
      "path": "meta>master>/",
      "name": "root",
      "label": "root",
      "description": "root",
      "order": 0,
      "pages": []
  },
  "children": [
    {
      "id": ".01.",
      "hierarchy": "_eng",
      "slugName": "eng",
      "name": "Engineering view",
      "childrenLabel": "System groups",
      "childLabel": "System group",
      "directory" : {
        "id": 2,
        "path": "meta>master>/eng/",
        "name": "eng",
        "label": "eng",
        "description": "eng",
        "order": 0,
        "pages": []
      }
    },
    {
      "id": ".02.",
      "hierarchy": "_biz",
      "slugName": "biz",
      "name": "Business view",
      "childrenLabel": "Units",
      "childLabel": "Unit"
    }
  ]
}
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: provide sub menu
    Given the hierarchy nodes are
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
      | suggestionsReports | .01.01.01.  |
      | usersWS            | .01.01.02.  |
      | usersWS            | .02.        |
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
    When I perform a "GET" on following URL "/api/menu/submenu/_eng"
    Then I get a response with status "200"
    And I get the following json response body
"""
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
                    "children": [
                      {
                        "id": 2,
                        "path": "suggestionsWS>master>/suggestions/",
                        "name": "suggestions",
                        "label": "Suggestions",
                        "description": "Suggestions...",
                        "order": 0,
                        "children": []
                      },
                      {
                        "id": 3,
                        "path": "suggestionsWS>master>/admin/",
                        "name": "admin",
                        "label": "Admin",
                        "description": "Administration...",
                        "order": 1,
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
          ],
          "children": []
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
          ],
          "children": []
        },
        {
          "id": ".01.01.03.",
          "hierarchy": "_eng_library_search",
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
}
"""

  @level_2_technical_details @nominal_case @valid
  Scenario: provide pages of a directory
    Given the hierarchy nodes are
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
      | suggestionsReports | .01.01.01.  |
      | usersWS            | .01.01.02.  |
      | usersWS            | .02.        |
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
    When I perform a "GET" on following URL "/api/directories?path=suggestionsWS>master>/suggestions/"
    Then I get a response with status "200"
    And  I get the following json response body
    """
[
  {
    "id": 2,
    "path": "suggestionsWS>master>/suggestions/",
    "name": "suggestions",
    "label": "Suggestions",
    "description": "Suggestions...",
    "order": 0,
    "pages": [
      {
        "path": "suggestionsWS>master>/suggestions/suggestion",
        "relativePath": "/suggestions/suggestion",
        "name": "suggestion",
        "label": "suggestion",
        "description": "suggestion",
        "order": 0,
        "content":[]
      },
      {
        "path": "suggestionsWS>master>/suggestions/examples",
        "relativePath": "/suggestions/examples",
        "name": "examples",
        "label": "examples",
        "description": "examples",
        "order": 1,
        "content":[]
      }
    ]
  }
]
    """

  @level_2_technical_details @nominal_case @valid
  Scenario: provide pages of a directory
    Given the hierarchy nodes are
      | id         | slugName   | name                 | childrenLabel | childLabel   |
      | .          | root       | Hierarchy root       | Views         | View         |
      | .01.       | eng        | Engineering view     | System groups | System group |
      | .02.       | biz        | Business view        | Units         | Unit         |
      | .01.01.    | library    | Library system group | Systems       | System       |
      | .01.01.01. | suggestion | Suggestion system    | Projects      | Project      |
    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | displayedBranches | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | master            | test/features    |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.01.01.  |
    And we have those branches in the database
      | id | name       | isStable | projectId     |
      | 1  | master     | true     | suggestionsWS |
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
    When I perform a "GET" on following URL "/api/directories?path=suggestionsWS>master>/suggestions/"
    Then I get a response with status "200"
    And  I get the following json response body
    """
[
  {
    "id": 2,
    "path": "suggestionsWS>>/suggestions/",
    "name": "suggestions",
    "label": "Suggestions",
    "description": "Suggestions...",
    "order": 0,
    "pages": [
      {
        "path": "suggestionsWS>>/suggestions/suggestion",
        "relativePath": "/suggestions/suggestion",
        "name": "suggestion",
        "label": "suggestion",
        "description": "suggestion",
        "order": 0,
        "content":[]
      },
      {
        "path": "suggestionsWS>>/suggestions/examples",
        "relativePath": "/suggestions/examples",
        "name": "examples",
        "label": "examples",
        "description": "examples",
        "order": 1,
        "content":[]
      }
    ]
  }
]
    """+

  @level_2_technical_details @nominal_case @valid
  Scenario: provide menu without branch name in directory path
    Given the hierarchy nodes are
      | id   | slugName | name             | childrenLabel | childLabel   | directoryPath |
      | .    | root     | Hierarchy root   | Views         | View         |               |
      | .01. | eng      | Engineering view | System groups | System group |               |

    And we have the following projects
      | id            | name                    | repositoryUrl                                         | stableBranch | displayedBranches | featuresRootPath |
      | suggestionsWS | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/ | master       | master            | test/features    |
    And the links between hierarchy nodes are
      | projectId     | hierarchyId |
      | suggestionsWS | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
    When I perform a "GET" on following URL "/api/menu"
    Then I get a response with status "200"
    And  I get the following json response body
"""
 {"id":".",
 "hierarchy":"_",
 "slugName":"root",
 "name":"Hierarchy root",
 "childrenLabel":"Views",
 "childLabel":"View",
 "projects":[],
 "children":[{
                "id":".01.",
                "hierarchy":"_eng",
                "slugName":"eng",
                "name":"Engineering view",
                "childrenLabel":"System groups",
                "childLabel":"System group",
                "projects":[{
                              "id":"suggestionsWS",
                              "path":"suggestionsWS",
                              "label":"Suggestions WebServices",
                              "stableBranch":"master",
                              "branches":[{
                                            "name":"master",
                                            "path":"suggestionsWS>master",
                                            "rootDirectory":{
                                                              "id":1,
                                                              "path":"suggestionsWS>>/",
                                                              "name":"root",
                                                              "label":"SuggestionsWS",
                                                              "description":"Suggestions WebServices",
                                                              "order":0,
                                                              "children":[{
                                                                            "id":2,
                                                                            "path":"suggestionsWS>>/suggestions/",
                                                                            "name":"suggestions",
                                                                            "label":"Suggestions",
                                                                            "description":"Suggestions...",
                                                                            "order":0,
                                                                            "children":[]
                                                                          },
                                                                          {
                                                                           "id":3,
                                                                           "path":"suggestionsWS>>/admin/",
                                                                           "name":"admin",
                                                                           "label":"Admin",
                                                                           "description":"Administration...",
                                                                           "order":1,
                                                                           "children":[]
                                                                          }]
                                                             }
                                           }]
                             }],
                "children":[]
               }]
 }
"""

