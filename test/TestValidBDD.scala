import cucumber.api.CucumberOptions
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  features = Array("test/features"),
  glue = Array("steps"),
  tags = Array("@valid"),
  plugin = Array("pretty", "html:target/cucumber-report", "json:target/cucumber-bdd.json")
)
class TestValidBDD {
}