package gov.cms.qpp.conversion.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import tools.jackson.databind.ObjectMapper;

import gov.cms.qpp.conversion.encode.JsonWrapper;

/**
 * Jackson configuration
 */
@Configuration
public class JacksonConfig {

	@Bean
	public ObjectMapper mapper() {
		return JsonWrapper.jsonMapper;
	}

}
