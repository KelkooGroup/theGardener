```thegardener
{
  "page" :
     {
        "label": "Include OpenApi elements",
        "description": "Include OpenApi elements"
     }
}
```

### Some context

The OpenAPI Specification (OAS) defines a standard, language-agnostic interface to RESTful APIs which allows both humans and computers to discover and understand the capabilities of the service without access to source code, documentation, or through network traffic inspection.
With OpenApi it is possible to produce a Json file (named swagger.json) that list all the paths that we can use and all the definitions of models that the API uses.
Sometimes, it is meaningful to add some Models from the source code to the documentation to be more clear. This is what we will do here  
By default in swagger.json models definitions there is only the names and types of the parameters.   
This module allows to display a default value, a description and examples if they are described in the swagger.json 


## Include OpenAPI models    

To include OpenApi models, use this module :

````
```thegardener
{
  "openApi" : 
     {
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
        "openApiType": "model",
        "ref": "#/definitions/Project",
        "deep": 1
     }
}
```


## Include OpenAPI path   

To include OpenApi Paths, use this module :

````
```thegardener
{
  "openApiPath" : 
     {
        "openApiUrl": "https://thegardener.kelkoogroup.com/api/docs/swagger.json",
        "refStartsWith": [
         "/api/projects/{id}",
         "/api/directories"
        ],
        "ref": [
         "/api/projects/{id}",
         "/api/directories"
        ],
       "methods": ["POST","GET"]
     }
}
``` 
````

Details on the settings :

- "openApi.json.url" refers to the url of the API that match the Open API specification (https://swagger.io/specification).
   - example : https://thegardener.kelkoogroup.com/api/docs/swagger.json
   - default value : use the variable ${openApi.json.url}.  The writer could set this variable at project level. In this case he would need to add it at every model inclusion. See #63. 
- "refStartsWith" : Table of paths that represent the start of the paths you want to display 
   - example: 
     - if we have the end points :  
      POST       /api/projects  
      GET        /api/projects   
      GET        /api/projects/:id  
      PUT        /api/projects/:id  
      DELETE     /api/projects/:id  
      POST       /api/projects/:id/synchronize  
      GET        /api/projects/:id/hierarchy  
      GET        /api/directories    
     - if we have:
      "refStartsWith": [ "/api/projects/{id}", "/api/directories"]  
     - it will display :  
       GET       /api/projects/:id   
       PUT       /api/projects/:id  
       DELETE    /api/projects/:id  
       POST      /api/projects/:id/synchronize                   
       GET       /api/projects/:id/hierarchy      
       GET       /api/directories  
- "ref" : Table of paths that represent the exact paths you want to display 
- "methods" : Define filter on methods
   - default : display all methods
   - example : 
     - if we have the same ref as above and methods: ["GET"]
     - it will display :  
       GET         /api/projects/:id               
       GET         /api/projects/:id/hierarchy      
       GET         /api/directories
- "errorMessage" : displayed if the resource is not reachable, If not provided, no message will be displayed

this module will be displayed as follows:  

```thegardener
{
  "openApiPath" : 
     {
        "openApiUrl": "https://thegardener.kelkoogroup.com/api/docs/swagger.json",
        "refStartsWith": [
         "/api/projects/{id}",
         "/api/directories"
        ],
        "ref": [
         "/api/projects/{id}",
         "/api/directories"
        ],
       "methods": ["POST","GET"]
     }
}
``` 
