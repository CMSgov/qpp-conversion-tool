package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.api.exceptions.InvalidFileTypeException;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.CpcFileServiceImpl;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.QppValidationException;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlerControllerV1Test {

	private static Converter.ConversionReport report;
	private static AllErrors allErrors = new AllErrors();

	@InjectMocks
	private ExceptionHandlerControllerV1 objectUnderTest;

	@Mock
	private AuditService auditService;

	@BeforeAll
	static void setup() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		report = new Converter(new PathSource(path)).getReport();
		report.setReportDetails(allErrors);
	}

	@BeforeEach
	void before() {
		when(auditService.failConversion(any(Converter.ConversionReport.class)))
				.thenReturn(CompletableFuture.completedFuture(null));
	}

	@Test
	void testTransformExceptionStatusCode() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	void testTransformExceptionHeaderContentType() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test
	void testTransformExceptionBody() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(allErrors);
	}

	@Test
	void testQppValidationExceptionStatusCode() {
		QppValidationException exception =
				new QppValidationException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleQppValidationException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	void testQppValidationExceptionHeaderContentType() {
		QppValidationException exception =
				new QppValidationException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleQppValidationException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test
	void testQppValidationExceptionBody() {
		QppValidationException exception =
				new QppValidationException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleQppValidationException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(allErrors);
	}

	@Test
	void testFileNotFoundExceptionStatusCode() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testFileNotFoundExceptionHeaderContentType() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.TEXT_PLAIN);
	}

	@Test
	void testFileNotFoundExceptionBody() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(CpcFileServiceImpl.FILE_NOT_FOUND);
	}

	@Test
	void testInvalidFileTypeExceptionStatusCode() {
		InvalidFileTypeException exception =
				new InvalidFileTypeException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleInvalidFileTypeException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testInvalidFileTypeExceptionHeaderContentType() {
		InvalidFileTypeException exception =
				new InvalidFileTypeException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleInvalidFileTypeException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.TEXT_PLAIN);
	}

	@Test
	void testInvalidFileTypeExceptionBody() {
		InvalidFileTypeException exception =
				new InvalidFileTypeException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleInvalidFileTypeException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(CpcFileServiceImpl.FILE_NOT_FOUND);
	}
}