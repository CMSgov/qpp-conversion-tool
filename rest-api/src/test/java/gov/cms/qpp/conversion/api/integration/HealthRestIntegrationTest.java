package gov.cms.qpp.conversion.api.integration;

import gov.cms.qpp.conversion.api.SpringTest;

import javax.inject.Inject;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringTest
public class HealthRestIntegrationTest {

	@Inject
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void shouldBeHealthy() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET, "/health"))
			.andExpect(status().is(200))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.environmentVariables").exists())
			.andExpect(jsonPath("$.systemProperties").exists())
			.andExpect(jsonPath("$.implementationVersion").exists());
	}
}
