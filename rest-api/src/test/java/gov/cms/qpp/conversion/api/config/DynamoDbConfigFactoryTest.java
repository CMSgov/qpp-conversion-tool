package gov.cms.qpp.conversion.api.config;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.google.common.truth.Truth.assertWithMessage;

class DynamoDbConfigFactoryTest {
	@Test
	void testConstructor() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Constructor<DynamoDbConfigFactory> constructor = DynamoDbConfigFactory.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	void testFactory() {
		DynamoDBMapper dynamoDBMapper = DynamoDbConfigFactory.createDynamoDbMapper(null, null, null);
		assertWithMessage("The DynamoDB mapper must not be null.").that(dynamoDBMapper).isNotNull();
	}
}
