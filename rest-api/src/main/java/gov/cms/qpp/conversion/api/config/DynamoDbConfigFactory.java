package gov.cms.qpp.conversion.api.config;


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

	/**
	 * Creates a simple {@link DynamoDBMapper} with no customized configuration nor attribute transformer.
	 *
	 * @param dynamoDb The DynamoDB client.
	 * @return A DynamoDB mapper.
	 */
	public static DynamoDBMapper createDynamoDbMapper(final AmazonDynamoDB dynamoDb) {
		return new DynamoDBMapper(dynamoDb);
	}

	/**
	 * Creates a {@link DynamoDBMapper} with a customized configuration but no attribute transformer.
	 *
	 * @param dynamoDb The DynamoDB client.
	 * @param config The configuration to use.
	 * @return A DynamoDB mapper.
	 */
	public static DynamoDBMapper createDynamoDbMapper(final AmazonDynamoDB dynamoDb, final DynamoDBMapperConfig config) {
		return new DynamoDBMapper(dynamoDb, config);
	}

	/**
	 * Creates a {@link DynamoDBMapper} with a customized configuration and customized attribute transformer.
	 *
	 * @param dynamoDb The DynamoDB client.
	 * @param config The configuration to use.
	 * @param transformer The attribute transformer to use.
	 * @return A DynamoDB mapper.
	 */
	public static DynamoDBMapper createDynamoDbMapper(
			final AmazonDynamoDB dynamoDb,
			final DynamoDBMapperConfig config,
			final AttributeTransformer transformer) {
		return new DynamoDBMapper(dynamoDb, config, transformer);
	}
}
