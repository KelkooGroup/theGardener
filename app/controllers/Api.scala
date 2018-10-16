package controllers


import io.swagger.annotations._
import javax.inject._
import models._
import play.api.mvc._

@Api(value = "GenerateDocumentationController", produces = "application/json")
class FakeGenerateDocumentationController @Inject()() extends InjectedController {


  @ApiOperation(value = "Get all hierarchies", response = classOf[HierarchyNode])
  def generateDocumentation(): Action[AnyContent] = Action {
    Ok(
      """
        |{
        |   "id":".",
        |   "slugName":"root",
        |   "name":"Hierarchy root",
        |   "childrenLabel":"Views",
        |   "childLabel":"View",
        |   "projects":[
        |
        |   ],
        |   "children":[
        |      {
        |         "id":".01.",
        |         "slugName":"eng",
        |         "name":"Engineering view",
        |         "childrenLabel":"System groups",
        |         "childLabel":"System groups",
        |         "projects":[
        |
        |         ],
        |         "children":[
        |            {
        |               "id":".01.01.",
        |               "slugName":"library",
        |               "name":"Library system group",
        |               "childrenLabel":"Systems",
        |               "childLabel":"System",
        |               "projects":[
        |
        |               ],
        |               "children":[
        |                  {
        |                     "id":".01.01.01.",
        |                     "slugName":"suggestion",
        |                     "name":"Suggestion system",
        |                     "childrenLabel":"Projects",
        |                     "childLabel":"Project",
        |                     "projects":[
        |                        {
        |                           "id":"suggestionsWS",
        |                           "name":"Suggestions WebServices",
        |                           "branches":[
        |                              {
        |                                 "id":1,
        |                                 "name":"master",
        |                                 "isStable":true,
        |                                 "features":[
        |                                    {
        |                                       "id":1,
        |                                       "path":"test/features/provide_book_suggestions.feature",
        |                                       "name":"Provide some book suggestions",
        |                                       "description":"As a user,\nI want some book suggestions\nSo that I can do some discovery",
        |                                       "tags":[
        |
        |                                       ],
        |                                       "language":"en",
        |                                       "keyword":"Feature",
        |                                       "scenarios":[
        |                                          {
        |                                             "id":0,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_0_high_level",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_0_high_level",
        |                                                "nominal_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"a user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for suggestions",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"error_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "error_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"impossible to get information on the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"Then",
        |                                                   "text":"the system is temporary not available",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions with popular categories",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"valid",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "nominal_case",
        |                                                "valid"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"he is \"4\" years old",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"And",
        |                                                   "text":"the popular categories for this age are",
        |                                                   "argument":[
        |                                                      [
        |                                                         "categoryId",
        |                                                         "categoryName"
        |                                                      ],
        |                                                      [
        |                                                         "cat1",
        |                                                         "Walt Disney"
        |                                                      ],
        |                                                      [
        |                                                         "cat2",
        |                                                         "Picture books"
        |                                                      ],
        |                                                      [
        |                                                         "cat3",
        |                                                         "Bedtime stories"
        |                                                      ]
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":4,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          }
        |                                       ],
        |                                       "comments":[
        |
        |                                       ]
        |                                    },
        |                                    {
        |                                       "id":1,
        |                                       "path":"test/features/provide_book_suggestions.feature",
        |                                       "name":"Provide some book suggestions 2",
        |                                       "description":"As a user,\nI want some book suggestions\nSo that I can do some discovery",
        |                                       "tags":[
        |
        |                                       ],
        |                                       "language":"en",
        |                                       "keyword":"Feature",
        |                                       "scenarios":[
        |                                          {
        |                                             "id":0,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_0_high_level",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_0_high_level",
        |                                                "nominal_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"a user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for suggestions",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"error_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "error_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"impossible to get information on the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"Then",
        |                                                   "text":"the system is temporary not available",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions with popular categories",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"valid",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "nominal_case",
        |                                                "valid"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"he is \"4\" years old",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"And",
        |                                                   "text":"the popular categories for this age are",
        |                                                   "argument":[
        |                                                      [
        |                                                         "categoryId",
        |                                                         "categoryName"
        |                                                      ],
        |                                                      [
        |                                                         "cat1",
        |                                                         "Walt Disney"
        |                                                      ],
        |                                                      [
        |                                                         "cat2",
        |                                                         "Picture books"
        |                                                      ],
        |                                                      [
        |                                                         "cat3",
        |                                                         "Bedtime stories"
        |                                                      ]
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":4,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          }
        |                                       ],
        |                                       "comments":[
        |
        |                                       ]
        |                                    }
        |                                 ]
        |                              }
        |                           ]
        |                        }
        |                     ],
        |                     "children":[
        |
        |                     ]
        |                  }
        |               ]
        |            },
        |
        |{
        |               "id":".01.02.",
        |               "slugName":"library",
        |               "name":"Library system group 2",
        |               "childrenLabel":"Systems",
        |               "childLabel":"System",
        |               "projects":[
        |
        |               ],
        |               "children":[
        |                  {
        |                     "id":".01.02.01.",
        |                     "slugName":"suggestion",
        |                     "name":"Suggestion system 2",
        |                     "childrenLabel":"Projects",
        |                     "childLabel":"Project",
        |                     "projects":[
        |                        {
        |                           "id":"suggestionsWS",
        |                           "name":"Suggestions WebServices 2",
        |                           "branches":[
        |                              {
        |                                 "id":1,
        |                                 "name":"master",
        |                                 "isStable":true,
        |                                 "features":[
        |                                    {
        |                                       "id":1,
        |                                       "path":"test/features/provide_book_suggestions.feature",
        |                                       "name":"Provide some book suggestions",
        |                                       "description":"As a user,\nI want some book suggestions\nSo that I can do some discovery",
        |                                       "tags":[
        |
        |                                       ],
        |                                       "language":"en",
        |                                       "keyword":"Feature",
        |                                       "scenarios":[
        |                                          {
        |                                             "id":0,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_0_high_level",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_0_high_level",
        |                                                "nominal_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"a user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for suggestions",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"error_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "error_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"impossible to get information on the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"Then",
        |                                                   "text":"the system is temporary not available",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions with popular categories",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"valid",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "nominal_case",
        |                                                "valid"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"he is \"4\" years old",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"And",
        |                                                   "text":"the popular categories for this age are",
        |                                                   "argument":[
        |                                                      [
        |                                                         "categoryId",
        |                                                         "categoryName"
        |                                                      ],
        |                                                      [
        |                                                         "cat1",
        |                                                         "Walt Disney"
        |                                                      ],
        |                                                      [
        |                                                         "cat2",
        |                                                         "Picture books"
        |                                                      ],
        |                                                      [
        |                                                         "cat3",
        |                                                         "Bedtime stories"
        |                                                      ]
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":4,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          }
        |                                       ],
        |                                       "comments":[
        |
        |                                       ]
        |                                    }
        |                                 ]
        |                              }
        |                           ]
        |                        },
        |{
        |                           "id":"suggestionsWS",
        |                           "name":"Suggestions WebServices 3",
        |                           "branches":[
        |                              {
        |                                 "id":1,
        |                                 "name":"master",
        |                                 "isStable":true,
        |                                 "features":[
        |                                    {
        |                                       "id":1,
        |                                       "path":"test/features/provide_book_suggestions.feature",
        |                                       "name":"Provide some book suggestions",
        |                                       "description":"As a user,\nI want some book suggestions\nSo that I can do some discovery",
        |                                       "tags":[
        |
        |                                       ],
        |                                       "language":"en",
        |                                       "keyword":"Feature",
        |                                       "scenarios":[
        |                                          {
        |                                             "id":0,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_0_high_level",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_0_high_level",
        |                                                "nominal_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"a user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for suggestions",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"error_case",
        |                                             "workflowStep":"ready",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "error_case",
        |                                                "ready"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"impossible to get information on the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"Then",
        |                                                   "text":"the system is temporary not available",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          },
        |                                          {
        |                                             "id":1,
        |                                             "name":"providing several book suggestions with popular categories",
        |                                             "abstractionLevel":"level_1",
        |                                             "caseType":"nominal_case",
        |                                             "workflowStep":"valid",
        |                                             "keyword":"Scenario",
        |                                             "description":"",
        |                                             "tags":[
        |                                                "level_1",
        |                                                "nominal_case",
        |                                                "valid"
        |                                             ],
        |                                             "steps":[
        |                                                {
        |                                                   "id":0,
        |                                                   "keyword":"Given",
        |                                                   "text":"the user \"Tim\"",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":1,
        |                                                   "keyword":"And",
        |                                                   "text":"he is \"4\" years old",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":2,
        |                                                   "keyword":"And",
        |                                                   "text":"the popular categories for this age are",
        |                                                   "argument":[
        |                                                      [
        |                                                         "categoryId",
        |                                                         "categoryName"
        |                                                      ],
        |                                                      [
        |                                                         "cat1",
        |                                                         "Walt Disney"
        |                                                      ],
        |                                                      [
        |                                                         "cat2",
        |                                                         "Picture books"
        |                                                      ],
        |                                                      [
        |                                                         "cat3",
        |                                                         "Bedtime stories"
        |                                                      ]
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":3,
        |                                                   "keyword":"When",
        |                                                   "text":"we ask for \"3\" suggestions from \"2\" different categories",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                },
        |                                                {
        |                                                   "id":4,
        |                                                   "keyword":"Then",
        |                                                   "text":"the suggestions are popular and available books adapted to the age of the user",
        |                                                   "argument":[
        |
        |                                                   ]
        |                                                }
        |                                             ]
        |                                          }
        |                                       ],
        |                                       "comments":[
        |
        |                                       ]
        |                                    }
        |                                 ]
        |                              }
        |                           ]
        |                        }
        |                     ],
        |                     "children":[
        |
        |                     ]
        |                  }
        |               ]
        |            }
        |
        |         ]
        |      }
        |   ]
        |}
      """.stripMargin)
  }


}

