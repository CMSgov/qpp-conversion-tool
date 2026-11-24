package gov.cms.qpp.conversion.api.config;

import gov.cms.qpp.conversion.api.model.Metadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.lang.reflect.Constructor;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DynamoDbConfigFactoryTest {

	@Test
	void testConstructor() throws Exception {
		Constructor<DynamoDbConfigFactory> constructor = DynamoDbConfigFactory.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	@SuppressWarnings("unchecked")
	void testFactory() {
		DynamoDbEnhancedClient enhancedClient = Mockito.mock(DynamoDbEnhancedClient.class);
		DynamoDbTable<Metadata> table = Mockito.mock(DynamoDbTable.class);
		TableSchema<Metadata> schema = TableSchema.fromBean(Metadata.class);
		when(enhancedClient.table("metadata", schema)).thenReturn(table);

		assertWithMessage("The DynamoDB table must not be null.")
			.that(DynamoDbConfigFactory.createDynamoDbTable(enhancedClient, "metadata", schema)).isSameInstanceAs(table);
		verify(enhancedClient).table("metadata", schema);
	}
}
