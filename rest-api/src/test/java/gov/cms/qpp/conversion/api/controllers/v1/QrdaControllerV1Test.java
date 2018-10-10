package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QrdaControllerV1Test {

	private static final String GOOD_FILE_CONTENT = "Good file";

	private MultipartFile multipartFile;

	@InjectMocks
	private QrdaControllerV1 objectUnderTest;

	@Mock
	private QrdaService qrdaService;

	@Mock
	private ValidationService validationService;

	@Mock
	private AuditService auditService;

	@Mock
	private ConversionReport report;

	@BeforeEach
	void initialization() throws IOException {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.putString("key", "Good Qpp");
		when(report.getEncoded()).thenReturn(wrapper);

		multipartFile = new MockMultipartFile(GOOD_FILE_CONTENT,
				new ByteArrayInputStream(GOOD_FILE_CONTENT.getBytes()));
	}

	@Test
	void uploadQrdaFile() {
		Metadata metadata = new Metadata();
		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> CompletableFuture.completedFuture(metadata));

		ResponseEntity<String> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, null);

		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(Source.class));

		assertThat(qppResponse.getBody())
				.isEqualTo(report.getEncoded().toString());
	}

	@Test
	void uploadTestQrdaFile() {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> null);

		when(report.getPurpose()).thenReturn("Test");
		ResponseEntity<String> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, "Test");

		assertThat(peopleCaptor.getValue().getPurpose()).isEqualTo("Test");
	}

	@Test
	void testHeadersContainsLocation() {
		Metadata metadata = new Metadata();
		metadata.setUuid(UUID.randomUUID().toString());
		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> CompletableFuture.completedFuture(metadata));

		ResponseEntity<String> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, null);
		assertThat(qppResponse.getHeaders().get("Location")).containsExactly(metadata.getUuid());
	}

	@Test
	void testFailedQppValidation() {
		String transformationErrorMessage = "Test failed QPP validation";

		when(qrdaService.convertQrda3ToQpp(any(Source.class)))
				.thenReturn(null);
		Mockito.doThrow(new TransformException(transformationErrorMessage, null, null))
			.when(validationService).validateQpp(isNull());

		try {
			ResponseEntity<String> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, null);
			Assertions.fail("An exception should have occurred. Instead was " + qppResponse);
		} catch(TransformException exception) {
			assertThat(exception.getMessage())
					.isEqualTo(transformationErrorMessage);
		} catch (Exception exception) {
			Assertions.fail("The wrong exception occurred.");
		}
	}

	@Test
	void testInputStreamSupplier() throws IOException {
		InputStream in = objectUnderTest.inputStream(multipartFile);
		String content = IOUtils.toString(in, StandardCharsets.UTF_8);

		assertThat(content).isEqualTo(GOOD_FILE_CONTENT);
	}

	@Test
	void testExceptionalInputStreamSupplier() {
		Assertions.assertThrows(UncheckedIOException.class, () -> {
			MultipartFile spy = spy(multipartFile);

			try {
				doThrow(new IOException()).when(spy).getInputStream();
				InputStream in = objectUnderTest.inputStream(spy);
				in.close();
			} catch (IOException ex) {
				Assertions.fail("wrong exception");
			}
		});
	}

}
