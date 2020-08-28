import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.runner._

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("test/features"),
  glue = Array("steps"),
  tags = "@ongoing",
  plugin = Array("pretty", "html:target/cucumber-report", "json:target/cucumber-bdd.json")
)
class OnGoingBDDTest


