package gov.cms.qpp.conversion.api.config;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration file for Amazon S3.
 *
 * Configures {@link Bean}s associated with AWS S3.
 */
@Configuration
public class S3Config {

	private static final Logger API_LOG = LoggerFactory.getLogger(S3Config.class);

	/**
	 * Creates the S3 client {@link Bean}.
	 *
	 * Uses the default client, but if a region is unspecified, uses {@code us-east-1}.
	 *
	 * @return The S3 client.
	 */
	@Bean
	public S3Client s3client() {
		try {
			return S3Client.builder().build();
		} catch (SdkClientException exception) {
			API_LOG.info("Default S3 client failed to build, trying again with region us-east-1", exception);
			return planB();
		}
	}

	/**
	 * Returns the default client that uses {@code us-east-1}.
	 *
	 * @return The S3 client.
	 */
	S3Client planB() {
		return S3Client.builder().region(Region.US_EAST_1).build();
	}
}