package steps

import cucumber.api.DataTable
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.mockito.MockitoSugar
import anorm._

class RegisterProjectSteps extends ScalaDsl with EN with MockitoSugar {

  import CommonSteps._

  Given("""^no project settings are setup in theGardener$""") { () =>
    db.withConnection{implicit connection =>
      SQL("TRUNCATE TABLE project").executeUpdate()
    }

  }



}
