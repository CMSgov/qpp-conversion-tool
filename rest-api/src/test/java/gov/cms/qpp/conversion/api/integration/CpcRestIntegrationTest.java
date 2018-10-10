package gov.cms.qpp.conversion.api.integration;

import gov.cms.qpp.conversion.api.SpringIntegrationTest;
import gov.cms.qpp.conversion.api.helper.JwtPayloadHelper;
import gov.cms.qpp.conversion.api.helper.JwtTestHelper;

import javax.inject.Inject;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringIntegrationTest
class CpcRestIntegrationTest {

	private static final String NOT_CPC = "not-CPC+";
	private static final String ORG_TYPE = "whatever";

	@Inject
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
	}

	@Test
	void testNoSecurityUnprocessedCpcFiles() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
			.get("/cpc/unprocessed-files"))
			.andExpect(status().is(403));
	}

	@Test
	void testNoSecurityGetCpcFile() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
			.get("/cpc/file/uuid"))
			.andExpect(status().is(403));
	}

	@Test
	void testNoSecurityProcessCpcFile() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders
			.put("/cpc/file/uuid"))
			.andExpect(status().is(403));
	}

	@Test
	void testIncorrectJwtNameUnprocessedCpcFiles() throws Exception {
		JwtPayloadHelper jwtPayload = new JwtPayloadHelper().withName(NOT_CPC).withOrgType(ORG_TYPE);

		mockMvc.perform(MockMvcRequestBuilders
			.get("/cpc/unprocessed-files").header("Authorization", JwtTestHelper.createJwt(jwtPayload)))
			.andExpect(status().is(403));
	}

	@Test
	void testIncorrectJwtNoOrgUnprocessedCpcFiles() throws Exception {
		JwtPayloadHelper jwtPayload = new JwtPayloadHelper().withName("cpc-test");

		mockMvc.perform(MockMvcRequestBuilders
			.get("/cpc/unprocessed-files").header("Authorization", JwtTestHelper.createJwt(jwtPayload)))
			.andExpect(status().is(403));
	}

	@Test
	void testIncorrectJwtGetCpcFile() throws Exception {
		JwtPayloadHelper jwtPayload = new JwtPayloadHelper().withName(NOT_CPC).withOrgType(ORG_TYPE);

		mockMvc.perform(MockMvcRequestBuilders
			.get("/cpc/file/uuid").header("Authorization", JwtTestHelper.createJwt(jwtPayload)))
			.andExpect(status().is(403));
	}

	@Test
	void testIncorrectJwtProcessCpcFile() throws Exception {
		JwtPayloadHelper jwtPayload = new JwtPayloadHelper().withName(NOT_CPC).withOrgType(ORG_TYPE);

		mockMvc.perform(MockMvcRequestBuilders
			.put("/cpc/file/uuid").header("Authorization", JwtTestHelper.createJwt(jwtPayload)))
			.andExpect(status().is(403));
	}
}
