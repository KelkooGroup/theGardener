Feature: Retrieve documentation pages from a project on a remote server
  As a user,
  I want theGardener to retrieve all documentation pages of my project on a remote server
  So that I can access this documentation through theGardener


  Background:
    Given No project is checkout
    And the database is empty
    And the cache is empty
    And the remote projects are empty


  @level_1_specification @nominal_case @ready
  Scenario: retrieve pages and directories from a project
    Given we have the following projects
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
    And the server "target/data/GetPages/" host under the project "library/suggestionsWS" on the branch "master" the files
      | file                          | content                               |
      | doc/context.md                | **Feature**: Provide book suggestions |
      | doc/suggestions/suggestion.md | **What's a suggestion ?**             |
      | doc/suggestions/examples.md   | **Some suggestion examples**          |
      | doc/admin/admin.md            | **Page for the admin users**          |
    When synchronization action is triggered
    And we have now those branches in the database
      | id | name   | isStable | projectId     |
      | 1  | master | true     | suggestionsWS |
    And we have now those directories in the database
      | id | name        | label         | description             | order | path          | branchId |
      | 1  | root        | SuggestionsWS | Suggestions WebServices | 0     | /             | 1        |
      | 2  | suggestions | Suggestions   | Suggestions...          | 0     | /suggestions/ | 1        |
      | 3  | admin       | Admin         | Administration...       | 1     | /admin/       | 1        |
    And we have now those pages in the database
      | id | name       | label      | description | order | path                    | markdown                              | directoryId |
      | 1  | context    | context    | context     | 0     | /context                | **Feature**: Provide book suggestions | 1           |
      | 2  | suggestion | suggestion | suggestion  | 0     | /suggestions/suggestion | **What's a suggestion ?**             | 2           |
      | 3  | examples   | examples   | examples    | 1     | /suggestions/examples   | **Some suggestion examples**          | 2           |
      | 4  | admin      | admin      | admin       | 0     | /admin/admin            | **Page for the admin users**          | 3           |



# TODO Add nominal case with page label and description defined in .md
# TODO Add limit case when a md file exists and not listed in the thegardener.json => not stored
# TODO Add limit case when a page listed in the thegardener.json do not refer to an existing file  => ignore, log a warning