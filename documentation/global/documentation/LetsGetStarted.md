```thegardener
{
  "page" :
     {
        "label": "Let's get started...",
        "description": "Let's get started..."
     }
}
```


To use theGardener to serve your documentation, follow the next steps: 

### Install an instance

[=> Install an instance with Docker](thegardener://navigate/_doc/theGardener/master/_Admin/Install).

After this step you will have a running instance of theGardener with
- default colors, logos, titles
- a simple hierarchy with one node
- one project, theGardener project, attached to this node  

### Configure the instance

#### Customize the UI

[=> Choose your logo, title, colors...](thegardener://navigate/_doc/theGardener/master/_Admin/Configure#ui-settings)

After this step you will have a running instance of theGardener that looks like your own back office.

#### Define the hierarchy to host your projects

[=> Configure the hierarchy tree](thegardener://navigate/_doc/theGardener/master/_Admin/Configure#hierarchy)

It really depends on how many projects you want to register and how you want to access to theme. 

The first level of the tree is the header, each sub tree under the header will appear on the left menu. 

After this step you will have a running instance of theGardener that looks like your own back office and well organized to host projects.

### Register your projects

[=> Register projects](thegardener://navigate/_doc/theGardener/master/_Admin/Configure#projects)

After this step your projects will be hosted by theGardener but 
- not yet accessible through the navigation
- not yet refreshed when a developer will push the code: theGardener schedule a scan of the git repositories from time to time

[=> Attach projects to hierarchy nodes](thegardener://navigate/_doc/theGardener/master/_Admin/Configure#link-between-projects-and-hierarchy)

Attach a project to a hierarchy node make it accessible through the navigation. A project can be attached to several nodes if needed: this is useful for common libraries for example.
After this step the projects will be accessible through the menu.

[=> Define web hooks at git repository level](thegardener://navigate/_doc/theGardener/master/_Admin/Configure#hooks-on-the-git-servers)

After this step the projects will be accessible through the menu and refreshed right away when a developer push the code.

### Follow theGardener documentation format 

- If your documentation is already in [Markdown](https://www.markdownguide.org/basic-syntax/), you will just have to reference your `.md` files in a `thegarderner.json` file at each directory.
- If not, the [Markdown](https://www.markdownguide.org/basic-syntax/) is a really simple syntax, it's gonna be easy.

After this step your documentation will be accessible through theGardener and you will take advantage of the basic principle of including the documentation task in the development loop.

Then you can improve your documentation by using [more advanced features](thegardener://navigate/_doc/theGardener/master/_Write/Improve), [include your Gherkin scenario](thegardener://navigate/_doc/theGardener/master/_Write/IncludeGherkin) or [take advantage of OpenAPI standards](thegardener://navigate/_doc/theGardener/master/_Write/IncludeOpenApi).

Enjoy and [give us feedback!](thegardener://navigate/_doc/theGardener/master/_Contribute/Overview#want-to-give-feedback)
  


  



