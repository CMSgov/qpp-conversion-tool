package gov.cms.qpp.acceptance;


import com.jayway.jsonpath.PathNotFoundException;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubmissionIntegrationTest {
	private static HttpClient client;
	private static String serviceUrl = "https://qpp-submissions-sandbox.navapbc.com/submissions";
	private JsonWrapper qpp;

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setup() throws IOException {
		client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(serviceUrl);
		request.setHeader("qpp-taxpayer-identification-number", "000777777");
		request.setHeader("Content-Type", "application/json");
		HttpResponse response = client.execute(request);
		List<Map> subs;
		try {
			subs = JsonHelper.readJsonAtJsonPath(response.getEntity().getContent(), "$.data.submissions", List.class);
		} catch (PathNotFoundException exception) {
			subs = new ArrayList<>();
		}
		subs.forEach( sub -> {
			String subId = sub.get("id").toString();
			deleteSubmission(subId);
		});
	}

	@Before
	public void setupTest() {
		qpp = loadQpp();
	}

	@Test
	@Ignore
	public void testSubmissionApiPostSuccess() throws IOException {
		HttpResponse httpResponse = servicePost(qpp);
		cleanUp(httpResponse);

		assertThat("QPP submission should succeed", getStatus(httpResponse), is(201));
	}

	@Test
	@Ignore
	@SuppressWarnings("unchecked")
	public void testSubmissionApiPostFailure() throws IOException {
		Map<String, Object> obj = (Map<String, Object>) qpp.getObject();
		obj.remove("performanceYear");
		HttpResponse httpResponse = servicePost(qpp);

		assertThat("QPP submission should succeed", getStatus(httpResponse), is(422));
	}

	private JsonWrapper loadQpp() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		Converter converter = new Converter(path);
		return converter.transform();
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
			deleteSubmission(subId);
		}
	}

	private static void deleteSubmission(String subId ) {
		try {
			HttpDelete cleanUp = new HttpDelete(serviceUrl + "/" + subId);
			client.execute(cleanUp);
		} catch (Exception ex) {
			//don't care
		}

	}

	private int getStatus(HttpResponse httpResponse) {
		return httpResponse.getStatusLine().getStatusCode();
	}
}
