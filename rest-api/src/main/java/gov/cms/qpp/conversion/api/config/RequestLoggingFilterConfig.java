package gov.cms.qpp.conversion.api.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for request logging.
 *
 * Note: CustomRequestLoggingFilter has been replaced by:
 * - RequestTracingFilter (request ID generation)
 * - StructuredRequestLoggingFilter (lifecycle logging)
 *
 * Both are @Component annotated and auto-registered.
 */
@Configuration
public class RequestLoggingFilterConfig {
    // Filters are now component-scanned; no @Bean definitions needed
}
