package controllers


import io.swagger.annotations._
import javax.inject._
import models._
import play.api._
import play.api.libs.json._
import play.api.mvc._
import repository._
import services._

import scala.concurrent._

@Api(value = "ProjectController", produces = "application/json")
class ProjectController @Inject()(projectRepository: ProjectRepository, projectService: ProjectService, hierarchyRepository: HierarchyRepository)(implicit ec: ExecutionContext) extends InjectedController {


  implicit val branchFormat = Json.format[Branch]
  implicit val hierarchyFormat = Json.format[HierarchyNode]
  implicit val projectFormat = Json.format[Project]

  @ApiOperation(value = "Register a new project", code = 201, response = classOf[Project])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The project to register", required = true, dataType = "models.Project", paramType = "body")))
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json")))
  def registerProject(): Action[Project] = Action(parse.json[Project]) { implicit request =>
    val project = request.body

    if (projectRepository.existsById(project.id)) {
      BadRequest

    } else {
      val savedProject = projectRepository.save(project)

      projectService.checkoutRemoteBranches(savedProject)

      Created(Json.toJson(savedProject))
    }
  }

  @ApiOperation(value = "Get all projects", response = classOf[Project], responseContainer = "list")
  def getAllProjects(): Action[AnyContent] = Action {
    Ok(Json.toJson(projectRepository.findAll()))
  }

  @ApiOperation(value = "Get a project", response = classOf[Project])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def getProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action {
    projectRepository.findById(id) match {

      case Some(project) =>
        val hierarchy = hierarchyRepository.findAllByProjectId(project.id)

        Ok(Json.toJson(project.copy(hierarchy = if (hierarchy.nonEmpty) Some(hierarchy) else None)))

      case _ => NotFound(s"No project $id")
    }
  }

  @ApiOperation(value = "Update a project", response = classOf[Project])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The project to update", required = true, dataType = "models.Project", paramType = "body")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Project not found"))
  )
  def updateProject(@ApiParam("Project id") id: String): Action[Project] = Action(parse.json[Project]) { implicit request =>
    val project = request.body

    if (id != project.id) {
      BadRequest

    } else {
      if (projectRepository.existsById(id)) {
        projectRepository.save(project)

        Ok(Json.toJson(projectRepository.findById(id)))

      } else {
        NotFound(s"No project $id")
      }
    }
  }

  @ApiOperation(value = "Delete a project")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def deleteProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action {

    if (projectRepository.existsById(id)) {
      projectRepository.deleteById(id)

      Ok

    } else {
      NotFound(s"No project $id")
    }
  }

  @ApiOperation(value = "Link a Project to hierarchy", code = 201, response = classOf[HierarchyNode])
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json"), new ApiResponse(code = 404, message = "Project or hierarchy not found")))
  def linkProjectToHierarchy(@ApiParam("Project Id") id: String, @ApiParam("Hierarchy Id") hierarchyId: String): Action[AnyContent] = Action {
    if (projectRepository.existsById(id)) {
      if (hierarchyRepository.existsById(hierarchyId)) {
        projectRepository.linkHierarchy(id, hierarchyId)
        val hierarchy = hierarchyRepository.findAllByProjectId(id)
        Created(Json.toJson(hierarchy))

      } else {
        NotFound(s"No hierarchy node $hierarchyId")
      }
    } else {
      NotFound(s"No project $id")
    }
  }

  @ApiOperation(value = "Delete a link hierarchy to a project")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Link hierarchy project not found")))
  def deleteLinkProjectToHierarchy(@ApiParam("Project id") id: String, @ApiParam("Hierarchy Id") hierarchyId: String): Action[AnyContent] = Action {

    if (projectRepository.existsLinkByIds(id, hierarchyId)) {
      projectRepository.unlinkHierarchy(id, hierarchyId)

      Ok(Json.toJson(hierarchyRepository.findAllByProjectId(id)))

    } else {
      NotFound(s"No link hierarchy $hierarchyId to a project $id")
    }
  }

  @ApiOperation(value = "Webhook to synchronize a new project")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def synchronizeProject(@ApiParam("Project id") id: String): Action[AnyContent] = Action.async {
    projectRepository.findById(id)
      .map(projectService.synchronize(_).map(_ => Ok))
      .getOrElse(Future.successful(NotFound(s"No project $id")))

  }

  @ApiOperation(value = "get the hierarchy link to a project", response = classOf[HierarchyNode])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Project not found")))
  def getLinkProjectToHierarchy(@ApiParam("project Id") id: String): Action[AnyContent] = Action {

    if (projectRepository.existsById(id)) {
      Ok(Json.toJson(hierarchyRepository.findAllByProjectId(id)))

    } else {
      NotFound(s"No project $id")
    }
  }
}

@Api(value = "HierarchyController", produces = "application/json")
class HierarchyController @Inject()(hierarchyRepository: HierarchyRepository) extends InjectedController {

  implicit val hierarchyFormat = Json.format[HierarchyNode]

  @ApiOperation(value = "Add a  new Hierarchy", code = 201, response = classOf[HierarchyNode])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The hierarchy to add", required = true, dataType = "models.HierarchyNode", paramType = "body")))
  @ApiResponses(Array(new ApiResponse(code = 400, message = "Incorrect json")))
  def addHierarchy(): Action[HierarchyNode] = Action(parse.json[HierarchyNode]) { implicit request =>
    val hierarchy = request.body

    if (hierarchyRepository.existsById(hierarchy.id)) {
      BadRequest

    } else {
      val addHierarchy = hierarchyRepository.save(hierarchy)
      Created(Json.toJson(addHierarchy))
    }
  }

