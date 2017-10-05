package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AmazonS3ClientBuilder.class)
public class S3ConfigTest {

	@Spy
	private S3Config underTest = new S3Config();

	@Test
	public void testDefaultClient() {
		mockStatic(AmazonS3ClientBuilder.class);
		when(AmazonS3ClientBuilder.defaultClient()).thenReturn(Mockito.mock(AmazonS3.class));
		Assert.assertNotNull(underTest.s3client());
		verify(underTest, times(0)).planB();
	}

	@Test
	public void testRegionClient() {
		mockStatic(AmazonS3ClientBuilder.class);
		when(AmazonS3ClientBuilder.defaultClient()).thenThrow(new SdkClientException("meep"));
		doAnswer(invocationOnMock -> null).when(underTest).planB();

		underTest.s3client();
		verify(underTest, times(1)).planB();
	}

	@Test
	public void testTransferManagerIsNotNull() {
		Assert.assertNotNull(underTest.s3TransferManager(Mockito.mock(AmazonS3.class)));
	}
}
