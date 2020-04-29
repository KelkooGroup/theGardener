```thegardener
{
  "page" :
     {
        "label": "Include Gherkin scenario",
        "description": "Include Gherkin scenario"
     }
}
```


### Some context 

The gherkin scenarios are really good to specify with the product owner what need to be implemented. 
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
 

