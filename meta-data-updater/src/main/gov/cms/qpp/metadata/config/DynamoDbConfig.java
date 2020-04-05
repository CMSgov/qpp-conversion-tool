package main.gov.cms.qpp.metadata.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeEncryptor;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.providers.DirectKmsMaterialProvider;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;

import static main.gov.cms.qpp.metadata.config.DynamoDbConfigFactory.createDynamoDbMapper;

public class DynamoDbConfig {

	private AWSKMS awsKms;

	public DynamoDbConfig() {
		this.awsKms = buildKms();
	}

	public AmazonDynamoDB dynamoDbClient() {
		AmazonDynamoDB client;

		try {
			client = AmazonDynamoDBClientBuilder.defaultClient();
		} catch (SdkClientException exception) {
			client = planB();
		}

		return client;
	}

	AWSKMS buildKms() {
		return AWSKMSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}

	AmazonDynamoDB planB() {
		return AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
	}

	public DynamoDBMapper dynamoDbMapper(AmazonDynamoDB dynamoDbClient, String[] args) {
		DynamoDBMapper dynamoDbMapper;
		final String kmsKey = args[0];
		final String tableName = args[1];

		dynamoDbMapper = createDynamoDbMapper(
			dynamoDbClient,
			tableNameOverrideConfig(tableName),
			encryptionTransformer(kmsKey));

		return dynamoDbMapper;
	}

	DynamoDBMapperConfig tableNameOverrideConfig(String tableName) {
		return DynamoDBMapperConfig.builder()
			.withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.UPDATE)
			.withTableNameOverride(new DynamoDBMapperConfig.TableNameOverride(tableName))
			.build();
	}

	AttributeTransformer encryptionTransformer(String kmsKey) {
		return new AttributeEncryptor(new DirectKmsMaterialProvider(awsKms, kmsKey));
	}
}
