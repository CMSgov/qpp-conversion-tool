package gov.cms.qpp.conversion.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * Spring configuration file for service task threadding.
 *
 * Configures {@link Bean}s associated with threadding.
 */
@Configuration
public class ConcurrencyConfig {

	/**
	 * A thread pool just for the ReST API.
	 *
	 * @return synchronous task executor
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		return new SyncTaskExecutor();
	}
}
