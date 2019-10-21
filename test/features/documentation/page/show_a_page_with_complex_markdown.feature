Feature: Generate a documentation page with a complex markdown

  Background:
    Given the database is empty
    And the cache is empty
    And No project is checkout
    And the remote projects are empty
    And the hierarchy nodes are
      | id   | slugName   | name              | childrenLabel | childLabel |
      | .    | root       | Hierarchy root    | Views         | View       |
      | .01. | suggestion | Suggestion system | Projects      | Project    |
    And we have the following projects
      | id                 | name                    | repositoryUrl                                              | stableBranch | featuresRootPath | documentationRootPath |
      | suggestionsWS      | Suggestions WebServices | target/remote/data/GetFeatures/library/suggestionsWS/      | master       | test/features    | doc                   |
      | suggestionsReports | Suggestions Reports     | target/remote/data/GetFeatures/library/suggestionsReports/ | master       | test/features    | doc                   |
    And the links between hierarchy nodes are
      | projectId          | hierarchyId |
      | suggestionsWS      | .01.        |
      | suggestionsReports | .01.        |
    And we have those branches in the database
      | id | name   | isStable | projectId          |
      | 1  | master | true     | suggestionsWS      |
      | 2  | master | true     | suggestionsReports |
    And the server "target/remote/data/GetFeatures" host under the project "library/suggestionsWS" on the branch "master" the file "test/features/provide_book_suggestions.feature"
    """
Feature: As a user Tim, I want some book suggestions so that I can do some discovery

  @level_0_high_level @nominal_case @draft
  Scenario: providing several book suggestions
    Given a user
    When we ask for suggestions
    Then the suggestions are popular and available books adapted to the age of the user

  @level_1_specification @error_case @valid
  Scenario: one service on which the suggestion system depends on is down
      Given the user "Tim"
      And impossible to get information on the user
      When we ask for "3" suggestions from "2" different categories
      Then the system is temporary not available

  @level_1_specification @nominal_case @valid
  Scenario: suggestions of popular and available books adapted to the age of the user
    Given the user "Tim"
    And he is "4" years old
    And the popular categories for this age are
      | categoryId | categoryName    |
      | cat1       | Walt Disney     |
      | cat2       | Picture books   |
      | cat3       | Bedtime stories |
    When we ask for "3" suggestions from "2" different categories
    Then the suggestions are popular and available books adapted to the age of the user

    """
    And the project "suggestionsWS" is synchronized
    And we have those directories in the database
      | id | name | label              | description             | order | relativePath | path                        | branchId |
      | 1  | root | SuggestionsWS      | Suggestions WebServices | 0     | /            | suggestionsWS>master>/      | 1        |
      | 2  | root | suggestionsReports | Suggestions Reports     | 0     | /            | suggestionsReports>master>/ | 2        |
    And we have those pages in the database
      | id | name    | label       | description               | order | relativePath | path                               | markdown | directoryId |
      | 1  | context | The context | Why providing suggestions | 0     | /context     | suggestionsWS>master>/context      |          | 1           |
      | 2  | context | The context | Why providing suggestions | 0     | /context     | suggestionsReports>master>/context |          | 2           |

  @level_2_technical_details @nominal_case @valid
  Scenario: generate a documentation page with all scenarios of a feature
    Given we have the following markdown for the page "suggestionsWS>master>/context"
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

![Roles](../assets/images/theGardener_role_writer.png)

**Write your documentation in your project source code in Markdown.** As MarkDown preview is embedded in any modern IDE, you will have an immediate preview , moreover if you push your current branch you will have easily a view of the output in theGardener application before even merging your code.


## Requirement


Your project need to be configured on theGardener instance :

 - your project origin Git repository is well defined
 - your project is attached to a node of the hierarchy in order to be displayed at some point in the left menu
 - on the project level ,

   -  _documentationRootPath_ is defined

Once this configuration done, you can apply the following format.

