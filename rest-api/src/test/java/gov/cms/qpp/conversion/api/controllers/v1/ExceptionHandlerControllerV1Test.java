package gov.cms.qpp.conversion.api.controllers.v1;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.api.services.CpcFileServiceImpl;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.QppValidationException;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerControllerV1Test {
	private static Converter.ConversionReport report;
	private static AllErrors allErrors = new AllErrors();

	@InjectMocks
	private ExceptionHandlerControllerV1 objectUnderTest;

	@Mock
	private AuditService auditService;

	@BeforeClass
	public static void setup() {
		Path path = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		report = new Converter(new PathSource(path)).getReport();
		report.setReportDetails(allErrors);
	}

	@Before
	public void before() {
		when(auditService.failConversion(any(Converter.ConversionReport.class)))
				.thenReturn(CompletableFuture.completedFuture(null));
	}

	@Test
	public void testTransformExceptionStatusCode() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	public void testTransformExceptionHeaderContentType() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test
	public void testTransformExceptionBody() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(allErrors);
	}

	@Test
	public void testQppValidationExceptionStatusCode() {
		QppValidationException exception =
				new QppValidationException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleQppValidationException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	public void testQppValidationExceptionHeaderContentType() {
		QppValidationException exception =
				new QppValidationException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleQppValidationException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.APPLICATION_JSON_UTF8);
	}

	@Test
	public void testQppValidationExceptionBody() {
		QppValidationException exception =
				new QppValidationException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleQppValidationException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(allErrors);
	}

	@Test
	public void testFileNotFoundExceptionStatusCode() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEquivalentAccordingToCompareTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void testFileNotFoundExceptionHeaderContentType() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.TEXT_PLAIN);
	}

	@Test
	public void testFileNotFoundExceptionBody() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(CpcFileServiceImpl.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(CpcFileServiceImpl.FILE_NOT_FOUND);
	}
}