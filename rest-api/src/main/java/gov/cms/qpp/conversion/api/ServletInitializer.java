package gov.cms.qpp.conversion.api;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Initialize the servlet container for application configuration
 */
public class ServletInitializer extends SpringBootServletInitializer {
	/**
	 * Configures the application via configuration classes
	 *
	 * @param application builder for the application context
	 * @return application builder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RestApiApplication.class);
	}
}
