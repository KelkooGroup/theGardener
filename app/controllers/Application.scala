package controllers

import javax.inject.Inject

import models.ServerSettings
import play.api.libs.json._
import play.api.mvc._
import views._

class Application @Inject()(dataSource: MyDataSource) extends InjectedController {

  def index = Action {
    Ok(html.index("Hello, the Gardener"))
  }

}

class MyDataSource {

  def someString: String = "Some random string"

  def someData: Seq[ServerSettings] = Seq()

}