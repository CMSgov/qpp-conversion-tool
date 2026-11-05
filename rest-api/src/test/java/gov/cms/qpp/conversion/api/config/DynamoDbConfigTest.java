package gov.cms.qpp.conversion.api.config;

import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.test.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.kms.KmsClient;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DynamoDbConfigTest {

	@Mock
	private Environment environment;

	@Mock
	private KmsClient kmsClient;

	private DynamoDbConfig underTest;

	@BeforeEach
	void setUp() {
		underTest = new DynamoDbConfig(environment, kmsClient);
	}

	@Test
	void createsMetadataTableWithLegacyEncryptionEnabled() {
		when(environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE)).thenReturn("test-kms-key");
		when(environment.getProperty(Constants.DYNAMO_TABLE_NAME_ENV_VARIABLE)).thenReturn("metadata-table");

		DynamoDbTable<Metadata> table = underTest.metadataTable();

		assertThat(table).isNotNull();
		assertThat(table.tableName()).isEqualTo("metadata-table");
	}

	@Test
	void noAuditSkipsTableCreation() {
		when(environment.getProperty(Constants.NO_AUDIT_ENV_VARIABLE)).thenReturn("true");

		assertThat(underTest.metadataTable()).isNull();
	}

	@Test
	void missingKmsKeyFailsStartup() {
		when(environment.getProperty(Constants.KMS_KEY_ENV_VARIABLE)).thenReturn("");

		assertThrows(BeanInitializationException.class, underTest::metadataTable);
	}
}