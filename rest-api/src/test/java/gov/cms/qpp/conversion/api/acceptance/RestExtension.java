package gov.cms.qpp.conversion.api.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

public class RestExtension implements BeforeAllCallback, AfterAllCallback {

	private static final String BASE_URI_PROPERTY = "DOCKER_DEPLOY_HOSTS";
	private static final String DEPLOY_PORT_PROPERTY = "DOCKER_DEPLOY_PORT";
	private static final String ROOT_PATH = "/";

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		Optional<String> baseUri = getProperty(BASE_URI_PROPERTY);
		Optional<String> port = getProperty(DEPLOY_PORT_PROPERTY);

		RestAssured.port = Integer.parseInt(port.orElse("8080"));
		RestAssured.baseURI = baseUri.map(host -> "http://" + host).orElse(RestAssured.DEFAULT_URI);
		RestAssured.rootPath = ROOT_PATH;

		waitForIt(baseUri.orElse("localhost"), RestAssured.port);
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		RestAssured.reset();
	}

	private Optional<String> getProperty(String propertyName) {
		return Optional.ofNullable(System.getenv(propertyName));
	}

	private void waitForIt(String host, int port) {
		await().ignoreException(RuntimeException.class)
				.atMost(30, TimeUnit.SECONDS).until(() -> {
			if (checkPort(host, port)) {
				throw new RuntimeException("Not yet");
			} else {
				return true;
			}
		});
	}

	private boolean checkPort(String host, int port) {
		try (Socket ignored = new Socket(host, port)) {
			return false;
		} catch (IOException ignored) {
			return true;
		}
	}
}