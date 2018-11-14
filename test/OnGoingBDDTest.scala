import cucumber.api._
import cucumber.api.junit._
import org.junit._
import org.junit.runner._

@RunWith(classOf[Cucumber])
@Ignore
@CucumberOptions(
  features = Array("test/features"),
  glue = Array("steps"),
  tags = Array("@ongoing"),
  plugin = Array("pretty", "html:target/cucumber-report", "json:target/cucumber-bdd.json")
)
class OnGoingBDDTest