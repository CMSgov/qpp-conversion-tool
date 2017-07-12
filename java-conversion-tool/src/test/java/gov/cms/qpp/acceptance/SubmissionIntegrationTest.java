package gov.cms.qpp.acceptance;


import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SubmissionIntegrationTest {
	private static String qppJson;

	@BeforeClass
	public static void setup() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		Converter converter = new Converter(path);
		JsonWrapper qpp = converter.transform();

		int npi = new Random().nextInt(999999999);
		int tin = new Random().nextInt(999999);
		qppJson = qpp.toString().replace("0567891421", "0" + npi);
		qppJson = qppJson.replace("000456789", "000" + tin);
	}

	@Test
	public void testSubmissionApiGetSuccess() throws IOException {
		HttpEntity entity = new ByteArrayEntity(qppJson.getBytes("UTF-8"));
		HttpPost request = new HttpPost("https://qpp-submissions-sandbox.navapbc.com/v1/submissions");
		request.setHeader("Content-Type", "application/json");
		request.setEntity(entity);
		HttpResponse httpResponse = HttpClientBuilder.create().build().execute(request);
		int statusCode = httpResponse.getStatusLine().getStatusCode();

		assertThat("QPP submission should succeed", statusCode, is(201));
	}
}
