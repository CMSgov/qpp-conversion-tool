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

import java.util.Optional;

@Configuration
public class DynamoDbConfig {

	private static final Logger API_LOG = LoggerFactory.getLogger("API_LOG");
	static final String DYNAMO_TABLE_NAME_ENV_VARIABLE = "DYNAMO_TABLE_NAME";
	static final String KMS_KEY_ENV_VARIABLE = "KMS_KEY";

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

		final Optional<String> kmsKey = getOptionalProperty(KMS_KEY_ENV_VARIABLE);
		final Optional<String> tableName = getOptionalProperty(DYNAMO_TABLE_NAME_ENV_VARIABLE);

		if (tableName.isPresent() && kmsKey.isPresent()) {
			API_LOG.info("Using DynamoDB table name {} and KMS key {}.", tableName, kmsKey);
			dynamoDBMapper = new DynamoDBMapper(
				dynamoDbClient,
				tableNameOverrideConfig(tableName.get()),
				encryptionTransformer(kmsKey.get()));
		} else if (tableName.isPresent()) {
			API_LOG.warn("Using DynamoDB table name {}, but no encryption specified.", tableName);
			dynamoDBMapper = new DynamoDBMapper(
				dynamoDbClient,
				tableNameOverrideConfig(tableName.get()));
		} else if (kmsKey.isPresent()) {
			API_LOG.warn("Using KMS key {}, but no DynamoDB table name specified.", tableName);
			dynamoDBMapper = new DynamoDBMapper(
				dynamoDbClient,
				getDynamoDbMapperConfig(),
				encryptionTransformer(kmsKey.get()));
		} else {
			API_LOG.warn("No DynamoDB table name nor encryption are specified.");
			dynamoDBMapper = new DynamoDBMapper(dynamoDbClient);
		}

		return dynamoDBMapper;
	}

	Optional<String> getOptionalProperty(String key) {
		String property = environment.getProperty(key);
		return (property != null && !property.isEmpty()) ? Optional.of(property) : Optional.empty();
	}

	DynamoDBMapperConfig tableNameOverrideConfig(String tableName) {
		return DynamoDBMapperConfig.builder().withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName))
			.build();
	}

	AttributeTransformer encryptionTransformer(String kmsKey) {
		return new AttributeEncryptor(new DirectKmsMaterialProvider(awsKms, kmsKey));
	}

	DynamoDBMapperConfig getDynamoDbMapperConfig() {
		return DynamoDBMapperConfig.builder().build();
	}
}
