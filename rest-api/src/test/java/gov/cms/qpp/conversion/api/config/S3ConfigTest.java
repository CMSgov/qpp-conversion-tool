package gov.cms.qpp.conversion.api.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

class S3ConfigTest {
	@Test
	void createsS3Client() {
		try (S3Client client = new S3Config().s3client()) {
			assertNotNull(client);
		}
	}
}
