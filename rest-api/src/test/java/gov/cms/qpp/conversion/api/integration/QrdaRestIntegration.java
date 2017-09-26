package gov.cms.qpp.conversion.api.integration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WebAppConfiguration
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:test.properties")
public class QrdaRestIntegration {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void shouldBeHealthy() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/health"))
				.andExpect(status().is(200));
	}

	@Test
	public void testDefaultValidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		mockMvc.perform(MockMvcRequestBuilders
			.fileUpload("/").file(qrda3File))
			.andExpect(status().is(201))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.taxpayerIdentificationNumber").exists());
	}

	@Test
	public void testValidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/valid-QRDA-III-latest.xml")));
		mockMvc.perform(MockMvcRequestBuilders
				.fileUpload("/").file(qrda3File).accept("application/vnd.qpp.cms.gov.v1+json"))
				.andExpect(status().is(201))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.taxpayerIdentificationNumber").exists());
	}


	@Test
	public void testInvalidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/not-a-QDRA-III-file.xml")));
		mockMvc.perform(MockMvcRequestBuilders
			.fileUpload("/").file(qrda3File))
			.andExpect(status().is(422))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.errors").exists());
	}

	@Test
	public void testInvalidAcceptHeader() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/not-a-QDRA-III-file.xml")));
		mockMvc.perform(MockMvcRequestBuilders
				.fileUpload("/").file(qrda3File)
				.accept("application/vnd.qpp.cms.gov.v2+json"))
				.andExpect(status().is(406));
	}

	@Test
	public void shouldFailForSubmissionApiValidation() throws Exception {
		String file = "../converter/src/test/resources/cpc_plus/CPCPlus_CMSPrgrm_LowerCase_SampleQRDA-III-success.xml";
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get(file)));
		mockMvc.perform(MockMvcRequestBuilders
				.fileUpload("/").file(qrda3File))
			.andExpect(status().is(422))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.errors").exists());
	}
}
