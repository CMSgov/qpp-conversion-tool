package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DynamoDbConfig {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");

	private static final String DYNAMO_TABLE_NAME_ENV_VARIABLE = "DYNAMO_TABLE_NAME";

	@Autowired
	private Environment environment;

	@Bean
	public AmazonDynamoDB dynamoDbClient() {
		AmazonDynamoDB client = null;

		try {
			client = AmazonDynamoDBClientBuilder.defaultClient();
		} catch (SdkClientException exception) {
			API_LOG.info("Default DynamoDB client failed to build, trying again with region us-east-1", exception);
			client = AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
		}

		return client;
	}

	@Bean
	public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB dynamoDbClient) {
		DynamoDBMapper dynamoDBMapper = null;

		final String tableName = environment.getProperty(DYNAMO_TABLE_NAME_ENV_VARIABLE);
		if (tableName == null || tableName.isEmpty()) {
			API_LOG.warn("No DynamoDB table name is specified.");
			dynamoDBMapper = new DynamoDBMapper(dynamoDbClient);
		} else {
			API_LOG.info("Using DynamoDB table name {}.", tableName);
			DynamoDBMapperConfig.Builder dynamoConfigBuilder = DynamoDBMapperConfig.builder().withTableNameOverride(
				new DynamoDBMapperConfig.TableNameOverride(tableName));
			dynamoDBMapper = new DynamoDBMapper(dynamoDbClient, dynamoConfigBuilder.build());
		}

		return dynamoDBMapper;
	}
}
