package gov.cms.qpp.conversion.api.config;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@PowerMockIgnore({"org.apache.xerces.*", "javax.xml.parsers.*", "org.xml.sax.*", "com.sun.org.apache.xerces.*" })
public class KmsConfigTest {

//	@Spy
//	private KmsConfig underTest = new KmsConfig();
//
//	@Test
//	public void testDefaultClient() {
//		mockStatic(AWSKMSClientBuilder.class);
//		when(AWSKMSClientBuilder.defaultClient()).thenReturn(Mockito.mock(AWSKMS.class));
//		assertNotNull(underTest.awsKms());
//		verify(underTest, times(0)).planB();
//	}
//
//	@Test
//	public void testRegionClient() {
//		mockStatic(AWSKMSClientBuilder.class);
//		when(AWSKMSClientBuilder.defaultClient()).thenThrow(new SdkClientException("meep"));
//		doAnswer(invocationOnMock -> null).when(underTest).planB();
//
//		underTest.awsKms();
//		verify(underTest, times(1)).planB();
//	}

}
