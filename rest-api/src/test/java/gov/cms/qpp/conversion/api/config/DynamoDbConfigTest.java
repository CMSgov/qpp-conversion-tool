package gov.cms.qpp.conversion.api.config;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.AttributeTransformer;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;


@RunWith(PowerMockRunner.class)
@PrepareForTest({AmazonDynamoDBClientBuilder.class, DynamoDBMapper.class, DynamoDbConfig.class})
public class DynamoDbConfigTest {

	@Spy
	@InjectMocks
	private DynamoDbConfig underTest = new DynamoDbConfig();

	@Mock
	private DynamoDBMapper dbMapper;

	@Mock
	private Environment environment;

	@Mock
	private AmazonDynamoDB meep;

	@Mock
	private DynamoDBMapperConfig mapConfig;

	@Mock
	private DynamoDBMapperConfig mapConfigNamed;

	@Mock
	private AttributeTransformer transformer;

	@Before
	public void setup() throws NoSuchFieldException, IllegalAccessException {
		when(underTest.encryptionTransformer(any(String.class))).thenReturn(transformer);
		when(underTest.tableNameOverrideConfig(any(String.class))).thenReturn(mapConfigNamed);
		when(underTest.getDynamoDbMapperConfig()).thenReturn(mapConfig);

		mockStatic(DynamoDBMapper.class);

		MockitoAnnotations.initMocks(DynamoDbConfigTest.class);
	}

	@Test
	public void testConfig() {
		mockStatic(AmazonDynamoDBClientBuilder.class);
		when(AmazonDynamoDBClientBuilder.defaultClient()).thenThrow(new SdkClientException("meep"));
		doAnswer(invocationOnMock -> null).when(underTest).planB();

		underTest.dynamoDbClient();
		verify(underTest, times(1)).planB();
	}

	@Test
	public void dbMapperInit() throws Exception {
		when(environment.getProperty(eq(DynamoDbConfig.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(DynamoDbConfig.KMS_KEY_ENV_VARIABLE))).thenReturn(null);
		whenNew(DynamoDBMapper.class).withArguments(meep).thenReturn(dbMapper);

		underTest.dynamoDBMapper(meep);

		verifyNew(DynamoDBMapper.class).withArguments(meep);
	}

	@Test
	public void dbMapperInitWithTableName() throws Exception {
		when(environment.getProperty(eq(DynamoDbConfig.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn("meep");
		when(environment.getProperty(eq(DynamoDbConfig.KMS_KEY_ENV_VARIABLE))).thenReturn(null);
		whenNew(DynamoDBMapper.class).withArguments(meep, mapConfigNamed).thenReturn(dbMapper);

		underTest.dynamoDBMapper(meep);

		verifyNew(DynamoDBMapper.class).withArguments(meep, mapConfigNamed);
	}

	@Test
	public void dbMapperInitWithKmsKey() throws Exception {
		when(environment.getProperty(eq(DynamoDbConfig.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn(null);
		when(environment.getProperty(eq(DynamoDbConfig.KMS_KEY_ENV_VARIABLE))).thenReturn("meep");
		whenNew(DynamoDBMapper.class).withArguments(meep, mapConfig, transformer).thenReturn(dbMapper);

		underTest.dynamoDBMapper(meep);

		verifyNew(DynamoDBMapper.class).withArguments(meep, mapConfig, transformer);
	}

	@Test
	public void dbMapperInitWithKmsKeyAndTableName() throws Exception {
		when(environment.getProperty(eq(DynamoDbConfig.DYNAMO_TABLE_NAME_ENV_VARIABLE))).thenReturn("meep");
		when(environment.getProperty(eq(DynamoDbConfig.KMS_KEY_ENV_VARIABLE))).thenReturn("meep");
		whenNew(DynamoDBMapper.class).withArguments(meep, mapConfigNamed, transformer).thenReturn(dbMapper);

		underTest.dynamoDBMapper(meep);

		verifyNew(DynamoDBMapper.class).withArguments(meep, mapConfigNamed, transformer);
	}

}