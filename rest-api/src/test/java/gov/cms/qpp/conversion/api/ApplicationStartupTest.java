package gov.cms.qpp.conversion.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.google.common.truth.Truth.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
	properties = {"server.ssl.enabled=false", "NO_AUDIT=true"})
class ApplicationStartupTest {

	@LocalServerPort
	private int port;

	@Test
	void healthAndOpenApiEndpointsAreAvailableAfterStartup() throws Exception {
		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> health = client.send(
			HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/health")).GET().build(),
			HttpResponse.BodyHandlers.ofString());
		HttpResponse<String> apiDocs = client.send(
			HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/v3/api-docs")).GET().build(),
			HttpResponse.BodyHandlers.ofString());

		assertThat(health.statusCode()).isEqualTo(200);
		assertThat(apiDocs.statusCode()).isEqualTo(200);
		assertThat(apiDocs.body()).contains("\"/health\"");
	}
}
