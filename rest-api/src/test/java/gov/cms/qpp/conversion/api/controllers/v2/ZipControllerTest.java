package gov.cms.qpp.conversion.api.controllers.v2;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import gov.cms.qpp.conversion.api.model.ConvertResponse;
import gov.cms.qpp.conversion.api.model.Metadata;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.QrdaService;
import gov.cms.qpp.conversion.api.services.ValidationService;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ZipControllerTest {

	private static final String GOOD_FILE_CONTENT = "good-file";

	static final Path validationJsonFilePath = Paths.get("src/test/resources/testCpcPlusValidationFile.json");
	static final Path goodZipFilePath = Paths.get("src/test/resources/good-file.zip");

	private MultipartFile multipartFile;

	private byte[] validationBytes;

	@InjectMocks
	private ZipController objectUnderTest;

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

		validationBytes = Files.readAllBytes(validationJsonFilePath);

		when(report.getEncodedWithMetadata()).thenReturn(wrapper);

		multipartFile = new MockMultipartFile(GOOD_FILE_CONTENT, Files.newInputStream(goodZipFilePath));
	}

	@Test
	void uploadQrdaFile() {
		Metadata metadata = Metadata.create();
		when(qrdaService.convertQrda3ToQpp(any(Source.class))).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationBytes);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> CompletableFuture.completedFuture(metadata));

		ResponseEntity<List<ConvertResponse>> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, null);

		verify(qrdaService, atLeastOnce()).convertQrda3ToQpp(any(Source.class));

		assertThat(qppResponse.getBody().get(0).getQpp().toString())
				.isEqualTo(report.getEncodedWithMetadata().toObject().toString());
	}

	@Test
	void uploadTestQrdaFile() {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationBytes);
		when(auditService.success(any(ConversionReport.class)))
				.then(invocation -> null);

		when(report.getPurpose()).thenReturn("Test");
		ResponseEntity<List<ConvertResponse>> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, "Test");

		assertThat(qppResponse).isNotNull();
		assertThat(peopleCaptor.getValue().getPurpose()).isEqualTo("Test");
	}

	@Test
	void uploadNullQrdaFile() {
		Assertions.assertThrows(UncheckedIOException.class, () -> {
			objectUnderTest.uploadQrdaFile(new MockMultipartFile("null.zip", new byte[0]), "Test");
		});
	}

	@Test
	void uploadQrdaFile_auditInterruptionException() throws Exception {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationBytes);
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
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationBytes);
		when(auditService.success(any(ConversionReport.class))).thenReturn(mockMetadata);
		when(mockMetadata.get()).thenThrow(new ExecutionException(new RuntimeException("Testing Audit Exception Handling")));
		
		String purpose = "Test";
		when(report.getPurpose()).thenReturn(purpose);
		assertThrows(AuditException.class, () -> objectUnderTest.uploadQrdaFile(multipartFile, purpose));
	}

	@Test
	void uploadQrdaFile_nullCpcValidationMap() {
		ArgumentCaptor<Source> peopleCaptor = ArgumentCaptor.forClass(Source.class);

		when(qrdaService.convertQrda3ToQpp(peopleCaptor.capture())).thenReturn(report);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(null);
		when(auditService.success(any(ConversionReport.class))).then(invocation -> null);

		String purpose = "Test";
		when(report.getPurpose()).thenReturn(purpose);
		ResponseEntity<List<ConvertResponse>> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, purpose);
		
		assertThat(qppResponse).isNotNull();
	}

	@Test
	void testFailedQppValidation() {
		String transformationErrorMessage = "Test failed QPP validation";

		when(qrdaService.convertQrda3ToQpp(any(Source.class)))
				.thenReturn(null);
		when(qrdaService.retrieveCpcPlusValidationFile()).thenReturn(validationBytes);
		Mockito.doThrow(new TransformException(transformationErrorMessage, null, null))
			.when(validationService).validateQpp(isNull());

		try {
			ResponseEntity<List<ConvertResponse>> qppResponse = objectUnderTest.uploadQrdaFile(multipartFile, null);
			Assertions.fail("An exception should have occurred. Instead was " + qppResponse);
		} catch(TransformException exception) {
			assertThat(exception.getMessage())
					.isEqualTo(transformationErrorMessage);
		} catch (Exception exception) {
			Assertions.fail("The wrong exception occurred.");
		}
	}

}
