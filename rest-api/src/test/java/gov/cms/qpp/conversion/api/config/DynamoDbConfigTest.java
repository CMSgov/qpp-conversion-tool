package gov.cms.qpp.conversion.api.config;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AmazonDynamoDBClientBuilder.class)
public class DynamoDbConfigTest {

	@Spy
	DynamoDbConfig underTest = new DynamoDbConfig();

	@Test
	public void testConfig() {
		mockStatic(AmazonDynamoDBClientBuilder.class);
		when(AmazonDynamoDBClientBuilder.defaultClient()).thenThrow(new SdkClientException("meep"));
		doAnswer(invocationOnMock -> null).when(underTest).planB();

		underTest.dynamoDbClient();
		verify(underTest, times(1)).planB();
	}

}