package gov.cms.qpp.conversion.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import gov.cms.qpp.conversion.api.security.JwtAuthorizationFilter;

import java.util.Set;

/**
 * Web Security Configuration
 */
@Configuration
@EnableWebSecurity
@CrossOrigin(origins="*")
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private static final String PCF_WILDCARD = "/pcf/**";

    @Value("${ORG_NAME:" + JwtAuthorizationFilter.DEFAULT_ORG_NAME + "}")
	protected String orgName;

	@Value("${RTI_ORG_NAME:" + JwtAuthorizationFilter.DEFAULT_RTI_ORG + "}")
	protected String rtiOrgName;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.securityMatcher(PCF_WILDCARD)
            .authorizeRequests()
			.anyRequest().authenticated()
			.and()
            .csrf(csrf -> csrf.disable())
            .addFilterAt(new JwtAuthorizationFilter(Set.of(orgName, rtiOrgName)), BasicAuthenticationFilter.class)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.headers(headers -> headers
				.contentSecurityPolicy(csp -> csp
					.policyDirectives("script-src 'self'")
				)
            );

        return http.build();
    }
}