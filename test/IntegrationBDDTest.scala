import io.cucumber.junit.{Cucumber, CucumberOptions}
import org.junit.Ignore
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("test/features"),
  glue = Array("steps"),
  tags = "@integration",
  plugin = Array("pretty", "html:target/cucumber-report.html", "json:target/cucumber-bdd.json")
)
@Ignore
class IntegrationBDDTest
