package gov.cms.qpp.conversion.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Application to be ran
 */
@SpringBootApplication
@EnableDynamoDBRepositories(basePackages = "gov.cms.qpp.conversion.api.repositories")
@EnableAsync
public class RestApiApplication {
	/**
	 * Main method to run the application
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		AmazonDynamoDB amazonDynamoDB =  AmazonDynamoDBClient.builder().build();

		return amazonDynamoDB;
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
