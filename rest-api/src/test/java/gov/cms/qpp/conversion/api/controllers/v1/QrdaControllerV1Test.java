package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.QrdaSource;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QrdaControllerV1Test {

	@InjectMocks
	private QrdaControllerV1 objectUnderTest;

	@Mock
	private QrdaService qrdaService;

	@Mock
	private ValidationService validationService;

	private static MultipartFile multipartFile;

	private static JsonWrapper qppResult;

	@BeforeClass
	public static void initialization() {
		qppResult = new JsonWrapper();
		qppResult.putString("key", "Good Qpp");
	}

	@Before
	public void setUp() throws IOException {
		multipartFile = new MockMultipartFile("Good file", new ByteArrayInputStream("Good file".getBytes()));
	}

	@Test
	public void uploadQrdaFile() throws IOException {
		when(qrdaService.convertQrda3ToQpp(any(QrdaSource.class))).thenReturn(qppResult);

		String qppResponse = objectUnderTest.uploadQrdaFile(multipartFile);

		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(QrdaSource.class));
		assertThat("The QPP response body is incorrect.", qppResponse, is(qppResult.toString()));
	}

	@Test
	public void testFailedQppValidation() {
		String transformationErrorMessage = "Test failed QPP validation";

		Mockito.doThrow(new TransformException(transformationErrorMessage, null, null))
			.when(validationService).validateQpp(any(JsonWrapper.class));

		try {
			String qppResponse = objectUnderTest.uploadQrdaFile(multipartFile);
			fail("An exception should have occurred.");
		} catch(TransformException exception) {
			assertThat("A different exception occurred.", exception.getMessage(), is(transformationErrorMessage));
		} catch (Exception exception) {
			fail("The wrong exception occurred.");
		}
	}
}
