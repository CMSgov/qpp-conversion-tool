package gov.cms.qpp.conversion.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.DynamoDbEncryptionInterceptor;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.enhancedclient.CreateDynamoDbEncryptionInterceptorInput;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.enhancedclient.DynamoDbEnhancedClientEncryption;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.enhancedclient.DynamoDbEnhancedTableEncryptionConfig;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.LegacyOverride;
import software.amazon.cryptography.dbencryptionsdk.dynamodb.model.LegacyPolicy;
import software.amazon.cryptography.dbencryptionsdk.structuredencryption.model.CryptoAction;
import software.amazon.cryptography.materialproviders.IKeyring;
import software.amazon.cryptography.materialproviders.MaterialProviders;
import software.amazon.cryptography.materialproviders.model.CreateAwsKmsMrkMultiKeyringInput;
import software.amazon.cryptography.materialproviders.model.MaterialProvidersConfig;

import com.amazonaws.services.dynamodbv2.datamodeling.sdkv2.encryption.DynamoDBEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.sdkv2.encryption.providers.DirectKmsMaterialProvider;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;

import static gov.cms.qpp.conversion.api.config.DynamoDbConfigFactory.createDynamoDbTable;

/**
 * Spring configuration file.
 *
 * Configures {@link Bean}s associated with AWS DynamoDB.
 */
@Configuration
public class DynamoDbConfig {

	private static final Logger API_LOG = LoggerFactory.getLogger(DynamoDbConfig.class);
	static final String NO_KMS_KEY = "No KMS key specified!";

	private final Environment environment;
	/**
	 * KmsClient is thread-safe and intended for sharing.
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	private final KmsClient kmsClient;

	/**
	 * Ensure required dependencies are supplied.
	 *
	 * @param environment access to environment variables
	 * @param kmsClient KMS client
	 */
	public DynamoDbConfig(Environment environment, KmsClient kmsClient) {
		this.environment = environment;
		this.kmsClient = kmsClient;
	}

	/**
	 * Creates an Enhanced Client table that interacts with {@link Metadata} records.
	 *
	 * Creates a DynamoDB object that depends on two different environment variables. {@code DYNAMO_TABLE_NAME} and
	 * {@code KMS_KEY}.
	 *
	 * If {@code DYNAMO_TABLE_NAME} is specified, the value will be used for all reads and writes. Otherwise,
	 * {@code ConversionMetadata} is used.
	 *
	 * If {@code KMS_KEY} is specified, items in the tables will be encrypted.  Else, the items will not be encrypted.
	 *
	 * @return The metadata table
	 */
	@Bean
	public DynamoDbTable<Metadata> metadataTable() {
		final Optional<String> kmsKey = getOptionalProperty(Constants.KMS_KEY_ENV_VARIABLE);
		final Optional<String> tableName = getOptionalProperty(Constants.DYNAMO_TABLE_NAME_ENV_VARIABLE);
		final Optional<String> noAudit = getOptionalProperty(Constants.NO_AUDIT_ENV_VARIABLE);

		if (noAudit.isPresent()) {
			API_LOG.info("Will not save any audit information.");
			return null;
		}

		if (kmsKey.isEmpty()) {
			API_LOG.error(NO_KMS_KEY + " This is a fatal error.");
			throw new BeanInitializationException(NO_KMS_KEY);
		}

		String tableNameOverride = tableName.orElse("ConversionMetadata");
		API_LOG.info("Using DynamoDB table name {} and KMS key {}.", tableNameOverride, kmsKey);

		TableSchema<Metadata> tableSchema = TableSchema.fromBean(Metadata.class);
		MaterialProviders materialProviders = MaterialProviders.builder()
			.MaterialProvidersConfig(MaterialProvidersConfig.builder().build())
			.build();
		IKeyring keyring = materialProviders.CreateAwsKmsMrkMultiKeyring(
			CreateAwsKmsMrkMultiKeyringInput.builder().generator(kmsKey.orElseThrow()).build());

		LegacyOverride legacyOverride = LegacyOverride.builder()
			.encryptor(DynamoDBEncryptor.getInstance(new DirectKmsMaterialProvider(kmsClient, kmsKey.orElseThrow())))
			.policy(LegacyPolicy.FORCE_LEGACY_ENCRYPT_ALLOW_LEGACY_DECRYPT)
			.attributeActionsOnEncrypt(legacyAttributeActions())
			.defaultAttributeFlag(CryptoAction.ENCRYPT_AND_SIGN)
			.build();

		Map<String, DynamoDbEnhancedTableEncryptionConfig> tableConfigs = new HashMap<>();
		tableConfigs.put(tableNameOverride, DynamoDbEnhancedTableEncryptionConfig.builder()
			.logicalTableName(tableNameOverride)
			.schemaOnEncrypt(tableSchema)
			.keyring(keyring)
			.legacyOverride(legacyOverride)
			.build());

		DynamoDbEncryptionInterceptor encryptionInterceptor = DynamoDbEnhancedClientEncryption
			.CreateDynamoDbEncryptionInterceptor(CreateDynamoDbEncryptionInterceptorInput.builder()
				.tableEncryptionConfigs(tableConfigs)
				.build());
		DynamoDbClient encryptedClient;
		try {
			encryptedClient = DynamoDbClient.builder()
				.overrideConfiguration(ClientOverrideConfiguration.builder()
					.addExecutionInterceptor(encryptionInterceptor)
					.build())
				.build();
		} catch (SdkClientException exception) {
			API_LOG.info("Default DynamoDB client failed to build, trying again with region us-east-1", exception);
			encryptedClient = DynamoDbClient.builder()
				.region(Region.US_EAST_1)
				.overrideConfiguration(ClientOverrideConfiguration.builder()
					.addExecutionInterceptor(encryptionInterceptor)
					.build())
				.build();
		}
		DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
			.dynamoDbClient(encryptedClient)
			.build();
		return createDynamoDbTable(enhancedClient, tableNameOverride, tableSchema);
	}

	private static Map<String, CryptoAction> legacyAttributeActions() {
		Map<String, CryptoAction> actions = new HashMap<>();
		for (String attribute : new String[] {
			"Uuid", "CreateDate", "Npi", "Apm", "ProgramName", "SubmissionYear", "SubmissionLocator", "QPPLocator", "Purpose",
			"OverallStatus", "ConversionStatus", "ValidationStatus", Constants.DYNAMO_CPC_ATTRIBUTE,
			Constants.DYNAMO_PCF_ATTRIBUTE, "ConversionErrorLocator", "ValidationErrorLocator",
			"RawValidationErrorLocator", Constants.DYNAMO_CPC_PROCESSED_CREATE_DATE_ATTRIBUTE,
			Constants.DYNAMO_RTI_PROCESSED_CREATE_DATE_ATTRIBUTE, "Errors", "Warnings", "MetadataVersion"
		}) {
			actions.put(attribute, CryptoAction.SIGN_ONLY);
		}
		return actions;
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

}
