```thegardener
{
  "page" :
     {
        "label": "Overview",
        "description": "What is theGardener?"
     }
}
```

## Aim

**We invite you to include the documentation writing task in the development loop.** 

You will trust again the technical documentation of your projects. 

![](global/assets/images/development_workflow.png)

The same way Test Driven Development have invited the tests in the development loop, with theGardener, you will include the documentation writing task in the development loop.

## Overview

![Architecture](assets/images/theGardener_project_roles_reader_writer.png)

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
     

### Open sourced

- [Apache License 2.0](https://github.com/KelkooGroup/theGardener/blob/master/LICENSE)
- [Sources](https://github.com/KelkooGroup/theGardener)
- [Milestones](https://github.com/KelkooGroup/theGardener/milestones?direction=asc&sort=title)
- [Kanban](https://github.com/KelkooGroup/theGardener/projects/1)
- [Issues](https://github.com/KelkooGroup/theGardener/issues)
- [Speak to a human](https://discordapp.com/channels/417704230531366923/417704230976225281)

### Technical stack

- On Front: TypeScript with Angular
- On Back: Scala with Play

### Original idea and main contributors

https://www.kelkoogroup.com/

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


### Roots

- At [KelkooGroup](https://www.kelkoogroup.com/) we are intensively using the Behavior Driven Development to specify, develop and tests our applications.  We prefer talking about Specification by examples but BDD (Behavior Driven Development) name is more popular. There are many advantages to use this process :   

  - Enforce a close collaboration DEV / Product Owner
    - Use examples to open discussion and find many cases
    - Allows a very fast feedback loop
  - Functional tests
    - Fast and stable tests
    - The developer is guided, the code is pulled by the tests
    - Flexible code is required to mock external interactions
  - Runnable Documentation
    - Pulled from code, the documentation is always up to date
    - The documentation is exhaustive 

- So we have exhaustive and up to date documentation with all those scenarios. It used for the specification, the implementation and the regression tests. But we can do more with it: expose it the users of our applications.

- We have not found a proper tool to expose them, so we have started to build one.
- As we are using a lot of open source project/framework and libraries, we have decided to make it open source : https://github.com/KelkooGroup/theGardener.


Read [theGardener roots](assets/decks/theGardener_roots.pdf) for the full picture and a full example of user story written / implemented / tested with BDD.

