import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = "src/test/resources/features",plugin = {"pretty"}, monochrome = true, glue = "stepDefinitions")
public class CucumberRunnerTest {
}