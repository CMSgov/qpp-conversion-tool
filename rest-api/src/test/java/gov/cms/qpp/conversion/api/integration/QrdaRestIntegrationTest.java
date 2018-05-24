package gov.cms.qpp.conversion.api.integration;

import gov.cms.qpp.conversion.api.SpringIntegrationTest;
import gov.cms.qpp.conversion.api.SpringTest;
import gov.cms.qpp.conversion.api.model.Constants;

import javax.inject.Inject;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringIntegrationTest
public class QrdaRestIntegrationTest {

	@Inject
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void testDefaultValidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		mockMvc.perform(MockMvcRequestBuilders
			.multipart("/").file(qrda3File))
			.andExpect(status().is(201))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.taxpayerIdentificationNumber").exists());
	}

	@Test
	void testValidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		mockMvc.perform(MockMvcRequestBuilders
				.multipart("/").file(qrda3File).accept(Constants.V1_API_ACCEPT))
				.andExpect(status().is(201))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.taxpayerIdentificationNumber").exists());
	}


	@Test
	void testInvalidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/not-a-QDRA-III-file.xml")));
		mockMvc.perform(MockMvcRequestBuilders
			.multipart("/").file(qrda3File))
			.andExpect(status().is(422))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.errors").exists());
	}

	@Test
	void testInvalidAcceptHeader() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/not-a-QDRA-III-file.xml")));
		mockMvc.perform(MockMvcRequestBuilders
				.multipart("/").file(qrda3File)
				.accept("application/vnd.qpp.cms.gov.v2+json"))
				.andExpect(status().is(406));
	}

	@Test
	void shouldFailForSubmissionApiValidation() throws Exception {
		String file = "../rest-api/src/test/resources/fail_validation.xml";
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get(file)));
		mockMvc.perform(MockMvcRequestBuilders
				.multipart("/").file(qrda3File))
			.andExpect(status().is(422))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.errors[0].type").value("ValidationError"));
	}
}
