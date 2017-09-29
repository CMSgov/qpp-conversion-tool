package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.kms.AWSKMS;
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

	private static final String KMS_KEY_ENV_VARIABLE = "KMS_KEY";

	@Autowired
	private Environment environment;

	@Autowired
	private AWSKMS awsKms;

	@Bean
	public AmazonDynamoDB dynamoDbClient() {
		AmazonDynamoDB client = null;

		try {
			client = AmazonDynamoDBClientBuilder.defaultClient();
		} catch (SdkClientException exception) {
			API_LOG.info("Default DynamoDB client failed to build, trying again with region us-east-1", exception);
			client = planB();
		}

		return client;
	}

	AmazonDynamoDB planB() {
		return AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
	}

	@Bean
	public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB dynamoDbClient) {
		DynamoDBMapper dynamoDBMapper = null;

		final String tableName = environment.getProperty(DYNAMO_TABLE_NAME_ENV_VARIABLE);
		final String kmsKey = environment.getProperty(KMS_KEY_ENV_VARIABLE);
		final boolean tableNameSpecified = (tableName != null && !tableName.isEmpty());
		final boolean kmsKeySpecified = (kmsKey != null && !kmsKey.isEmpty());

		if (tableNameSpecified && kmsKeySpecified) {
			API_LOG.info("Using DynamoDB table name {} and KMS key {}.", tableName, kmsKey);
			dynamoDBMapper = new DynamoDBMapper(
				dynamoDbClient,
				tableNameOverrideConfig(tableName),
				encryptionTransformer(kmsKey));
		} else if (tableNameSpecified) {
			API_LOG.warn("Using DynamoDB table name {}, but no encryption specified.", tableName);
			dynamoDBMapper = new DynamoDBMapper(
				dynamoDbClient,
				tableNameOverrideConfig(tableName));
		} else if (kmsKeySpecified) {
			API_LOG.warn("Using KMS key {}, but no DynamoDB table name specified.", tableName);
			dynamoDBMapper = new DynamoDBMapper(
				dynamoDbClient,
				DynamoDBMapperConfig.builder().build(),
				encryptionTransformer(kmsKey));
		} else {
			API_LOG.warn("No DynamoDB table name nor encryption are specified.");
			dynamoDBMapper = new DynamoDBMapper(dynamoDbClient);
		}

		return dynamoDBMapper;
	}

	private DynamoDBMapperConfig tableNameOverrideConfig(String tableName) {
		return DynamoDBMapperConfig.builder().withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName))
			.build();
	}

	private AttributeTransformer encryptionTransformer(String kmsKey) {
		return new AttributeEncryptor(new DirectKmsMaterialProvider(awsKms, kmsKey));
	}
}
