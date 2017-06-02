package gov.cms.qpp.conversion.api.integration;

import gov.cms.qpp.conversion.api.RestApiApplication;
import gov.cms.qpp.conversion.api.controllers.v1.QrdaControllerV1;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RestApiApplication.class)
//@ContextConfiguration(classes = RestApiApplication.class)
@WebAppConfiguration
@RunWith(SpringRunner.class)
public class QrdaRestTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Mock
	private QrdaService qrdaService;

	@InjectMocks
	private QrdaControllerV1 qrdaControllerV1;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		MockitoAnnotations.initMocks(this);
		JsonWrapper jsonWrapper = new JsonWrapper();
		jsonWrapper.putString("key", "mockJsonWrapper");
		when(qrdaService.convertQrda3ToQpp(any(InputStream.class))).thenReturn(jsonWrapper);
	}

	@Test
	public void testValidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/valid-QRDA-III.xml")));
		mockMvc.perform(MockMvcRequestBuilders
			.fileUpload("/v1/qrda3").file(qrda3File))
			.andExpect(status().is(201))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.taxpayerIdentificationNumber").exists());
	}

	@Test
	public void testInvalidQpp() throws Exception {
		MockMultipartFile qrda3File = new MockMultipartFile("file", Files.newInputStream(Paths.get("../qrda-files/not-a-QDRA-III-file.xml")));
		mockMvc.perform(MockMvcRequestBuilders
			.fileUpload("/v1/qrda3").file(qrda3File))
			.andExpect(status().is(422))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.errorSources").exists());
	}
}
