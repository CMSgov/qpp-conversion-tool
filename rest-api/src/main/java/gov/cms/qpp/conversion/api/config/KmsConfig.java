package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KmsConfig {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	@Bean
	public AWSKMS awsKms() {
		AWSKMS client = null;

		try {
			client = AWSKMSClientBuilder.defaultClient();
		} catch (SdkClientException exception) {
			API_LOG.info("Default KMS client failed to build, trying again with region us-east-1", exception);
			client = AWSKMSClientBuilder.standard().withRegion("us-east-1").build();
		}

		return client;
	}
}
