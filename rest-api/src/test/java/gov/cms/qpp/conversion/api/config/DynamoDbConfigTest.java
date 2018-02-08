package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.kms.AWSKMS;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.env.Environment;

import gov.cms.qpp.conversion.api.model.Constants;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AmazonDynamoDBClientBuilder.class, DynamoDbConfigFactory.class, DynamoDBMapper.class})
public class DynamoDbConfigTest {

	private DynamoDbConfig underTest;

	@Mock
	private Environment environment;

	@Mock
	private AWSKMS awsKms;

	@Mock
	private DynamoDBMapper dbMapper;

	@Mock
	private AmazonDynamoDB amazonDynamoDB;

	@Mock
	private DynamoDBMapperConfig mapConfig;

	@Mock
	private DynamoDBMapperConfig mapConfigNamed;

	@Mock
	private AttributeTransformer transformer;

	@Rule
	private ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup() {

		underTest = spy(new DynamoDbConfig(environment, awsKms));

		when(underTest.encryptionTransformer(any(String.class))).thenReturn(transformer);
		when(underTest.tableNameOverrideConfig(any(String.class))).thenReturn(mapConfigNamed);
		when(underTest.getDynamoDbMapperConfig()).thenReturn(mapConfig);

		mockStatic(DynamoDBMapper.class);
		mockStatic(DynamoDbConfigFactory.class);

		MockitoAnnotations.initMocks(DynamoDbConfigTest.class);
	}

	@Test
	public void testDefaultClient() {
		mockStatic(AmazonDynamoDBClientBuilder.class);
		when(AmazonDynamoDBClientBuilder.defaultClient()).thenReturn(Mockito.mock(AmazonDynamoDB.class));
		assertNotNull(underTest.dynamoDbClient());
		verify(underTest, times(0)).planB();
	}

	@Test
	public void testRegionClient() {
		mockStatic(AmazonDynamoDBClientBuilder.class);
		when(AmazonDynamoDBClientBuilder.defaultClient()).thenThrow(new SdkClientException("meep"));
		doAnswer(invocationOnMock -> null).when(underTest).planB();

		underTest.dynamoDbClient();
		verify(underTest, times(1)).planB();
	}

	@Test
	public void dbMapperNoAudit() {
		when(environment.getProperty(eq(Constants.NO_AUDIT_ENV_VARIABLE))).thenReturn("true");
		when(environment.getProperty(eq(Constants.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn(null);

		DynamoDBMapper dynamoDBMapper = underTest.dynamoDbMapper(amazonDynamoDB);

		assertThat(dynamoDBMapper).isNull();
	}

	@Test
	public void dbMapperInitWithNothing() {
		when(environment.getProperty(eq(Constants.NO_AUDIT_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(Constants.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn("");

		thrown.expect(BeanInitializationException.class);
		thrown.expectMessage(DynamoDbConfig.NO_KMS_KEY);

		underTest.dynamoDbMapper(amazonDynamoDB);
	}

	@Test
	public void dbMapperInitWithTableName() {
		when(environment.getProperty(eq(Constants.NO_AUDIT_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(Constants.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn("meep");
		when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn(null);

		thrown.expect(BeanInitializationException.class);
		thrown.expectMessage(DynamoDbConfig.NO_KMS_KEY);

		underTest.dynamoDbMapper(amazonDynamoDB);
	}

	@Test
	public void dbMapperInitWithKmsKey() throws Exception {
		when(environment.getProperty(eq(Constants.NO_AUDIT_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(Constants.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn("meep");

		mockStatic(DynamoDbConfigFactory.class);
		doReturn(dbMapper).when(DynamoDbConfigFactory.class, "createDynamoDbMapper", amazonDynamoDB, mapConfig, transformer);

		underTest.dynamoDbMapper(amazonDynamoDB);

		verifyStatic(DynamoDbConfigFactory.class, times(1));
		DynamoDbConfigFactory.createDynamoDbMapper(amazonDynamoDB, mapConfig, transformer);
	}

	@Test
	public void dbMapperInitWithKmsKeyAndTableName() throws Exception {
		when(environment.getProperty(eq(Constants.NO_AUDIT_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(Constants.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn("meep");
		when(environment.getProperty(eq(Constants.KMS_KEY_ENV_VARIABLE))).thenReturn("meep");

		mockStatic(DynamoDbConfigFactory.class);
		doReturn(dbMapper).when(DynamoDbConfigFactory.class, "createDynamoDbMapper", amazonDynamoDB, mapConfigNamed, transformer);

		underTest.dynamoDbMapper(amazonDynamoDB);

		verifyStatic(DynamoDbConfigFactory.class, times(1));
		DynamoDbConfigFactory.createDynamoDbMapper(amazonDynamoDB, mapConfigNamed, transformer);
	}
}