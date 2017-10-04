package gov.cms.qpp.conversion.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;

/**
 * Spring configuration file for Amazon S3.
 *
 * Configures {@link Bean}s associated with S3.
 */
@Configuration
public class S3Config {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	/**
	 * Creates the S3 client {@link Bean}.
	 *
	 * Uses the default client, but if a region is unspecified, uses {@code us-east-1}.
	 *
	 * @return The S3 client.
	 */
	@Bean
	public AmazonS3 s3client() {
		AmazonS3 client = null;

		try {
			client = AmazonS3ClientBuilder.defaultClient();
		} catch (SdkClientException exception) {
			API_LOG.info("Default S3 client failed to build, trying again with region us-east-1", exception);
			client = AmazonS3ClientBuilder.standard().withRegion("us-east-1").build();
		}

		return client;
	}

	/**
	 * Creates a standard Transfer Manager {@link Bean} based on an available {@link AmazonS3} {@link Bean}.
	 *
	 * @param s3Client The {@link AmazonS3} {@link Bean}.
	 * @return The S3 Transfer Manager.
	 */
	@Bean
	public TransferManager s3TransferManager(AmazonS3 s3Client) {
		return TransferManagerBuilder.standard().withS3Client(s3Client).build();
	}
}