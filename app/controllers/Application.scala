package controllers

import javax.inject.Inject

import models.MyModel
import play.api.libs.json._
import play.api.mvc._
import views._

class Application @Inject()(dataSource: MyDataSource) extends InjectedController {

  val HelloWorldMessage = "Hello, world"

  def index = Action {
    Ok(html.index(dataSource.someString))
  }

  def example = Action {
    Ok(html.example(dataSource.someString))
  }

  def helloJson = Action {
    Ok(Json.obj("message" -> HelloWorldMessage))
  }

  def dataJson = Action {
    Ok(Json.toJson(dataSource.someData))
  }

}

class MyDataSource {

  def someString: String = "Some random string"

  def someData: Seq[MyModel] = Seq(
    MyModel("The universal answer", 42),
    MyModel("Emergency number", 911)
  )

}