package gov.cms.qpp.conversion.api.controllers.v1;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Source;
import gov.cms.qpp.conversion.api.exceptions.AuditException;
import gov.cms.qpp.conversion.api.exceptions.InvalidPurposeException;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QrdaControllerV1Test {

	private static final String GOOD_FILE_CONTENT = "Good file";

	static final Path validationJsonFilePath = Paths.get("src/test/resources/testCpcPlusValidationFile.json");

	private MultipartFile multipartFile;

	private InputStream validationInputStream;

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

	@Mock
	private CompletableFuture<Metadata> mockMetadata;// = Mockito.mock(CompletableFuture<Metadata>.class);

	
	@BeforeEach
	void initialization() throws IOException {
		JsonWrapper wrapper = new JsonWrapper();
		wrapper.put("key", "Good Qpp");

		validationInputStream = Files.newInputStream(validationJsonFilePath);

		when(report.getEncodedWithMetadata()).thenReturn(wrapper);

		multipartFile = new MockMultipartFile(GOOD_FILE_CONTENT,
				new ByteArrayInputStream(GOOD_FILE_CONTENT.getBytes()));
	}

	@Test
	void uploadQrdaFile() {
		Metadata metadata = Metadata.create();
		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationInputStream);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> CompletableFuture.completedFuture(metadata));

		ResponseEntity<String> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, null);

		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(Source.class));

		assertThat(qppResponse.getBody())
				.isEqualTo(report.getEncodedWithMetadata().toString());
	}

	@Test
	void uploadTestQrdaFile() {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationInputStream);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> null);

		when(report.getPurpose()).thenReturn("Test");
		ResponseEntity<String> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, "Test");

		assertThat(qppResponse).isNotNull();
		assertThat(peopleCaptor.getValue().getPurpose()).isEqualTo("Test");
	}

	@Test
	void uploadQrdaFile_auditInterruptionException() throws Exception {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationInputStream);
		when(auditService.success(any(ConversionReport.class))).thenReturn(mockMetadata);
		when(mockMetadata.get()).thenThrow(new InterruptedException("Testing Audit Exception Handling"));
		
		String purpose = "Test";
		when(report.getPurpose()).thenReturn(purpose);
		assertThrows(AuditException.class, () -> objectUnderTest.uploadQrdaFile(multipartFile, purpose));
	}

	@Test
	void uploadQrdaFile_auditExecutionException() throws Exception {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationInputStream);
		when(auditService.success(any(ConversionReport.class))).thenReturn(mockMetadata);
		when(mockMetadata.get()).thenThrow(new ExecutionException(new RuntimeException("Testing Audit Exception Handling")));
		
		String purpose = "Test";
		when(report.getPurpose()).thenReturn(purpose);
		assertThrows(AuditException.class, () -> objectUnderTest.uploadQrdaFile(multipartFile, purpose));
	}

	@Test
	void uploadQrdaFile_badPurpose() {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationInputStream);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> null);

		String purpose = "This prupose is bad because it is too long, exceeding 25 chars.";
		when(report.getPurpose()).thenReturn(purpose);
		
		assertThrows(InvalidPurposeException.class, () -> objectUnderTest.uploadQrdaFile(multipartFile, purpose));
	}
	
	@Test
	void uploadQrdaFile_nullCpcValidationMap() {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(null);
		when(auditService.success(any(ConversionReport.class))).then(invocation -> null);

		String purpose = "Test";
		when(report.getPurpose()).thenReturn(purpose);
		ResponseEntity<String> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, purpose);
		
		assertThat(qppResponse).isNotNull();
	}
	
	
//	apmToNpiValidationMap.getNpiToApmMap()
	
	@Test
	void testHeadersContainsLocation() {
		Metadata metadata = Metadata.create();
		metadata.setUuid(UUID.randomUUID().toString());
		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationInputStream);
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
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationInputStream);
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
