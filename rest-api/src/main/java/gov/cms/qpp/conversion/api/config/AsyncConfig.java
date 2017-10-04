package gov.cms.qpp.conversion.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import gov.cms.qpp.conversion.util.EnvironmentHelper;

/**
 * Spring configuration file for service task threadding.
 *
 * Configures {@link Bean}s associated with threadding.
 */
@Configuration
public class AsyncConfig {

	public static final String USE_SYNC_EXECUTOR = "USE_SYNC_EXECUTOR";
	public static final String POOL_SIZE_VARIABLE = "SERVICE_THREAD_POOL_SIZE";
	public static final String POOLED_THREAD_PREFIX = "QppConversionRestApi-";

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
		executor.setCorePoolSize(5);
		executor.setThreadNamePrefix(POOLED_THREAD_PREFIX);
		executor.initialize();
		return executor;
	}

	private boolean isSync() {
		return EnvironmentHelper.isPresent(USE_SYNC_EXECUTOR);
	}
}
