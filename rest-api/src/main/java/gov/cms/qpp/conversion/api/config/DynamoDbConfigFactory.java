package gov.cms.qpp.conversion.api.config;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

public class DynamoDbConfigFactory {

	public static DynamoDBMapper createDynamoDbMapper(final AmazonDynamoDB dynamoDB) {
		return new DynamoDBMapper(dynamoDB);
	}

	public static DynamoDBMapper createDynamoDbMapper(final AmazonDynamoDB dynamoDB, final DynamoDBMapperConfig config) {
		return new DynamoDBMapper(dynamoDB, config);
	}

	public static DynamoDBMapper createDynamoDbMapper(
			final AmazonDynamoDB dynamoDB,
			final DynamoDBMapperConfig config,
			final AttributeTransformer transformer) {
		return new DynamoDBMapper(dynamoDB, config, transformer);
	}
}
