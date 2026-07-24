package gov.cms.qpp.conversion.api.config;

import org.junit.jupiter.api.Test;

@SuppressWarnings("unused") // Tests commented out pending PowerMock -> Mockito.mockStatic migration
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
