package controllers

import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import services.MenuService


@Api(value = "MenuController", produces = "application/json")
class MenuController @Inject()(menuService: MenuService) extends InjectedController {

  @ApiOperation(value = "Get all menu items", response = classOf[MenuDTO])
  def getMenu(): Action[AnyContent] = Action {
    Ok(Json.toJson(MenuDTO(menuService.getMenuTree())))
  }

  @ApiOperation(value = "Get menu header", response = classOf[MenuDTO])
  def getMenuHeader(): Action[AnyContent] = Action {
    val menu = menuService.getMenuTree()

    val menuHeader = MenuDTO.header(menu).copy(children = Some(menu.children.map(MenuDTO.header)))

    Ok(Json.toJson(menuHeader))
  }

  @ApiOperation(value = "Get submenu", response = classOf[MenuDTO])
  @ApiResponses(Array(new ApiResponse(code = 404, message = "Menu not found")))
  def getSubMenu(hierarchy: String): Action[AnyContent] = Action {

    val menu = MenuService.findMenuSubtree(hierarchy.split("_").toSeq.filterNot(_.isEmpty))(menuService.getMenuTree())

    menu.map(m => Ok(Json.toJson(MenuDTO(m)))).getOrElse(NotFound(s"No menu $hierarchy"))
  }
}
