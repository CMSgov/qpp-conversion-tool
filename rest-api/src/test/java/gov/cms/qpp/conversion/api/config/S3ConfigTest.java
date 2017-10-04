package gov.cms.qpp.conversion.api.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;

@SpringBootTest
@RunWith(SpringRunner.class)
public class S3ConfigTest {
	@Autowired
	private AmazonS3 s3client;

	@Autowired
	private TransferManager s3TransferManager;

	@Test
	public void testS3ClientIsInjectable() {
		assertNotNull("S3Client should be injectable", s3client);
	}

	@Test
	public void testTransferManagerIsInjectable() {
		assertNotNull("S3TransferManager should be injectable", s3TransferManager);
	}
}
