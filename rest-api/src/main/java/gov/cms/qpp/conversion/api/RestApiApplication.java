package gov.cms.qpp.conversion.api;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

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
	public static void main(String... args) {
		SpringApplication.run(RestApiApplication.class, args);
	}
}
