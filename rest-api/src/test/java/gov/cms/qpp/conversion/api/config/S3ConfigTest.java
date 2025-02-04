package gov.cms.qpp.conversion.api.config;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mockStatic;

@PowerMockIgnore({"org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*", "com.sun.org.apache.xerces.*" })
public class S3ConfigTest {

//	@Spy
//	private S3Config underTest = new S3Config();
//
//	@Test
//	public void testDefaultClient() {
//        mockStatic(AmazonS3ClientBuilder.class);
//		when(AmazonS3ClientBuilder.defaultClient()).thenReturn(Mockito.mock(AmazonS3.class));
//		assertNotNull(underTest.s3client());
//		verify(underTest, times(0)).planB();
//	}
//
//	@Test
//	public void testRegionClient() {
//        mockStatic(AmazonS3ClientBuilder.class);
//		when(AmazonS3ClientBuilder.defaultClient()).thenThrow(new SdkClientException("meep"));
//		doAnswer(invocationOnMock -> null).when(underTest).planB();
//
//		underTest.s3client();
//		verify(underTest, times(1)).planB();
//	}
//
//	@Test
//	public void testTransferManagerIsNotNull() {
//		assertWithMessage("Transfer manager should not be null.")
//				.that(underTest.s3TransferManager(Mockito.mock(AmazonS3.class))).isNotNull();
//	}
}
