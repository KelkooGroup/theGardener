```thegardener
{
  "page" :
     {
        "label": "Overview",
        "description": "theGardener overview"
     }
}
```

## Aim

**We invite you to include the documentation writing task in the development loop.** 

You will trust again the technical documentation of your projects. 

![](global/assets/images/development_workflow.png)

The same way Test Driven Development have invited the tests in the development loop, with theGardener, you will include the documentation writing task in the development loop.

## Overview

![Architecture](global/assets/images/theGardener_project_roles_reader_writer.png)

### Main features

- Scan projects from git repositories
- Organize projects in a tree
- Organize documentation of a project with pages within directories
- Features to help the developer in the documentation redaction

   - Support [Markdown](https://www.markdownguide.org/basic-syntax/) documents
   - Include [Gherkin](https://cucumber.io/docs/gherkin/reference/) scenarios
   - Include [OpenAPI](https://swagger.io/resources/open-api/) models and endpoints (standard behind [swagger.io](https://swagger.io))
   - Include images
   - Externalize data by variables
   - Include external web pages
   - Preview on theGardener before merge 
     
### Prerequisite

 - **Use git** 
   - theGardener scan only projects hosted on a git repository
 - **Use feature branching and merge requests**
   - Your documentation will be reviewed with a pair at the same time as the code: you will trust your documentation
 - **Write documentation in [Markdown](https://www.markdownguide.org/basic-syntax/)**    
 - _(Optional but recommended)_ Write code with [Behavior Driven Development](https://cucumber.io/docs/bdd/)
   - It would help showing meaningful examples
 - _(Optional but recommended)_ On web services development, [OpenAPI](https://swagger.io/docs/specification/about/) (For instance, use Swagger) 
   - It would avoid you copy past of model and endpoint descriptions