  @ApiOperation(value = "Get all hierarchies", response = classOf[HierarchyNode])
  def getAllHierarchies(): Action[AnyContent] = Action {
    Ok(Json.toJson(hierarchyRepository.findAll()))
  }

  @ApiOperation(value = "Update an hierarchy", response = classOf[HierarchyNode])
  @ApiImplicitParams(Array(new ApiImplicitParam(value = "The hierarchy to update", required = true, dataType = "models.HierarchyNode", paramType = "body")))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Hierarchy not found"))
  )
  def updateHierarchy(@ApiParam("Hierarchy id") id: String): Action[HierarchyNode] = Action(parse.json[HierarchyNode]) { implicit request =>
    val hierarchy = request.body

    if (id != hierarchy.id) {
      BadRequest

    } else {
      if (hierarchyRepository.existsById(id)) {
        hierarchyRepository.save(hierarchy)

        Ok(Json.toJson(hierarchyRepository.findById(id)))

      } else {
        NotFound(s"No hierarchy $id")
      }
    }
  }

  @ApiOperation(value = "Delete an hierarchy")
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Hierarchy not found")))
  def deleteHierarchy(@ApiParam("Hierarchy id") id: String): Action[AnyContent] = Action {

    if (hierarchyRepository.existsById(id)) {
      hierarchyRepository.deleteById(id)

      Ok

    } else {
      NotFound(s"No hierarchy $id")
    }
  }
}


@Api(value = "CriteriasController", produces = "application/json")
class FakeCriteriasController @Inject()() extends InjectedController {

  def getAllHierarchies(): Action[AnyContent] = Action {
    Ok(
      """
[
  {
		"id": ".",
		"slugName": "root",
		"name": "Hierarchy root",
    "childrenLabel": "Views",
    "childLabel": "View"
	},
    {
		"id": ".01.",
		"slugName": "eng",
		"name": "Engineering view",
    "childrenLabel": "System groups",
	  "childLabel": "System groups"
  },
    {
		"id": ".01.01.",
		"slugName": "library",
		"name": "Library system group",
    "childrenLabel": "Systems",
    "childLabel": "System"
	},
    {
		"id": ".01.01.01.",
		"slugName": "suggestion",
		"name": "Suggestion system",
    "childrenLabel": "Projects",
    "childLabel": "Project",
    "projects" : [
       {
         "id" : "suggestionWS",
         "label" : "Suggestion WebService",
         "stableBranch" : "master",
         "branches" : [  "master", "feature/upgrade" ]
       },
       {
         "id" : "suggestionWSClient",
          "label" : "Suggestion WebService client library",
          "stableBranch" : "master",
          "branches" : [  "master", "bugfix/13553", "bugfix/13521", "bugfix/13544313" ]
        }
     ]
	},
    {
		"id": ".01.01.02.",
		"slugName": "user",
		"name": "User system",
    "childrenLabel": "Projects",
    "childLabel": "Project",
    "projects" : [
        {
          "id" : "userWS",
          "label" : "User WebService",
          "stableBranch" : "master",
          "branches" : [   "feature/upgrade-angular", "master",  "bugfix/13553"]
        },
        {
          "id" : "userWSClient",
           "label" : "User WebService client library",
           "stableBranch" : "master",
           "branches" : [   "bugfix/13553", "bugfix/13521", "master", "bugfix/13544313" ]
         }
      ]
	},
    {
		"id": ".01.01.03.",
		"slugName": "search",
		"name": "Search system",
    "childrenLabel": "Projects",
    "childLabel": "Project",
    "projects" : [
      {
        "id" : "searchWS",
        "label" : "Search WebService",
        "stableBranch" : "master",
        "branches" : [  "master", "feature/upgrade" ]
      },
      {
        "id" : "searchWSClient",
         "label" : "Search WebService client library",
         "stableBranch" : "master",
         "branches" : [  "master", "bugfix/1351" ]
       }
    ]
	},
    {
		"id": ".01.02.",
		"slugName": "other",
		"name": "Other system group",
    "childrenLabel": "Systems",
    "childLabel": "System",
       "projects" : [
        {
          "id" : "otherWS",
          "label" : "Other WebService",
          "stableBranch" : "master",
          "branches" : [  "master" ]
        }
      ]
	},
    {
		"id": ".01.03.",
		"slugName": "another",
		"name": "Another system group",
    "childrenLabel": "Systems",
    "childLabel": "System",
    "projects" : [
         {
           "id" : "anotherWS",
           "label" : "Another WebService",
           "stableBranch" : "master",
           "branches" : [  "master" ]
         }
       ]
	},
   {
 		"id": ".02.",
 		"slugName": "biz",
 		"name": "Business view",
    "childrenLabel": "Units",
 	  "childLabel": "Unit"
   },
   {
		"id": ".02.01.",
		"slugName": "u1",
		"name": "Unit 1",
    "childrenLabel": "Sub units",
    "childLabel": "Sub unit",
       "projects" : [
        {
          "id" : "otherWS",
          "label" : "Other WebService",
          "stableBranch" : "master",
          "branches" : [  "master" ]
        }
      ]
	},
    {
		"id": ".02.02.",
		"slugName": "u2",
		"name": "Unit 2",
    "childrenLabel": "Sub units",
    "childLabel": "Sub unit",
    "projects" : [
         {
           "id" : "anotherWS",
           "label" : "Another WebService",
           "stableBranch" : "master",
           "branches" : [  "master" ]
         }
       ]
	}
]
  """.stripMargin)
  }

}

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

