package gov.cms.qpp.conversion.integration;


import gov.cms.qpp.conversion.api.RestApiApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApiApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"VALIDATION_URL = https://qpp-submissions-sandbox.navapbc.com/submissions/validate"})
public class ConverterIntegration {
	private String urlBase;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setup() throws InterruptedException {
		urlBase = "http://localhost:" + port + "/";
	}

	@Test
	public void shouldBeHealthy() throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(urlBase + "/health", String.class);
		assertTrue("Health endpoint is not healthy", response.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void shouldSucceedForValidQrda() throws Exception {
		HttpEntity<LinkedMultiValueMap<String, Object>> entity =
				generateEntity("../qrda-files/valid-QRDA-III-latest.xml");
		ResponseEntity<String> response = restTemplate.exchange(urlBase, HttpMethod.POST, entity, String.class);
		assertTrue("Valid QRDA did not succeed", response.getStatusCode().is2xxSuccessful());
	}

	@Test
	public void shouldFailForInvalidQrda() throws Exception {
		HttpEntity<LinkedMultiValueMap<String, Object>> entity =
				generateEntity("../qrda-files/not-a-QDRA-III-file.xml");
		ResponseEntity<String> response = restTemplate.exchange(urlBase, HttpMethod.POST, entity, String.class);
		assertTrue("Valid QRDA did not fail", response.getStatusCode().is4xxClientError());
	}

	@Test
	public void shouldFailForSubmissionApiValidation() throws Exception {
		HttpEntity<LinkedMultiValueMap<String, Object>> entity =
				generateEntity("../converter/src/test/resources/cpc_plus/CPCPlus_CMSPrgrm_LowerCase_SampleQRDA-III-success.xml");
		ResponseEntity<String> response = restTemplate.exchange(urlBase, HttpMethod.POST, entity, String.class);
		assertTrue("Valid QRDA did not fail", response.getStatusCode().is4xxClientError());
	}

	private HttpEntity<LinkedMultiValueMap<String, Object>> generateEntity(String filePath) throws IOException {
		LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
		Path path = Paths.get(filePath);
		byte[] payload = Files.readAllBytes(path);

		Resource xmlFile = new ByteArrayResource(payload){
			@Override
			public String getFilename(){
				return "file.xml";
			}
		};

		HttpHeaders xmlHeaders = new HttpHeaders();
		xmlHeaders.setContentType(MediaType.APPLICATION_XML);

		HttpEntity<Resource> xmlEntity = new HttpEntity<>(xmlFile, xmlHeaders);

		parameters.add("file", xmlEntity);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentLength(payload.length);

		return new HttpEntity<>(parameters, headers);
	}

}
