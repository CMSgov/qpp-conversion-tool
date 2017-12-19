package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrdaControllerV1Test {

	private static final String GOOD_FILE_CONTENT = "Good file";
	private static MultipartFile multipartFile;
	private static Converter.ConversionReport report;

	@InjectMocks
	private QrdaControllerV1 objectUnderTest;

	@Mock
	private QrdaService qrdaService;

	@Mock
	private ValidationService validationService;

	@Mock
	private AuditService auditService;

	@BeforeAll
	static void initialization() {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.putString("key", "Good Qpp");
		report = mock(Converter.ConversionReport.class);
		when(report.getEncoded()).thenReturn(wrapper);
	}

	@BeforeEach
	void setUp() throws IOException {
		multipartFile = new MockMultipartFile(GOOD_FILE_CONTENT,
				new ByteArrayInputStream(GOOD_FILE_CONTENT.getBytes()));
	}

	@Test
	void uploadQrdaFile() throws IOException {
		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(auditService.success(any(Converter.ConversionReport.class)))
				.then(invocation -> null);

		ResponseEntity qppResponse = objectUnderTest.uploadQrdaFile(multipartFile);

		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(Source.class));

		assertWithMessage("The QPP response body is incorrect.")
				.that(qppResponse.getBody())
				.isEqualTo(report.getEncoded().toString());
	}

	@Test
	void testFailedQppValidation() {
		String transformationErrorMessage = "Test failed QPP validation";

		when(qrdaService.convertQrda3ToQpp(any(Source.class)))
				.thenReturn(null);
		Mockito.doThrow(new TransformException(transformationErrorMessage, null, null))
			.when(validationService).validateQpp(isNull());

		try {
			ResponseEntity qppResponse = objectUnderTest.uploadQrdaFile(multipartFile);
			Assertions.fail("An exception should have occurred. Instead was " + qppResponse);
		} catch(TransformException exception) {
			assertWithMessage("A different exception occurred.")
					.that(exception.getMessage())
					.isEqualTo(transformationErrorMessage);
		} catch (Exception exception) {
			Assertions.fail("The wrong exception occurred.");
		}
	}

	@Test
	void testInputStreamSupplier() throws IOException {
		InputStream in = objectUnderTest.inputStreamSupplier(multipartFile).get();
		String content = IOUtils.toString(in, StandardCharsets.UTF_8);

		assertThat(content).isEqualTo(GOOD_FILE_CONTENT);
	}

	@Test
	void testExceptionalInputStreamSupplier() {
		Assertions.assertThrows(UncheckedIOException.class, () -> {
			MultipartFile spy = spy(multipartFile);

			try {
				doThrow(new IOException()).when(spy).getInputStream();
				InputStream in = objectUnderTest.inputStreamSupplier(spy).get();
				in.close();
			} catch (IOException ex) {
				Assertions.fail("wrong exception");
			}
		});
	}

}
