package gov.cms.qpp.conversion.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
}
