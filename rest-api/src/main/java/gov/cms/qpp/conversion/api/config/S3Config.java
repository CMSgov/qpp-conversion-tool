package gov.cms.qpp.conversion.api.config;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

	@Bean
	public AmazonS3 s3client() {
		return AmazonS3ClientBuilder.defaultClient();
	}

	@Bean
	public TransferManager s3TransferManager(AmazonS3 s3Client) {
		return TransferManagerBuilder.standard().withS3Client(s3Client).build();
	}
}