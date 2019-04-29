package stepDefinitions;

import com.jayway.jsonpath.TypeRef;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import model.ErrorHandler;
import model.EnvironmentDataHolder;
import model.TestResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import gov.cms.qpp.conversion.util.EnvironmentHelper;
import gov.cms.qpp.conversion.util.JsonHelper;

import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class ConverterApiSteps {

	TestResponse testResponse = new TestResponse();
	EnvironmentDataHolder environmentDataHolder = new EnvironmentDataHolder();

	@Given("^User starts QPPCT API test")
	public void user_starts_api_tests() {
		environmentDataHolder.setServerUrl(EnvironmentHelper.get("environment_url"));
		environmentDataHolder.setEnvironment(EnvironmentHelper.get("vpc_name"));
		environmentDataHolder.setServerCookie(EnvironmentHelper.get("environment_cookie"));
		if (StringUtils.isEmpty(environmentDataHolder.getServerUrl())) {
			environmentDataHolder.setServerUrl("http://localhost:8080/");
		}
	}

	@When("User makes a Multipart POST request(?: to (.*))? with (.*?)(?: and (.*))?$")
	public void user_user_makes_multipart_post(String endpoint, String filepath, String filename) {
		ClassPathResource classPathResource = new ClassPathResource(filepath);
		MultiValueMap<String, Object> body
			= new LinkedMultiValueMap<>();
		body.add("file", classPathResource);

		MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
		headers.add("Accept", "application/vnd.qpp.cms.gov.v2+json");


		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
		//String serverUrl = "http://internal-qpp-qrda3converter-dev-app-2081849179.us-east-1.elb.amazonaws.com";
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ErrorHandler());
		ResponseEntity<String> response = restTemplate
				.postForEntity(environmentDataHolder.getServerUrl(), requestEntity, String.class);
			testResponse.setStatus(response.getStatusCode().value());
			testResponse.setJsonResponse(response.getBody());
	}

	@Then("User receives (.*) response code")
	public void user_receives_response_201(int expected) {
		assertEquals(expected, testResponse.getStatus());
	}

	@And("the JSON response at (.*) should not be (.*)")
	public void json_response_should_not_equal_to(String jsonPath, String unexpected) {
		List<Map<String, String>> acceptedJsonResponse =
			JsonHelper.readJsonAtJsonPath(testResponse.getJsonResponse(), jsonPath, new TypeRef<List<Map<String, String>>>() { });
		assertNotEquals(unexpected, acceptedJsonResponse);
	}

	@And("the JSON response at (.*) should never contain (.*)")
	public void json_response_should_not_contain(String jsonPath, String unexpected) {
		List<String> acceptedJsonResponse =
			JsonHelper.readJsonAtJsonPath(testResponse.getJsonResponse(), jsonPath, new TypeRef<List<String>>() { });
		assertThat(acceptedJsonResponse, not(hasItem(unexpected)));
	}

	@And("the JSON response at (.*) should contain (.*)")
	public void json_response_could_contain_this_many(String jsonPath, String expected) {
		List<String> acceptedJsonResponse =
			JsonHelper.readJsonAtJsonPath(testResponse.getJsonResponse(), jsonPath, new TypeRef<List<String>>() { });
		assertThat(acceptedJsonResponse, hasItem(expected));

	}
}