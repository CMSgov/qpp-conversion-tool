package gov.cms.qpp.conversion.api.config;


import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {
	@Value("${jsa.aws.access_key_id}")
	private String awsId;

	@Value("${jsa.aws.secret_access_key}")
	private String awsKey;

	@Value("${jsa.s3.region}")
	private String region;

	@Bean
	public AmazonS3 s3client() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsId, awsKey);

		AWSCredentialsProvider credProvider = new AWSCredentialsProviderChain(
				new EnvironmentVariableCredentialsProvider(),
				new SystemPropertiesCredentialsProvider(),
				InstanceProfileCredentialsProvider.getInstance(),
				new AWSStaticCredentialsProvider(awsCreds));

		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withRegion(Regions.fromName(region))
				.withCredentials(credProvider)
				.build();

		return s3Client;
	}
}