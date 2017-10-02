package gov.cms.qpp.conversion.api.config;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

public class DynamoDbConfigFactory {

	private DynamoDbConfigFactory(){}

	public static DynamoDBMapper createDynamoDbMapper(final AmazonDynamoDB dynamoDb) {
		return new DynamoDBMapper(dynamoDb);
	}

	public static DynamoDBMapper createDynamoDbMapper(final AmazonDynamoDB dynamoDb, final DynamoDBMapperConfig config) {
		return new DynamoDBMapper(dynamoDb, config);
	}

	public static DynamoDBMapper createDynamoDbMapper(
			final AmazonDynamoDB dynamoDb,
			final DynamoDBMapperConfig config,
			final AttributeTransformer transformer) {
		return new DynamoDBMapper(dynamoDb, config, transformer);
	}
}
