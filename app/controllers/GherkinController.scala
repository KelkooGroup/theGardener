package controllers

import io.swagger.annotations._
import javax.inject.Inject
import models._
import play.api.Logging
import play.api.libs.json.Json
import play.api.mvc._
import services.MenuService
import dto._
import repositories._


@Api(value = "GherkinController", produces = "application/json")
class GherkinController @Inject()(gherkinRepository: GherkinRepository, menuService: MenuService, hierarchyRepository: HierarchyRepository, projectRepository: ProjectRepository) extends InjectedController with Logging {


  @ApiOperation(value = "Get gherkin", response = classOf[DocumentationDTO])
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "project", dataType = "string", paramType = "query", allowMultiple = true),
    new ApiImplicitParam(name = "node", dataType = "string", paramType = "query", allowMultiple = true)
  ))
  @ApiResponses(Array(
    new ApiResponse(code = 400, message = "Incorrect json"),
    new ApiResponse(code = 404, message = "Project not found")
  ))
  def generateGherkin(): Action[AnyContent] = Action { request =>
    try {
      val menu = menuService.getMenuTree()
      val menuItemMap = menuService.getMenu().map(c => c.id -> MenuService.findMenuSubtree(c.id)(menu)).toMap

      val projectGherkins = request.queryString.getOrElse("project", Seq()).flatMap { projectParam =>
        val params = projectParam.split(">")
        val hierarchy = params(0).split("_").toSeq.filterNot(slug => slug.isEmpty || slug == "root")
        val projectId = params(1)
        val project = projectRepository.findById(projectId)
        val projectName = project.map(_.name).getOrElse("")
        val branchName = params.lift(2) match {
          case Some(branchParam) if branchParam.nonEmpty => branchParam
          case _ => project.map(_.stableBranch).getOrElse("master")
        }

        val featureFilter = params.lift(3)
        val tagsFilter = params.lift(4).map(tagsAsString => tagsAsString.split(",").toSeq)




        MenuService.findMenuSubtree(hierarchy)(menu).flatMap(_.hierarchy.lastOption).flatMap { hierarchyNode =>
          menuItemMap.get(hierarchyNode.id).flatten.map { criteria =>
            buildGherkin(criteria.hierarchy, Seq(gherkinRepository.buildProjectGherkin(ProjectMenuItem(projectId, projectName, branchName, featureFilter, tagsFilter))))
          }
        }
      }

      val nodeGherkins = request.queryString.getOrElse("node", Seq()).flatMap { nodeParam =>
        val hierarchy = nodeParam.split("_").toSeq.filterNot(_.isEmpty)

        MenuService.findMenuSubtree(hierarchy)(menu).flatMap(_.hierarchy.lastOption).map(_.id).flatMap(menuItemMap.get).flatten.map(MenuService.mergeChildrenHierarchy).getOrElse(Seq()).flatMap { hierarchyNode =>
          menuItemMap.get(hierarchyNode.id).flatten.map { criteria =>
            criteria.projects.map { project =>
              buildGherkin(criteria.hierarchy, Seq(gherkinRepository.buildProjectGherkin(ProjectMenuItem(project.id, project.name, project.stableBranch))))
            }
          }
        }.flatten
      }

      val gherkins = projectGherkins ++ nodeGherkins

      if (gherkins.nonEmpty) {
        Ok(Json.toJson(gherkins.reduceLeft((acc: DocumentationDTO, current: DocumentationDTO) => acc.merge(current).getOrElse(acc))))

      } else {
        NotFound
      }
    } catch {
      case e: Exception =>
        logger.error(e.getMessage, e)
        BadRequest
    }
  }

  def buildGherkin(hierarchy: Seq[HierarchyNode], projects: Seq[ProjectDocumentationDTO]): DocumentationDTO = {
    hierarchy.toList match {
      case Nil => DocumentationDTO(hierarchyRepository.findAll().headOption.getOrElse(HierarchyNode("", "", "", "", "")), projects, Seq())
      case h :: Nil => DocumentationDTO(h, projects, Seq())
      case h :: tail => DocumentationDTO(h, Seq(), Seq(buildGherkin(tail, projects)))
    }

  }

}
