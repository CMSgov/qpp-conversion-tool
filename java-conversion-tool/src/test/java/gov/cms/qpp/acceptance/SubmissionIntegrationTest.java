package gov.cms.qpp.acceptance;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubmissionIntegrationTest {
	private static HttpClient client;
	private static String serviceUrl = "https://qpp-submissions-sandbox.navapbc.com/v1/submissions";

	@BeforeClass
	public static void setup() {
		client = HttpClientBuilder.create().build();
	}

	@Test
	public void testSubmissionApiPostSuccess() throws IOException {
		JsonWrapper qpp = loadQpp("../qrda-files/valid-QRDA-III-latest.xml");
		HttpResponse httpResponse = servicePost(qpp);
		cleanUp(httpResponse);

		assertThat("QPP submission should succeed", getStatus(httpResponse), is(201));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSubmissionApiPostFailure() throws IOException {
		JsonWrapper qpp = loadQpp("../qrda-files/valid-QRDA-III-latest.xml");
		Map<String, Object> obj = (Map<String, Object>) qpp.getObject();
		obj.remove("performanceYear");
		HttpResponse httpResponse = servicePost(qpp);

		assertThat("QPP submission should succeed", getStatus(httpResponse), is(422));
	}

	private JsonWrapper loadQpp(String qrdaPath) {
		Path path = Paths.get(qrdaPath);
		Converter converter = new Converter(path);
		JsonWrapper qpp = converter.transform();
		return qpp;
	}

	private HttpResponse servicePost(JsonWrapper qpp) throws IOException {
		HttpEntity entity = new ByteArrayEntity(qpp.toString().getBytes("UTF-8"));
		HttpPost request = new HttpPost(serviceUrl);
		request.setHeader("Content-Type", "application/json");
		request.setEntity(entity);
		return client.execute(request);
	}

	private void cleanUp(HttpResponse httpResponse) throws IOException {
		int statusCode = getStatus(httpResponse);
		if (statusCode == 201) {
			InputStream inStream = httpResponse.getEntity().getContent();
			Map json = JsonHelper.readJson(inStream, Map.class);
			String subId = (String) ((Map)((Map) json.get("data")).get("submission")).get("id");
			HttpDelete cleanUp = new HttpDelete(serviceUrl + "/" + subId);
			client.execute(cleanUp);
		}
	}

	private int getStatus(HttpResponse httpResponse) {
		return httpResponse.getStatusLine().getStatusCode();
	}
}