## Format

### Directories and pages

The hierarchy of directories and pages is defined by one unique _thegardener.json_ file at each directory.
The first _thegardener.json_ file expected by theGardener application is located in the directory referred by _documentationRootPath_ .

For instance, for the project _myProject_

-  _documentationRootPath_ = /documentation

   - the file _myProject/documentation/thegardener.json_ should exists.

This file should have the following format :

````
```thegardener
{
  "directory" :
  {
    "label": "theGardener",
    "description": "In our documentation we trust.",
    "pages": [
      "why",
      "prerequisite",
      "changelog"
    ],
    "children" :[
      "guides"
    ]
  }
}
```
````

- directory

   - label: define the text shown on the menu item
   - description: define the tooltip of the menu item
   - pages: define the list of pages in order attached to this directory.

      - Each directory has a list of pages that are displayed as tabs. The tabs respect the order of pages defined here.
      - In the example, _why_ refer to a file _why.md_ in the current directory.

   - children: define the list of sub directories in order attached to this directory.

      - Each directory has a list of directories that are displayed as sub items in the menu. The sub items respect the order of children defined here.
      - In the example, _guides_ refer to a directory _guides_ in the current directory

         - a file _guides/thegardener.json_ should exists to define how to display the sub directory. This is a recursive structure.

