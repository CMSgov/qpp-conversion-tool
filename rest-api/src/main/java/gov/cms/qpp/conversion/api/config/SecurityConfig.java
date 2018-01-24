package gov.cms.qpp.conversion.api.config;

import gov.cms.qpp.conversion.api.security.JwtAuthorizationFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Web Security Configuration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private static final String CPC_WILDCARD = "/cpc/**";

	@Value("${ORG_NAME:" + JwtAuthorizationFilter.DEFAULT_ORG_NAME + "}")
	protected String orgName;

	/**
	 * Configures the path to be authorized by the JWT token
	 *
	 * @param http Object that holds configuration
	 * @throws Exception check for any Exception that may occur
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.antMatcher(CPC_WILDCARD).authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.addFilter(new JwtAuthorizationFilter(authenticationManager(), orgName))
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().cors()
				.and().csrf().disable();
	}

}
