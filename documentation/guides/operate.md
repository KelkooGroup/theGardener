```thegardener
{
  "page" :
     {
        "label": "Operate",
        "description": "How to operate to theGardener instance ?"
     }
}
```

Once the instance is running, the OPS could need some tools to operate the application. Here are some tools. 

![Roles](../assets/images/theGardener_role_admin.png)



## Refresh data  

As there are several layers of data :
- the data in remote on the git repository
- the data stored on the file system in local
- the data in the database
- the data in cache

It might be useful to reset some data layer from its source. 

Here are some tools to refresh the data :

```thegardener
    {
      "scenarios" : 
         {
            "feature": "/features/administration/operation.feature",
            "select": { "tags" : ["@data_refresh"]  }
         }
    }
```