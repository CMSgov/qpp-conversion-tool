package gov.cms.qpp.conversion.api.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.kms.KmsClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class KmsConfigTest {
	@Test
	void createsKmsClient() {
		try (KmsClient client = new KmsConfig().awsKms()) {
			assertNotNull(client);
		}
	}
}
