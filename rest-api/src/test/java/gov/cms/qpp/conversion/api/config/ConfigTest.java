package gov.cms.qpp.conversion.api.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import gov.cms.qpp.conversion.api.RestApiApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;


@SpringBootTest(classes = { RestApiApplication.class, S3Config.class })
@RunWith(SpringRunner.class)
public class ConfigTest {
	@Autowired
	private AmazonS3 s3client;

	@Autowired
	public TransferManager s3TransferManager;

	@Autowired
	private TaskExecutor executor;

	@Test
	public void initializedConfig() {
		assertNotNull("S3Client should be injectable", s3client);
		assertNotNull("S3TransferManager should be injectable",s3TransferManager);
		assertNotNull("Executor should be injectable",executor);
	}
}
