package gov.cms.qpp.conversion.api.config;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration file.
 *
 * Configures {@link Bean}s associated with AWS KMS.
 */
@Configuration
public class KmsConfig {

	private static final Logger API_LOG = LoggerFactory.getLogger(KmsConfig.class);

	/**
	 * Creates the KMS client {@link Bean}.
	 *
	 * Uses the default client, but if a region is unspecified, uses {@code us-east-1}.
	 *
	 * @return The KMS client.
	 */
	@Bean
	public KmsClient awsKms() {
		try {
			return KmsClient.builder().build();
		} catch (SdkClientException exception) {
			API_LOG.info("Default KMS client failed to build, trying again with region us-east-1", exception);
			return planB();
		}
	}

	/**
	 * Returns the default client that uses {@code us-east-1}.
	 *
	 * @return The KMS client.
	 */
	KmsClient planB() {
		return KmsClient.builder().region(Region.US_EAST_1).build();
	}
}
