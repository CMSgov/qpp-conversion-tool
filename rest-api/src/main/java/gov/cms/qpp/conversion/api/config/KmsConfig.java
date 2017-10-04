package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import gov.cms.qpp.conversion.api.model.Constants;
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

	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);

	/**
	 * Creates the KMS client {@link Bean}.
	 *
	 * Uses the default client, but if a region is unspecified, uses {@code us-east-1}.
	 *
	 * @return The KMS client.
	 */
	@Bean
	public AWSKMS awsKms() {
		AWSKMS client = null;

		try {
			client = AWSKMSClientBuilder.defaultClient();
		} catch (SdkClientException exception) {
			API_LOG.info("Default KMS client failed to build, trying again with region us-east-1", exception);
			client = planB();
		}

		return client;
	}

	/**
	 * Returns the default client that uses {@code us-east-1}.
	 *
	 * @return The KMS client.
	 */
	AWSKMS planB() {
		return AWSKMSClientBuilder.standard().withRegion("us-east-1").build();
	}
}
