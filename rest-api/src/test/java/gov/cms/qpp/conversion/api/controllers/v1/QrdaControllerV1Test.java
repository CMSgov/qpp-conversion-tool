package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.QrdaSource;
import gov.cms.qpp.conversion.api.model.TransformResult;
import gov.cms.qpp.conversion.api.services.AuditService;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
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

	@Mock
	private AuditService auditService;

	private static MultipartFile multipartFile;

	private static TransformResult result;

	@BeforeClass
	public static void initialization() {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.putString("key", "Good Qpp");
		result = new TransformResult(null, wrapper);
	}

	@Before
	public void setUp() throws IOException {
		multipartFile = new MockMultipartFile("Good file", new ByteArrayInputStream("Good file".getBytes()));
	}

	@Test
	public void uploadQrdaFile() throws IOException {
		when(qrdaService.convertQrda3ToQpp(any(QrdaSource.class))).thenReturn(result);
		when(auditService.success(any(InputStream.class), any(TransformResult.class)))
				.then(invocation -> null);

		ResponseEntity qppResponse = objectUnderTest.uploadQrdaFile(multipartFile);

		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(QrdaSource.class));

		assertWithMessage("The QPP response body is incorrect.")
				.that(qppResponse.getBody())
				.isEqualTo(result.getEncoded().toString());
	}

	@Test
	public void testFailedQppValidation() {
		String transformationErrorMessage = "Test failed QPP validation";

		when(qrdaService.convertQrda3ToQpp(any(QrdaSource.class)))
				.thenReturn(new TransformResult(null, null));
		Mockito.doThrow(new TransformException(transformationErrorMessage, null, null))
			.when(validationService).validateQpp(isNull());

		try {
			ResponseEntity qppResponse = objectUnderTest.uploadQrdaFile(multipartFile);
			fail("An exception should have occurred. Instead was " + qppResponse);
		} catch(TransformException exception) {
			assertWithMessage("A different exception occurred.")
					.that(exception.getMessage())
					.isEqualTo(transformationErrorMessage);
		} catch (Exception exception) {
			fail("The wrong exception occurred.");
		}
	}
}
