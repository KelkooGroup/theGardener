import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("test/features"),
  glue = Array("steps"),
  tags = "@valid",
  plugin = Array("pretty", "html:target/cucumber-report.html", "json:target/cucumber-bdd.json")
)
class ValidBDDTest
