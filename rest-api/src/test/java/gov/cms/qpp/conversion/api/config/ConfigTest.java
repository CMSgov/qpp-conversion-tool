package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.transfer.TransferManager;
import gov.cms.qpp.conversion.api.RestApiApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.task.TaskExecutor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;


@RunWith(PowerMockRunner.class)
@PrepareForTest({AmazonS3ClientBuilder.class})
public class ConfigTest {

	private RestApiApplication restApiApplication = new RestApiApplication();

	@InjectMocks
	@Spy
	private S3Config s3Config = new S3Config();

	@Before
	public void beforeEachTest() {
		MockitoAnnotations.initMocks(ConfigTest.class);
	}

	@Test
	public void testS3ClientNoRegion() {
		mockStatic(AmazonS3ClientBuilder.class);
		when(AmazonS3ClientBuilder.defaultClient()).thenThrow(new SdkClientException("moof"));
		doAnswer(invocationOnMock -> null).when(s3Config).planB();

		s3Config.s3client();

		verify(s3Config, times(1)).planB();
	}

	@Test
	public void testS3ClientDefault() {
		mockStatic(AmazonS3ClientBuilder.class);

		s3Config.s3client();

		verify(s3Config, times(0)).planB();
		verifyStatic(AmazonS3ClientBuilder.class);
		AmazonS3ClientBuilder.defaultClient();
	}

	@Test
	public void testTransferManager() {
		AmazonS3 amazonS3 = mock(AmazonS3.class);
		TransferManager transferManager = s3Config.s3TransferManager(amazonS3);
		assertNotNull("Transfer Manager should not be null.", transferManager);
	}

	@Test
	public void testTAskExecutor() {
		TaskExecutor taskExecutor = restApiApplication.taskExecutor();
		assertNotNull("Transfer Manager should not be null.", taskExecutor);
	}
}
