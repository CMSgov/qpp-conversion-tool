package gov.cms.qpp.conversion.api.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.Optional;

public class RestExtension implements BeforeAllCallback, AfterAllCallback {

	private static final String BASE_URI_PROPERTY = "DOCKER_DEPLOY_HOSTS";
	private static final Integer PORT = 8080;
	private static final String ROOT_PATH = "/";

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		Optional<String> baseUri = getSystemProperty(BASE_URI_PROPERTY);

		RestAssured.port = PORT;
		RestAssured.baseURI = baseUri.map(host -> "http://" + host).orElse(RestAssured.DEFAULT_URI);
		RestAssured.rootPath = ROOT_PATH;
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		RestAssured.reset();
	}

	private Optional<String> getSystemProperty(String propertyName) {
		return Optional.ofNullable(System.getProperty(propertyName));
	}
}