package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class SubmissionIntegrationTest {

	private static HttpClient client;
	private static final String SERVICE_URL = "https://val.qpp-submissions.navapbc.com/public/validate-submission";
	private JsonWrapper qpp;

	@BeforeAll
	@SuppressWarnings("unchecked")
	static void setup() {
		client = HttpClientBuilder.create().build();
	}

	private static boolean endpointIsUp(final HttpResponse response) {
		return response.getStatusLine().getStatusCode() < 500;
	}

	@BeforeEach
	void setupTest() {
		qpp = loadQpp();
	}

	@Test
	void testSubmissionApiPostSuccess() throws IOException {
		HttpResponse httpResponse = servicePost(qpp);
		Assumptions.assumeTrue(endpointIsUp(httpResponse), "Validation api is down");

		assertThat(getStatus(httpResponse)).isEqualTo(200);
	}

	@Test
	@SuppressWarnings("unchecked")
	void testSubmissionApiPostFailure() throws IOException {
		Map<String, Object> obj = (Map<String, Object>) qpp.getObject();
		obj.remove("performanceYear");
		HttpResponse httpResponse = servicePost(qpp);
		Assumptions.assumeTrue(endpointIsUp(httpResponse), "Validation api is down");

		assertWithMessage("QPP submission should be unprocessable")
				.that(getStatus(httpResponse))
				.isEqualTo(422);
	}

	private JsonWrapper loadQpp() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		Converter converter = new Converter(new PathSource(path));
		return converter.transform();
	}

	private HttpResponse servicePost(JsonWrapper qpp) throws IOException {
		HttpEntity entity = new ByteArrayEntity(qpp.toString().getBytes("UTF-8"));
		HttpPost request = new HttpPost(SERVICE_URL);
		request.setHeader("Content-Type", "application/json");
		request.setEntity(entity);
		return client.execute(request);
	}

	private int getStatus(HttpResponse httpResponse) {
		return httpResponse.getStatusLine().getStatusCode();
	}
}
