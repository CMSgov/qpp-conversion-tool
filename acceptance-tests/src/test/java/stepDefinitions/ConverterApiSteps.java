package stepDefinitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;
import static org.junit.Assert.*;

public class ConverterApiSteps {

	@Given("^User starts QPPCT API test")
	public void user_starts_api_tests() {
//		$response = nil
//		$userObj = nil
//		$authToken = nil
//
//		app ? @app = app : @app = 'QPPWI'
//		rootURL = UserUtil.get_homepage(@app)
//
//		# removes cookie from URL if it exists
//		if rootURL.include? "?"
//		$url = rootURL.slice(0..(rootURL.index('?') - 1))
//		else
//		$url = rootURL
//		end
//			end
	}

	@When("User makes a Multipart POST request(?: to (.*))? with (.*?)(?: and (.*))?$")
	public void user_user_makes_multipart_post() {

	}

	@Then("User receives (.*) response code")
	public void user_receives_response_201(String expected) {
		assertEquals(expected, 201);
	}
}