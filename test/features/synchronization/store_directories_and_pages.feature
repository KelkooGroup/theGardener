Feature: Retrieve documentation pages from a project on a remote server
  As a user,
  I want theGardener to retrieve all documentation pages of my project on a remote server
  So that I can access this documentation through theGardener


  Background:
    Given No project is checkout
    And the database is empty
    And the cache is empty
    And the remote projects are empty
    And we have the following projects
      | id            | name                    | repositoryUrl                               | stableBranch | documentationRootPath |
      | suggestionsWS | Suggestions WebServices | target/data/GetPages/library/suggestionsWS/ | master       | doc                   |
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/thegardener.json"
    """
{
  "directory" :
  {
    "label": "SuggestionsWS",
    "description": "Suggestions WebServices",
    "pages": [
      "context"
    ],
    "children" :[
      "suggestions",
      "admin"
    ]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/suggestions/thegardener.json"
    """
{
  "directory" :
  {
    "label": "Suggestions",
    "description": "Suggestions...",
    "pages": [
      "suggestion",
      "examples"
    ],
    "children" :[]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/admin/thegardener.json"
    """
{
  "directory" :
  {
    "label": "Admin",
    "description": "Administration...",
    "pages": [
      "admin"
    ],
    "children" :[]
  }
}
    """

  @level_1_specification @nominal_case @valid
  Scenario: retrieve pages and directories from a project
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
    And we have now those pages in the database
      | id | name       | label      | description | order | relativePath            | path                                         | markdown                              | directoryId |
      | 1  | context    | context    | context     | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 1           |
      | 2  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 2           |
      | 3  | examples   | examples   | examples    | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 2           |
      | 4  | admin      | admin      | admin       | 0     | /admin/admin            | suggestionsWS>master>/admin/admin            | **Page for the admin users**          | 3           |


  @level_1_specification @nominal_case @valid
  Scenario: retrieve updates from a project, update on a page
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    And the project "suggestionsWS" is synchronized
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/context.md"
    """
    **Feature**: Provide new awesome book suggestions
    """
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
    And we have now those pages in the database
      | id | name       | label      | description | order | relativePath            | path                                         | markdown                                          | directoryId |
      | 1  | context    | context    | context     | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide new awesome book suggestions | 1           |
      | 2  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**                         | 2           |
      | 3  | examples   | examples   | examples    | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**                      | 2           |
      | 4  | admin      | admin      | admin       | 0     | /admin/admin            | suggestionsWS>master>/admin/admin            | **Page for the admin users**                      | 3           |


  @level_1_specification @nominal_case @valid
  Scenario: retrieve updates from a project, add a directory
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    And the project "suggestionsWS" is synchronized
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/thegardener.json"
    """
{
  "directory" :
  {
    "label": "SuggestionsWS",
    "description": "Suggestions WebServices",
    "pages": [
      "context"
    ],
    "children" :[
      "suggestions",
      "admin",
      "changes"
    ]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/changes/thegardener.json"
    """
{
  "directory" :
  {
    "label": "Changes",
    "description": "Changes...",
    "pages": [
      "changelog"
    ],
    "children" :[]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/changes/changelog.md"
    """
** Change log **
    """
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 4  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 5  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 6  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
      | 7  | changes     | Changes       | Changes...              | 2     | /changes/     | suggestionsWS>master>/changes/     | 1        |
    And we have now those pages in the database
      | id | name       | label      | description | order | relativePath            | path                                         | markdown                              | directoryId |
      | 5  | context    | context    | context     | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 4           |
      | 6  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 5           |
      | 7  | examples   | examples   | examples    | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 5           |
      | 8  | admin      | admin      | admin       | 0     | /admin/admin            | suggestionsWS>master>/admin/admin            | **Page for the admin users**          | 6           |
      | 9  | changelog  | changelog  | changelog   | 0     | /changes/changelog      | suggestionsWS>master>/changes/changelog      | ** Change log **                      | 7           |


  @level_1_specification @nominal_case @valid
  Scenario: retrieve updates from a project, remove a directory
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    And the project "suggestionsWS" is synchronized
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/thegardener.json"
    """
{
  "directory" :
  {
    "label": "SuggestionsWS",
    "description": "Suggestions WebServices",
    "pages": [
      "context"
    ],
    "children" :[
      "suggestions"
    ]
  }
}
    """
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 4  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 5  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
    And we have now those pages in the database
      | id | name       | label      | description | order | relativePath            | path                                         | markdown                              | directoryId |
      | 5  | context    | context    | context     | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 4           |
      | 6  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 5           |
      | 7  | examples   | examples   | examples    | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 5           |

  @level_1_specification @nominal_case @valid
  Scenario: retrieve updates from a project, rename a directory
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    And the project "suggestionsWS" is synchronized
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/thegardener.json"
    """
{
  "directory" :
  {
    "label": "SuggestionsWS",
    "description": "Suggestions WebServices",
    "pages": [
      "context"
    ],
    "children" :[
      "suggestions",
      "administration"
    ]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/administration/thegardener.json"
    """
{
  "directory" :
  {
    "label": "Admin",
    "description": "Administration...",
    "pages": [
      "admin"
    ],
    "children" :[]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/administration/admin.md"
    """
**Page for the admin users**
    """
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name           | label         | description             | order | relativePath     | path                                  | branchId |
      | 4  | root           | SuggestionsWS | Suggestions WebServices | 0     | /                | suggestionsWS>master>/                | 1        |
      | 5  | suggestions    | Suggestions   | Suggestions...          | 0     | /suggestions/    | suggestionsWS>master>/suggestions/    | 1        |
      | 6  | administration | Admin         | Administration...       | 1     | /administration/ | suggestionsWS>master>/administration/ | 1        |
    And we have now those pages in the database
      | id | name       | label      | description | order | relativePath            | path                                         | markdown                              | directoryId |
      | 5  | context    | context    | context     | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 4           |
      | 6  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 5           |
      | 7  | examples   | examples   | examples    | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 5           |
      | 8  | admin      | admin      | admin       | 0     | /administration/admin   | suggestionsWS>master>/administration/admin   | **Page for the admin users**          | 6           |

  @level_1_specification @nominal_case @valid
  Scenario: retrieve updates from a project, rename a page
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    And the project "suggestionsWS" is synchronized
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/admin/thegardener.json"
    """
{
  "directory" :
  {
    "label": "Admin",
    "description": "Administration...",
    "pages": [
      "administration"
    ],
    "children" :[]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/admin/administration.md"
    """
**Page for the admin users**
    """
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 4  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 5  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 6  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
    And we have now those pages in the database
      | id | name           | label          | description    | order | relativePath            | path                                         | markdown                              | directoryId |
      | 5  | context        | context        | context        | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 4           |
      | 6  | suggestion     | suggestion     | suggestion     | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 5           |
      | 7  | examples       | examples       | examples       | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 5           |
      | 8  | administration | administration | administration | 0     | /admin/administration   | suggestionsWS>master>/admin/administration   | **Page for the admin users**          | 6           |


  @level_1_specification @nominal_case @valid
  Scenario: retrieve updates from a project, several changes at the same time
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    And the project "suggestionsWS" is synchronized
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/suggestions/examples.md"
    """
**Some suggestion with many examples**
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/thegardener.json"
    """
{
  "directory" :
  {
    "label": "SuggestionsWS",
    "description": "Suggestions WebServices",
    "pages": [
      "context"
    ],
    "children" :[
      "suggestions",
      "administration",
      "changes"
    ]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/administration/thegardener.json"
    """
{
  "directory" :
  {
    "label": "Admin",
    "description": "Administration...",
    "pages": [
      "administration"
    ],
    "children" :[]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/administration/administration.md"
    """
**Page for the admin users**
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/changes/thegardener.json"
    """
{
  "directory" :
  {
    "label": "Changes",
    "description": "Changes...",
    "pages": [
      "changelog"
    ],
    "children" :[]
  }
}
    """
    And the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/changes/changelog.md"
    """
** Change log **
    """
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name           | label         | description             | order | relativePath     | path                                  | branchId |
      | 4  | root           | SuggestionsWS | Suggestions WebServices | 0     | /                | suggestionsWS>master>/                | 1        |
      | 5  | suggestions    | Suggestions   | Suggestions...          | 0     | /suggestions/    | suggestionsWS>master>/suggestions/    | 1        |
      | 6  | administration | Admin         | Administration...       | 1     | /administration/ | suggestionsWS>master>/administration/ | 1        |
      | 7  | changes        | Changes       | Changes...              | 2     | /changes/        | suggestionsWS>master>/changes/        | 1        |
    And we have now those pages in the database
      | id | name           | label          | description    | order | relativePath                   | path                                                | markdown                               | directoryId |
      | 5  | context        | context        | context        | 0     | /context                       | suggestionsWS>master>/context                       | **Feature**: Provide book suggestions  | 4           |
      | 6  | suggestion     | suggestion     | suggestion     | 0     | /suggestions/suggestion        | suggestionsWS>master>/suggestions/suggestion        | **What's a suggestion ?**              | 5           |
      | 7  | examples       | examples       | examples       | 1     | /suggestions/examples          | suggestionsWS>master>/suggestions/examples          | **Some suggestion with many examples** | 5           |
      | 8  | administration | administration | administration | 0     | /administration/administration | suggestionsWS>master>/administration/administration | **Page for the admin users**           | 6           |
      | 9  | changelog      | changelog      | changelog      | 0     | /changes/changelog             | suggestionsWS>master>/changes/changelog             | ** Change log **                       | 7           |


  @level_1_specification @limit_case @valid
  Scenario: retrieve pages and directories from a project - some page or directories are missing - ignore them
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                        | content                               |
      | doc/context.md              | **Feature**: Provide book suggestions |
      | doc/suggestions/examples.md | **Some suggestion examples**          |
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
    And we have now those pages in the database
      | id | name     | label    | description | order | relativePath          | path                                       | markdown                              | directoryId |
      | 1  | context  | context  | context     | 0     | /context              | suggestionsWS>master>/context              | **Feature**: Provide book suggestions | 1           |
      | 2  | examples | examples | examples    | 1     | /suggestions/examples | suggestionsWS>master>/suggestions/examples | **Some suggestion examples**          | 2           |

  @level_1_specification @limit_case @valid
  Scenario: retrieve pages and directories from a project - some pages or directories exists but not listed - ignore them
    Given the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/suggestions/another.md    | **WIP What's a suggestion ?**         |
      | doc/admin/admin.md            | **Page for the admin users**          |
      | doc/admin/guide.md            | **WIP Admin guide**                   |
      | doc/ui/screenshots.md         | **WIP The screens**                   |
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    Then we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | relativePath  | path                               | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | suggestionsWS>master>/             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | suggestionsWS>master>/suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | suggestionsWS>master>/admin/       | 1        |
    And we have now those pages in the database
      | id | name       | label      | description | order | relativePath            | path                                         | markdown                              | directoryId |
      | 1  | context    | context    | context     | 0     | /context                | suggestionsWS>master>/context                | **Feature**: Provide book suggestions | 1           |
      | 2  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | suggestionsWS>master>/suggestions/suggestion | **What's a suggestion ?**             | 2           |
      | 3  | examples   | examples   | examples    | 1     | /suggestions/examples   | suggestionsWS>master>/suggestions/examples   | **Some suggestion examples**          | 2           |
      | 4  | admin      | admin      | admin       | 0     | /admin/admin            | suggestionsWS>master>/admin/admin            | **Page for the admin users**          | 3           |


  @level_1_specification @nominal_case @valid @meta
  Scenario: meta data of a page
    Given the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/context.md"
"""
```thegardener
{
  "page" :
     {
        "label": "The context",
        "description": "Why providing suggestions"
     }
}
```

**Feature**: Provide book suggestions
"""
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    And we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 1  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And we have now those pages in the database
      | id | name    | label       | description               | order | relativePath | path                          | markdown                                                                                                                                                                            | directoryId |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n{\n  "page" :\n     {\n        "label": "The context",\n        "description": "Why providing suggestions"\n     }\n}\n```\n\n**Feature**: Provide book suggestions | 1           |


  @level_1_specification @nominal_case @valid @meta
  Scenario: meta data of a page with other theGardener modules
    Given the server "target/data/GetPages" host under the project "library/suggestionsWS" on the branch "master" the file "doc/context.md"
"""
```thegardener
{
  "page" :
     {
        "label": "Write documentation",
        "description": "How to write documentation with theGardener format ?"
     }
}
```
```thegardener
    {
      "scenarios" :
         {
            "feature": "/page/show_a_page_with_variables.feature",
            "select": { "tags" : ["@nominal_case"]  }
         }
    }
```
"""
    When the synchronization action is triggered by the webhook for project "suggestionsWS"
    And we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name | label         | description             | order | relativePath | path                   | branchId |
      | 1  | root | SuggestionsWS | Suggestions WebServices | 0     | /            | suggestionsWS>master>/ | 1        |
    And we have now those pages in the database
      | id | name    | label               | description                                          | order | relativePath | path                          | markdown                                                                                                                                                                                                                                                                                                                                                                                 | directoryId |
      | 1  | context | Write documentation | How to write documentation with theGardener format ? | 0     | /context     | suggestionsWS>master>/context | ```thegardener\n{\n  "page" :\n     {\n        "label": "Write documentation",\n        "description": "How to write documentation with theGardener format ?"\n     }\n}\n```\n```thegardener\n    {\n      "scenarios" :\n         {\n            "feature": "/page/show_a_page_with_variables.feature",\n            "select": { "tags" : ["@nominal_case"]  }\n         }\n    }\n``` | 1           |
