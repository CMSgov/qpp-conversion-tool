package main.gov.cms.qpp.metadata.config;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

/**
 * Creates an appropriate {@link DynamoDBMapper} depending on what method is called.
 */
public class DynamoDbConfigFactory {

	/**
	 * The constructor is private due to this being a static utility class.
	 */
	private DynamoDbConfigFactory(){}

	public static DynamoDBMapper createDynamoDbMapper(
			final AmazonDynamoDB dynamoDb,
			final DynamoDBMapperConfig config,
			final AttributeTransformer transformer) {
		return new DynamoDBMapper(dynamoDb, config, transformer);
	}
}
