package gov.cms.qpp.conversion.api.config;

import gov.cms.qpp.conversion.api.security.JwtAuthorizationFilter;

import javax.inject.Inject;

import com.google.common.truth.Truth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SecurityConfig.class)
public class SecurityConfigTest {

	@Inject
	private SecurityConfig config;

	@Test
	public void testUsesDefaultOrgName() {
		Truth.assertThat(config.orgName).isEqualTo(JwtAuthorizationFilter.DEFAULT_ORG_NAME);
	}

}
