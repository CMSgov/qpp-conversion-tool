package gov.cms.qpp.conversion.api.config;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AWSKMSClientBuilder.class)
public class KmsConfigTest {

	@Spy
	KmsConfig underTest = new KmsConfig();

	@Test
	public void testConfig() {
		mockStatic(AWSKMSClientBuilder.class);
		when(AWSKMSClientBuilder.defaultClient()).thenThrow(new SdkClientException("meep"));
		doAnswer(invocationOnMock -> null).when(underTest).planB();

		underTest.awsKms();
		verify(underTest, times(1)).planB();
	}

}
