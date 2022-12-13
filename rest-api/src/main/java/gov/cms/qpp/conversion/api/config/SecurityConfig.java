package gov.cms.qpp.conversion.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import gov.cms.qpp.conversion.api.security.JwtAuthorizationFilter;

import java.util.Set;

/**
 * Web Security Configuration
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String PCF_WILDCARD = "/pcf/**";

	@Value("${ORG_NAME:" + JwtAuthorizationFilter.DEFAULT_ORG_NAME + "}")
	protected String orgName;

	@Value("${RTI_ORG_NAME:" + JwtAuthorizationFilter.DEFAULT_RTI_ORG + "}")
	protected String rtiOrgName;

	/**
	 * Configures the path to be authorized by the JWT token
	 *
	 * @param http Object that holds configuration
	 * @throws Exception check for any Exception that may occur
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.requestMatchers().antMatchers(PCF_WILDCARD)
			.and()
			.authorizeRequests()
			.anyRequest().authenticated()
			.and()
			.addFilter(new JwtAuthorizationFilter(authenticationManager(), Set.of(orgName, rtiOrgName)))
			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and().cors()
			.and().csrf().disable();
	}
}
