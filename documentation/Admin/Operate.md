```thegardener
{
  "page" :
     {
        "label": "Operate theGardener",
        "description": "Operate theGardener"
     }
}
```

Once the instance is running, the OPS could need some tools to operate the application. Here are some tools. 

![Roles](../assets/images/theGardener_role_ops_operate.png)

## Refresh data  

As there are several layers of data :
- the data in remote on the git repository
- the data stored on the file system in local
- the data in the database
- the data in cache

It might be useful to reset some data layer from its source. 

### API tools

#### Refresh menu from the database:

Compute the menu from the database and put it in the cache

```thegardener
{
  "openApiPath" : 
     {
        "refStartsWith": [
         "/api/admin/menu"
        ],
        "ref": [
        ],
       "methods": ["POST"]
     }
}
```   

   
#### Refresh one or all projects from the database:

- Compute all pages of all branches of the project from the database and put them in the cache 
- Compute the menu from the database and put it in the cache   


```thegardener
{
  "openApiPath" : 
     {
        "refStartsWith": [
         "/api/admin/projects/{id}/refreshFromDatabase"
        ],
        "ref": [
        ],
       "methods": ["POST"]
     }
}
```   
 

#### Refresh one or all projects from the local files:

- Remove all branches, directories and pages of the project from the database
- Transform local files of the project into branches, directories and pages entities and store them in the database  
- Compute all pages of all branches of the project from the database and put them in the cache  
- Compute the menu from the database and put it in the cache   

```thegardener
{
  "openApiPath" : 
     {
        "refStartsWith": [
         "/api/admin/projects/{id}/refreshFromDisk"
        ],
        "ref": [
        ],
       "methods": ["POST"]
     }
}
```   


#### Synchronize one project from the remote git repository:

- Update all local files from the remote git repository
- Transform local files of the project into branches, directories and pages entities and update the related entities in the database  
- Compute all pages of all branches of the project from the database and put them in the cache  
- Compute the menu from the database and put it in the cache   

```thegardener
{
  "openApiPath" : 
     {
        "refStartsWith": [
         "/api/admin/projects/{id}/synchronizeFromRemoteGitRepository"
        ],
        "ref": [
        ],
       "methods": ["POST"]
     }
}
``` 

#### Refresh one project from the remote git repository:

- Remove all branches, directories and pages of the project from the database
- Remove local files related to the project
- Checkout from scratch all branches of the project into the local file system
- Transform local files of the project into branches, directories and pages entities and store them in the database  
- Compute all pages of all branches of the project from the database and put them in the cache  
- Compute the menu from the database and put it in the cache   
 
```thegardener
{
  "openApiPath" : 
     {
        "refStartsWith": [
         "/api/admin/projects/{id}/refreshFromRemoteGitRepository"
        ],
        "ref": [
        ],
       "methods": ["POST"]
     }
}
``` 
 
 
