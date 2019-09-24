



![synchronization](../assets/images/theGardener_sychronize.png)

Markdown files, feature files, assets are synchronized from the git repositories either by a scheduler or by a web hook at git repository level, or both. 


```thegardener
    {
      "scenarios" :
         {
            "feature": "/synchronization/synchronize_resources.feature",
            "select": { "tags" : ["@documentation"]  }
         }
    }
```
