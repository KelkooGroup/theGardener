package controllers

import controllers.dto._
import io.swagger.annotations._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc._
import services.MenuService


@Api(value = "MenuController", produces = "application/json")
class MenuController @Inject()(menuService: MenuService) extends InjectedController {

  @ApiOperation(value = "Get all menu items", response = classOf[MenuDTO], responseContainer = "list")
  def getMenu(): Action[AnyContent] = Action {

    Ok(Json.toJson(MenuDTO(menuService.getMenuTree())))
  }
}
