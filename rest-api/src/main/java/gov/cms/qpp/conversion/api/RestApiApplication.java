package gov.cms.qpp.conversion.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Application to be ran
 */
@SpringBootApplication
public class RestApiApplication {
	/**
	 * Main method to run the application
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

	/**
	 * A thread pool just for the ReST API.
	 *
	 * @return The thread pool.
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(3);
		executor.setThreadNamePrefix("QppConversionRestApi-");
		executor.initialize();
		return executor;
	}
}
