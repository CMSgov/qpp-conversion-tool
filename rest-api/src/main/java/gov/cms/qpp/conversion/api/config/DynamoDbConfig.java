package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.kms.AWSKMS;
import gov.cms.qpp.conversion.api.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Optional;

import static gov.cms.qpp.conversion.api.config.DynamoDbConfigFactory.createDynamoDbMapper;

/**
 * Spring configuration file.
 *
 * Configures {@link Bean}s associated with AWS DynamoDB.
 */
@Configuration
public class DynamoDbConfig {

	private static final Logger API_LOG = LoggerFactory.getLogger(Constants.API_LOG);
	static final String DYNAMO_TABLE_NAME_ENV_VARIABLE = "DYNAMO_TABLE_NAME";
	static final String KMS_KEY_ENV_VARIABLE = "KMS_KEY";
	static final String NO_AUDIT_ENV_VARIABLE = "NO_AUDIT";
	static final String NO_KMS_KEY = "No KMS key specified!";

	@Autowired
	private Environment environment;

	@Autowired
	private AWSKMS awsKms;

	/**
	 * Creates the DynamoDB client {@link Bean}.
	 *
	 * Uses the default client, but if a region is unspecified, uses {@code us-east-1}.
	 *
	 * @return The DynamoDB client.
	 */
	@Bean
	public AmazonDynamoDB dynamoDbClient() {
		AmazonDynamoDB client;

		try {
			client = AmazonDynamoDBClientBuilder.defaultClient();
		} catch (SdkClientException exception) {
			API_LOG.info("Default DynamoDB client failed to build, trying again with region us-east-1", exception);
			client = planB();
		}

		return client;
	}

	/**
	 * Returns the default client that uses {@code us-east-1}.
	 *
	 * @return The DynamoDB client.
	 */
	AmazonDynamoDB planB() {
		return AmazonDynamoDBClientBuilder.standard().withRegion("us-east-1").build();
	}

	/**
	 * Creates a DynamoDB object mapper {@link Bean} that interacts with {@link DynamoDBTable} annotated POJOs.
	 * Based on an available {@link AmazonDynamoDB} and {@link AWSKMS} {@link Bean}.
	 *
	 * Creates a DynamoDB object that depends on two different environment variables. {@code DYNAMO_TABLE_NAME} and
	 * {@code KMS_KEY}.
	 *
	 * If {@code DYNAMO_TABLE_NAME} is specified, the value will be used for all read/write from/to the table of that name
	 * regardless of what is specified for {@link DynamoDBTable}.  Else, the table specified for {@link DynamoDBTable} will be
	 * used like normal.
	 *
	 * If {@code KMS_KEY} is specified, items in the tables will be encrypted.  Else, the items will not be encrypted.
	 *
	 * @param dynamoDbClient The {@link AmazonDynamoDB} {@link Bean}.
	 * @return The DynamoDB object mapper
	 */
	@Bean
	public DynamoDBMapper dynamoDbMapper(AmazonDynamoDB dynamoDbClient) {
		DynamoDBMapper dynamoDbMapper;

		final Optional<String> kmsKey = getOptionalProperty(KMS_KEY_ENV_VARIABLE);
		final Optional<String> tableName = getOptionalProperty(DYNAMO_TABLE_NAME_ENV_VARIABLE);
		final Optional<String> noAudit = getOptionalProperty(NO_AUDIT_ENV_VARIABLE);

		if (!noAudit.isPresent()) {
			if (tableName.isPresent() && kmsKey.isPresent()) {
				API_LOG.info("Using DynamoDB table name {} and KMS key {}.", tableName, kmsKey);
				dynamoDbMapper = createDynamoDbMapper(
					dynamoDbClient,
					tableNameOverrideConfig(tableName.get()),
					encryptionTransformer(kmsKey.get()));
			} else if (kmsKey.isPresent()) {
				API_LOG.warn("Using KMS key {}, but no DynamoDB table name specified.", tableName);
				dynamoDbMapper = createDynamoDbMapper(
					dynamoDbClient,
					getDynamoDbMapperConfig(),
					encryptionTransformer(kmsKey.get()));
			} else {
				API_LOG.error(NO_KMS_KEY + " This is a fatal error.");
				throw new BeanInitializationException(NO_KMS_KEY);
			}
		} else {
			API_LOG.info("Will not save any audit information.");
			dynamoDbMapper = null;
		}

		return dynamoDbMapper;
	}

	/**
	 * Optional wrapper for environment properties
	 *
	 * @param key environment variable name
	 * @return environment variable value
	 */
	private Optional<String> getOptionalProperty(String key) {
		String property = environment.getProperty(key);
		return (property != null && !property.isEmpty()) ? Optional.of(property) : Optional.empty();
	}

	/**
	 * Creates a DynamoDB configuration that forces the table name to the parameter.
	 *
	 * @param tableName The name of the table.
	 * @return A DynamoDB configuration that forces the table name.
	 */
	DynamoDBMapperConfig tableNameOverrideConfig(String tableName) {
		return DynamoDBMapperConfig.builder().withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName))
			.build();
	}

	/**
	 * Creates a DynamoDB attribute transformer that encrypts items based on the KMS key parameter
	 *
	 * @param kmsKey The KMS key ARN to use to encrypt.
	 * @return An encryption attribute transformer.
	 */
	AttributeTransformer encryptionTransformer(String kmsKey) {
		return new AttributeEncryptor(new DirectKmsMaterialProvider(awsKms, kmsKey));
	}

	/**
	 * Nameless db mapper config builder
	 *
	 * @return dynamo db mapper
	 */
	DynamoDBMapperConfig getDynamoDbMapperConfig() {
		return DynamoDBMapperConfig.builder().build();
	}
}
