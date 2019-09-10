```thegardener
{
  "page" :
     {
        "label": "Configure",
        "description": "How to configure a new instance of theGardener ?"
     }
}
```

theGardener is an application that gather documentation from projects hosted on git repositories. 
This application need to be installed and configured. 

![Roles](../assets/images/theGardener_role_admin.png)


The main configuration steps are :

- define the hierarchy
- register projects hosted on a git repository
- make the links between the projects and the hierarchy
- define hooks on git servers to trigger theGardener synchronisation


## Hierarchy and projects

![Roles](../assets/images/theGardener_hierarchy_projects.png)



The hierarchy consist on a tree that can be as fat and as deep as needed. 
For a better user experience we advise to have 
- less than 8 nodes on the first level
- less that 5 levels 

The list of project can be as long as needed. The limit is the disk size to store the different project sources in the database and on the file system.

Once the hierarchy and the projects are registered, we need to associate the project to the nodes of the hierarchy. One given project can be attached to several nodes if needed.    

The first level of the hierarchy will be displayed on the header of the application. 
Once a first level node is selected, the sub tree will be displayed on the left menu.

**DISCLAIMER**: this procedure help to configure the application theGardener. This procedure will be replaced by a proper UI: the goal of the milestone [M3 Administration](https://github.com/KelkooGroup/theGardener/milestone/2) is to provide an easy way to configure the application.


### Hierarchy

Insert rows on the table **hierarchyNode**.

For instance to have the previous hierarchy :

```
mysql> INSERT INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel) VALUES ('.', 'root', 'root', 'Platforms', 'Platform');
mysql> INSERT INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel) VALUES ('.01.', 'lib', 'Library', 'Systems', 'System');
mysql> INSERT INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel) VALUES ('.02.', 'uni', 'University', 'Projects', 'Project');
mysql> INSERT INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel) VALUES ('.03.', 'leg', 'Legal', 'Projects', 'Project');
mysql> INSERT INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel) VALUES ('.01.01.', 'users', 'Users', 'Projects', 'Project');
mysql> INSERT INTO hierarchyNode (id, slugName, name, childrenLabel, childLabel) VALUES ('.01.02.', 'books', 'Books', 'Projects', 'Project');
```

### Projects

Insert rows on the table **project**.

For instance insert theGardener itself :

```
mysql> INSERT INTO project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath) VALUES ('theGardener', 'theGardener', 'https://github.com/KelkooGroup/theGardener.git', 'master', 'master|feature.*/', 'test/features', 'documentation');
```

Field | Type  | Description
------------ | ------------- | -------------
id | string |  the identify the project
name | string |  label of the project in the menu
repositoryUrl | string |  url of the project in a git server. should start with _http_ and end by _.git_
stableBranch | string |  existing branch that is considered as stable. Could be _master_ for instance.
displayedBranches | regexpr |  regular expression to filter the branches that are synchronized and displayed.
featuresRootPath | string |  relative path to the directory that host gherkin scenarios. Can be empty.
documentationRootPath | string |  relative path to the directory that host the documentation. This directory should store the first _thegardener.json_ file.


### Link between projects and hierarchy

Insert rows on the table **project_hierarchyNode**.

This is an association table between **project** and **hierarchyNode**. 


## Hooks on the git servers

To be able to have fast feedback on theGardener when a change is pushed on a project registered on theGardener, we need to put in place a web hook.
The web hook configuration depends on tha web application that serve the git repositories. For instance it can be GitLab.
The web hook should do a **POST** on **/api/projects/:id/synchronize** : this will trigger the synchronisation of this project on theGardener.
 
