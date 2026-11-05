package gov.cms.qpp.conversion.api.config;


import gov.cms.qpp.conversion.api.model.Metadata;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

/**
 * Creates a DynamoDB Enhanced Client table for metadata.
 */
public class DynamoDbConfigFactory {

	/**
	 * The constructor is private due to this being a static utility class.
	 */
	private DynamoDbConfigFactory(){}

	/**
	 * Creates a table interface with a supplied schema.
	 *
	 * @param enhancedClient The Enhanced Client.
	 * @param tableName The DynamoDB table name.
	 * @param schema The metadata table schema.
	 * @return A DynamoDB table.
	 */
	public static DynamoDbTable<Metadata> createDynamoDbTable(
			DynamoDbEnhancedClient enhancedClient, String tableName, TableSchema<Metadata> schema) {
		return enhancedClient.table(tableName, schema);
	}
}
