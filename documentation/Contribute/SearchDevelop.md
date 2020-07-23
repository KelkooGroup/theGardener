```thegardener
{
  "page" :
     {
        "label": "SearchDevelop",
        "description": "Search develop on theGardener"
     }
}
```

## Aim
Get a Search on theGardener using lucene4s

## List of step 

### Step 1 establish the base search
- Create an index of the pages to search into
- Create a route to enable the frontend to do a search
- Enable search on name and labels with same weight of pages
- in memory index 
- maybe replace the cache but not now  (if so rethink what is stored in)

```thegardener
{
  "scenarios" : 
     {
        "feature": "/search/search_through_name_pages.feature",
        "select": { "tags" : ["@nominal_case"]  }
     }
}
```


## step 2  UI 
- define how to do the search UI 
    - position of the bar (search on the top ? on a tab ? on the left menu ?)
    - how many results display ? 
    - form of the display ( path, relative path name and label description  ) ?
 
## step 3 Tags 
- add tags to pages on metaData
- use tags on search 

## step 4 define weight
- define weight of each part
- add weight manually or with lucene (if possible )?

