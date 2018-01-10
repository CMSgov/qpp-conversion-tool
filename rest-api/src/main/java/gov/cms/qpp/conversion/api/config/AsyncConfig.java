package gov.cms.qpp.conversion.api.config;

import gov.cms.qpp.conversion.api.model.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Spring configuration file for service task threadding.
 *
 * Configures {@link Bean}s associated with threadding.
 */
@Configuration
public class AsyncConfig {

	public static final String POOLED_THREAD_PREFIX = "QppConversionRestApi-";

	@Autowired
	private Environment environment;

	/**
	 * A thread pool just for the ReST API.
	 *
	 * @return The thread pool, or synchronous task executor if desired.
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		if (isSync()) {
			return new SyncTaskExecutor();
		}

		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(15);
		executor.setThreadNamePrefix(POOLED_THREAD_PREFIX);
		executor.initialize();
		return executor;
	}

	private boolean isSync() {
		return environment.containsProperty(Constants.USE_SYNC_EXECUTOR);
	}
}
