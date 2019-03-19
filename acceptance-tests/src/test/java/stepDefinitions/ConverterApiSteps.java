package stepDefinitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import static org.junit.Assert.*;

public class ConverterApiSteps {

	WebDriver driver;

	@Given("^User starts QPPCT API test")
	public void user_starts_api_tests() {
		System.out.println("This step opens the browser and launches the application.");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get("http://internal-qpp-qrda3converter-dev-app-2081849179.us-east-1.elb.amazonaws.com");

	}

	@When("User makes a Multipart POST request(?: to (.*))? with (.*?)(?: and (.*))?$")
	public void user_user_makes_multipart_post(String endpoint, String filepath, String filename) {
		System.out.println("Endpoint: " + endpoint);
		System.out.println("Filepath: " + filepath);
		System.out.println("filename: " + filename);
	}

	@Then("User receives (.*) response code")
	public void user_receives_response_201(String expected) {
		System.out.println("User Gets HTTP 201 response");
		assertEquals(expected, 201);
	}
}