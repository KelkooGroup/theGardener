```thegardener
{
  "page" :
     {
        "label": "Include Gherkin scenario",
        "description": "Include Gherkin scenario"
     }
}
```


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
        "feature": "/documentation/page/show_a_page_with_variables.feature",
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


## Include OpenAPI models    
**Some context**: The OpenAPI Specification (OAS) defines a standard, language-agnostic interface to RESTful APIs which allows both humans and computers to discover and understand the capabilities of the service without access to source code, documentation, or through network traffic inspection.
With OpenApi it is possible to produce a Json file (named swagger.json) that list all the paths that we can use and all the definitions of models that the API uses.
Sometimes, it is meaningful to add some Models from the source code to the documentation to be more clear. This is what we will do here  
By default in swagger.json models definitions there is only the names and types of the parameters.   
This module allows to display a default value, a description and examples if they are described in the swagger.json 

To include OpenApi models, use this module :

````
```thegardener
{
  "openApi" : 
     {
        "openApiUrl": "https://thegardener.kelkoogroup.com/api/docs/swagger.json",
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 1
     }
}
```
````

Details on the settings :

- "openApiUrl" refers to the url of the API that match the Open API specification (https://swagger.io/specification).
   - example : https://thegardener.kelkoogroup.com/api/docs/swagger.json
   - default value : use the variable ${openApi.json.url}. The writer could set this variable at project level. [See "Use variables" section](thegardener://path=theGardener>>_Write_/Write#use-variables). 
- "openApiType" : "model". Only one possible value for now. We will be able to include "path" in the future
- "ref" : path to get the model. 
   - example "#/definitions/Project". Use the Open API Reference syntax : https://swagger.io/specification/#referenceObject.
- "deep" : int. By default 1. Identify at which level on the model tree we should go.
   - Example, for Project, 
      - With "deep" == 1 => show only Project
      - With "deep" == 2 => show Project and Variable, HierarchyNode, Branch
- "label" : define which label we want to display. By default : name of the model, in this example "Project"
- "errorMessage" : displayed if the resource is not reachable, If not provided, no message will be displayed

this module will be displayed as follows:

```thegardener
{
  "openApi" : 
     {
        "openApiUrl": "https://thegardener.kelkoogroup.com/api/docs/swagger.json",
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 1
     }
}
```

