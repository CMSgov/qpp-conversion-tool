package stepDefinitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import model.ErrorHandler;
import model.Response;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;

public class ConverterApiSteps {
	Response  testResponse = new Response();

	@Given("^User starts QPPCT API test")
	public void user_starts_api_tests() {
	}

	@When("User makes a Multipart POST request(?: to (.*))? with (.*?)(?: and (.*))?$")
	public void user_user_makes_multipart_post(String endpoint, String filepath, String filename) {
		MultiValueMap<String, Object> body
			= new LinkedMultiValueMap<>();
		body.add("file", new FileSystemResource(filepath));

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Accept", "application/vnd.qpp.cms.gov.v2+json");

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
				String serverUrl = "http://internal-qpp-qrda3converter-dev-app-2081849179.us-east-1.elb.amazonaws.com";
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ErrorHandler());
		ResponseEntity<String> response = restTemplate
				.postForEntity(serverUrl, requestEntity, String.class);
			testResponse.setStatus(response.getStatusCode().value());
	}

	@Then("User receives (.*) response code")
	public void user_receives_response_201(int expected) {
		assertEquals(expected, testResponse.getStatus());
	}
}