See [the example](https://github.com/KelkooGroup/theGardener/blob/master/documentation/thegardener.json) in context.


### A page

The format of the page respect the [Markdown syntax](https://guides.github.com/features/mastering-markdown/).

Note: to be displayed in theGardener, the Markdown file need to have been listed in the _thegardener.json_ file of the current directory as explained above.

To **enrich the Markdown syntax**, several additional command can be applied. Those commands use the fact that Markdown syntax accept syntax highlighting: we will had a new language called _thegardener_ with a json format. **We will call refer to those kind of command as module.** Make sure to use ``` before and after the command  (in the current documentation we are using ''' otherwise it would have been escapted at the rendering :) ).


#### Define meta data

At the top of the page, add the following module :

````
```thegardener
{
  "page" :
     {
        "label": "Write documentation",
        "description": "How to write documentation with theGardener format ?"
     }
}
```
````

- page

   - label: define the text shown on the tab item
   - description: define the title of the page


See [the current page](https://github.com/KelkooGroup/theGardener/blob/master/documentation/guides/write.md) in context.


#### Use variables

During the configuration of the project in theGardener, we can define variables at project level. This allow to externalise some values that we do not want to hard code in the documentation. For instance, server, urls...
It can be useful to define swagger documentation urls for example.

_TODO Show how to get those values._


For instance :
```json
[
  {"name" : "${swagger.url}", "value" : "http://thegardener.kelkoogroup.net/api/docs/"},
  {"name" : "${headline}" , "value" : "In our documentation we trust."}
]
```

Note: we do not assume of the format of the variable name: it a simple replaceAll in that data at display time of the page.

Implicit variables that are always available :

- *project.current*: the current project name
- *branch.current*: the current branch name
- *branch.stable*: the stable branch name defined at project level

Note: don't forget to surround implicit variables by ${}


This feature is explained by this specific scenario:
```thegardener
    {
      "scenarios" :
         {
            "feature": "/provide_book_suggestions.feature",
            "select": { "tags" : ["@nominal_case"]  }
         }
    }
```

#### Include external web page

This can be useful to include the Swagger documentation. At the top of the markdown file, use this module :

````
```thegardener
{
  "includeExternalPage" :
     {
        "url": "http://thegardener.kelkoogroup.net/api/docs/"
     }
}
```
````

This external web page will be display at the same place as the other pages. In other word, the text bellow this module will be ignored.

Note that we can use the variables here :

````
```thegardener
{
  "includeExternalPage" :
     {
        "url": "${swagger.url}/#"
     }
}
```
````


TODO Find an example here.

See [an example](https://github.com/KelkooGroup/theGardener/blob/????) in context.


#### Include gherkin scenarios

**Some context**: The gherkin scenarios are really good to specify with the product owner what need to be implemented.
The gherkin scenarios are living documentation and regression tests.
There can be quite a lot of scenarios to cover all possible cases of all features.
Usually, all the gherkin scenarios are needed for regression tests and when you want to dig on a very specific case BUT it can be quite difficult to understand them when you are not in the team.
Only a few of them are meaningful for an external user who want to use your application. This is what will do here.

To include gherkin scenarios, use this module :

````
```thegardener
{
  "scenarios" :
     {
        "feature": "/page/show_a_page_with_variables.feature",
        "select": { "tags" : ["@nominal_case"]  }
     }
}
```
````

Details on the settings :

- "project": this is the name of the project in theGardener. We can use "project": "." to select the current project or even remove the setting "project" : by default this will be the current project.
- "branch": branch selected to get the scenario.  If not defined,
   - if the setting "project" refer to the current project : use the current branch
   - if the setting "project" do not refer to the current project : use the stable branch
- "feature": "/page/show_a_page_with_variables.feature", is the full path of the feature from the features directory of the project in theGardener.
- "type" :  domain {"scenario","background"}, by default "scenario".
   - if "type" is scenario, this is a selection of some scenarios in a feature
   - if "type" is background, this is a selection of the background in a feature
- "select": { tags : ["@nominal_case", "@level_1"]  } : this means to filter on scenario with @nominal_case AND @level_1 . Not used if the "type" is background.
- "includeBackground" : by default false. In case of scenario selection, include or not the background in the given step list.


See [the current page](https://github.com/KelkooGroup/theGardener/blob/master/documentation/guides/write.md) in context, there is an inclusion in the "Use variables" section.

"""
    When I perform a "GET" on following URL "/api/pages?path=suggestionsWS>master>/context"
    Then I get the following json response body
"""
[
  {
    "path": "suggestionsWS>master>/context",
    "relativePath": "/context",
    "name": "context",
    "label": "The context",
    "description": "Why providing suggestions",
    "order": 0,
    "content": [
      {
        "type": "markdown",
        "data": {
          "markdown": "\n![Roles](http://localhost:9000/api/assets?path=suggestionsWS>master>/../assets/images/theGardener_role_writer.png)\n\n**Write your documentation in your project source code in Markdown.** As MarkDown preview is embedded in any modern IDE, you will have an immediate preview , moreover if you push your current branch you will have easily a view of the output in theGardener application before even merging your code.\n\n\n## Requirement\n\n\nYour project need to be configured on theGardener instance :\n\n - your project origin Git repository is well defined\n - your project is attached to a node of the hierarchy in order to be displayed at some point in the left menu\n - on the project level ,\n\n   -  _documentationRootPath_ is defined\n\nOnce this configuration done, you can apply the following format.\n\n## Format\n\n### Directories and pages\n\nThe hierarchy of directories and pages is defined by one unique _thegardener.json_ file at each directory.\nThe first _thegardener.json_ file expected by theGardener application is located in the directory referred by _documentationRootPath_ .\n\nFor instance, for the project _myProject_\n\n-  _documentationRootPath_ = /documentation\n\n   - the file _myProject/documentation/thegardener.json_ should exists.\n\nThis file should have the following format :\n\n````\n```thegardener\n{\n  \"directory\" :\n  {\n    \"label\": \"theGardener\",\n    \"description\": \"In our documentation we trust.\",\n    \"pages\": [\n      \"why\",\n      \"prerequisite\",\n      \"changelog\"\n    ],\n    \"children\" :[\n      \"guides\"\n    ]\n  }\n}\n```\n````\n\n- directory\n\n   - label: define the text shown on the menu item\n   - description: define the tooltip of the menu item\n   - pages: define the list of pages in order attached to this directory.\n\n      - Each directory has a list of pages that are displayed as tabs. The tabs respect the order of pages defined here.\n      - In the example, _why_ refer to a file _why.md_ in the current directory.\n\n   - children: define the list of sub directories in order attached to this directory.\n\n      - Each directory has a list of directories that are displayed as sub items in the menu. The sub items respect the order of children defined here.\n      - In the example, _guides_ refer to a directory _guides_ in the current directory\n\n         - a file _guides/thegardener.json_ should exists to define how to display the sub directory. This is a recursive structure.\n\nSee [the example](https://github.com/KelkooGroup/theGardener/blob/master/documentation/thegardener.json) in context.\n\n\n### A page\n\nThe format of the page respect the [Markdown syntax](https://guides.github.com/features/mastering-markdown/).\n\nNote: to be displayed in theGardener, the Markdown file need to have been listed in the _thegardener.json_ file of the current directory as explained above.\n\nTo **enrich the Markdown syntax**, several additional command can be applied. Those commands use the fact that Markdown syntax accept syntax highlighting: we will had a new language called _thegardener_ with a json format. **We will call refer to those kind of command as module.** Make sure to use ``` before and after the command  (in the current documentation we are using ''' otherwise it would have been escapted at the rendering :) ).\n\n\n#### Define meta data\n\nAt the top of the page, add the following module :\n\n````\n```thegardener\n{\n  \"page\" :\n     {\n        \"label\": \"Write documentation\",\n        \"description\": \"How to write documentation with theGardener format ?\"\n     }\n}\n```\n````\n\n- page\n\n   - label: define the text shown on the tab item\n   - description: define the title of the page\n\n\nSee [the current page](https://github.com/KelkooGroup/theGardener/blob/master/documentation/guides/write.md) in context.\n\n\n#### Use variables\n\nDuring the configuration of the project in theGardener, we can define variables at project level. This allow to externalise some values that we do not want to hard code in the documentation. For instance, server, urls...\nIt can be useful to define swagger documentation urls for example.\n\n_TODO Show how to get those values._\n\n\nFor instance :\n```json\n[\n  {\"name\" : \"${swagger.url}\", \"value\" : \"http://thegardener.kelkoogroup.net/api/docs/\"},\n  {\"name\" : \"${headline}\" , \"value\" : \"In our documentation we trust.\"}\n]\n```\n\nNote: we do not assume of the format of the variable name: it a simple replaceAll in that data at display time of the page.\n\nImplicit variables that are always available :\n\n- *project.current*: the current project name\n- *branch.current*: the current branch name\n- *branch.stable*: the stable branch name defined at project level\n\nNote: don't forget to surround implicit variables by ${}\n\n\nThis feature is explained by this specific scenario:"
        }
      },
      {
        "type": "scenarios",
        "data": {
          "scenarios": {
            "id": 1,
            "branchId": 1,
            "path": "test/features/provide_book_suggestions.feature",
            "tags": [],
            "language": "en",
            "keyword": "Feature",
            "name": "As a user Tim, I want some book suggestions so that I can do some discovery",
            "description": "",
            "scenarios": [
              {
                "name": "suggestions of popular and available books adapted to the age of the user",
                "workflowStep": "valid",
                "description": "",
                "id": 3,
                "keyword": "Scenario",
                "abstractionLevel": "level_1_specification",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "the user \"Tim\"",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "And",
                    "text": "he is \"4\" years old",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "And",
                    "text": "the popular categories for this age are",
                    "argument": [
                      [
                        "categoryId",
                        "categoryName"
                      ],
                      [
                        "cat1",
                        "Walt Disney"
                      ],
                      [
                        "cat2",
                        "Picture books"
                      ],
                      [
                        "cat3",
                        "Bedtime stories"
                      ]
                    ]
                  },
                  {
                    "id": 3,
                    "keyword": "When",
                    "text": "we ask for \"3\" suggestions from \"2\" different categories",
                    "argument": []
                  },
                  {
                    "id": 4,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "level_1_specification",
                  "nominal_case",
                  "valid"
                ],
                "caseType": "nominal_case"
              },
              {
                "name": "providing several book suggestions",
                "workflowStep": "draft",
                "description": "",
                "id": 1,
                "keyword": "Scenario",
                "abstractionLevel": "level_0_high_level",
                "steps": [
                  {
                    "id": 0,
                    "keyword": "Given",
                    "text": "a user",
                    "argument": []
                  },
                  {
                    "id": 1,
                    "keyword": "When",
                    "text": "we ask for suggestions",
                    "argument": []
                  },
                  {
                    "id": 2,
                    "keyword": "Then",
                    "text": "the suggestions are popular and available books adapted to the age of the user",
                    "argument": []
                  }
                ],
                "tags": [
                  "draft",
                  "level_0_high_level",
                  "nominal_case"
                ],
                "caseType": "nominal_case"
              }
            ],
            "comments": []
          }
        }
      },
      {
        "type": "markdown",
        "data": {
          "markdown": "\n#### Include external web page\n\nThis can be useful to include the Swagger documentation. At the top of the markdown file, use this module :\n\n````\n```thegardener\n{\n  \"includeExternalPage\" :\n     {\n        \"url\": \"http://thegardener.kelkoogroup.net/api/docs/\"\n     }\n}\n```\n````\n\nThis external web page will be display at the same place as the other pages. In other word, the text bellow this module will be ignored.\n\nNote that we can use the variables here :\n\n````\n```thegardener\n{\n  \"includeExternalPage\" :\n     {\n        \"url\": \"${swagger.url}/#\"\n     }\n}\n```\n````\n\n\nTODO Find an example here.\n\nSee [an example](https://github.com/KelkooGroup/theGardener/blob/????) in context.\n\n\n#### Include gherkin scenarios\n\n**Some context**: The gherkin scenarios are really good to specify with the product owner what need to be implemented.\nThe gherkin scenarios are living documentation and regression tests.\nThere can be quite a lot of scenarios to cover all possible cases of all features.\nUsually, all the gherkin scenarios are needed for regression tests and when you want to dig on a very specific case BUT it can be quite difficult to understand them when you are not in the team.\nOnly a few of them are meaningful for an external user who want to use your application. This is what will do here.\n\nTo include gherkin scenarios, use this module :\n\n````\n```thegardener\n{\n  \"scenarios\" :\n     {\n        \"feature\": \"/page/show_a_page_with_variables.feature\",\n        \"select\": { \"tags\" : [\"@nominal_case\"]  }\n     }\n}\n```\n````\n\nDetails on the settings :\n\n- \"project\": this is the name of the project in theGardener. We can use \"project\": \".\" to select the current project or even remove the setting \"project\" : by default this will be the current project.\n- \"branch\": branch selected to get the scenario.  If not defined,\n   - if the setting \"project\" refer to the current project : use the current branch\n   - if the setting \"project\" do not refer to the current project : use the stable branch\n- \"feature\": \"/page/show_a_page_with_variables.feature\", is the full path of the feature from the features directory of the project in theGardener.\n- \"type\" :  domain {\"scenario\",\"background\"}, by default \"scenario\".\n   - if \"type\" is scenario, this is a selection of some scenarios in a feature\n   - if \"type\" is background, this is a selection of the background in a feature\n- \"select\": { tags : [\"@nominal_case\", \"@level_1\"]  } : this means to filter on scenario with @nominal_case AND @level_1 . Not used if the \"type\" is background.\n- \"includeBackground\" : by default false. In case of scenario selection, include or not the background in the given step list.\n\n\nSee [the current page](https://github.com/KelkooGroup/theGardener/blob/master/documentation/guides/write.md) in context, there is an inclusion in the \"Use variables\" section.\n"
        }
      }
    ]
  }
]
"""
