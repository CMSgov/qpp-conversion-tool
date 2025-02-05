package gov.cms.qpp.conversion.api.controllers.v1;

import com.amazonaws.AmazonServiceException;
import com.google.common.truth.Truth;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.api.exceptions.InvalidFileTypeException;
import gov.cms.qpp.conversion.api.exceptions.InvalidPurposeException;
import gov.cms.qpp.conversion.api.exceptions.NoFileInDatabaseException;
import gov.cms.qpp.conversion.api.helper.AdvancedApmHelper;
import gov.cms.qpp.conversion.api.services.AuditService;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.QppValidationException;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;
import gov.cms.qpp.test.logging.LoggerContract;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ExceptionHandlerControllerV1Test implements LoggerContract {

	private static ConversionReport report;
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
		when(auditService.failConversion(any(ConversionReport.class)))
				.thenReturn(CompletableFuture.completedFuture(null));
	}

	@Test
	void testTransformExceptionStatusCode() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	void testTransformExceptionHeaderContentType() {
		TransformException exception =
				new TransformException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleTransformException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.APPLICATION_JSON);
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
				.isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@Test
	void testQppValidationExceptionHeaderContentType() {
		QppValidationException exception =
				new QppValidationException("test transform exception", new NullPointerException(), report);

		ResponseEntity<AllErrors> responseEntity = objectUnderTest.handleQppValidationException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.APPLICATION_JSON);
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
				new NoFileInDatabaseException(AdvancedApmHelper.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testFileNotFoundExceptionHeaderContentType() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(AdvancedApmHelper.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.TEXT_PLAIN);
	}

	@Test
	void testFileNotFoundExceptionBody() {
		NoFileInDatabaseException exception =
				new NoFileInDatabaseException(AdvancedApmHelper.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleFileNotFoundException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testInvalidFileTypeExceptionStatusCode() {
		InvalidFileTypeException exception =
				new InvalidFileTypeException(AdvancedApmHelper.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleInvalidFileTypeException(exception);

		assertWithMessage("The response entity's status code must be 422.")
				.that(responseEntity.getStatusCode())
				.isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void testInvalidFileTypeExceptionHeaderContentType() {
		InvalidFileTypeException exception =
				new InvalidFileTypeException(AdvancedApmHelper.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleInvalidFileTypeException(exception);

		assertThat(responseEntity.getHeaders().getContentType())
				.isEquivalentAccordingToCompareTo(MediaType.TEXT_PLAIN);
	}

	@Test
	void testInvalidFileTypeExceptionBody() {
		InvalidFileTypeException exception =
				new InvalidFileTypeException(AdvancedApmHelper.FILE_NOT_FOUND);

		ResponseEntity<String> responseEntity = objectUnderTest.handleInvalidFileTypeException(exception);
		assertThat(responseEntity.getBody()).isEqualTo(AdvancedApmHelper.FILE_NOT_FOUND);
	}

	@Test
	void testHandleAmazonExceptionStatusCode() {
		AmazonServiceException exception = new AmazonServiceException("some message");
		exception.setStatusCode(404);

		ResponseEntity<String> response = objectUnderTest.handleAmazonException(exception);

		Truth.assertThat(response.getStatusCodeValue()).isEqualTo(404);
	}

//	@Test
//	void testHandleAmazonExceptionResponseBody() {
//		AmazonServiceException exception = new AmazonServiceException("some message");
//
//		ResponseEntity<String> response = objectUnderTest.handleAmazonException(exception);
//
//		Truth.assertThat(response.getBody()).contains("some message");
//	}

	@Test
	void testHandleInvalidPurposeExceptionExceptionResponseBody() {
		InvalidPurposeException exception = new InvalidPurposeException("some message");

		ResponseEntity<String> response = objectUnderTest.handleInvalidPurposeException(exception);

		Truth.assertThat(response.getBody()).contains("some message");
	}

	@Test
	void testHandleInvalidPurposeExceptionResponseBodyDoesInterception() throws Exception {
		QrdaControllerV1 mock = Mockito.mock(QrdaControllerV1.class);
		MockMvc mvc = MockMvcBuilders.standaloneSetup(mock)
				.setControllerAdvice(new ExceptionHandlerControllerV1(auditService))
				.build();
		when(mock.uploadQrdaFile(ArgumentMatchers.any(), ArgumentMatchers.anyString())).thenCallRealMethod();

		String purpose = "this is an invalid purpose because it's too long" + UUID.randomUUID();
		RequestBuilder builder = MockMvcRequestBuilders.multipart("/")
			.file("file", ArrayUtils.EMPTY_BYTE_ARRAY)
			.header("Purpose", purpose);
		MvcResult result = mvc.perform(builder).andReturn();
		Truth.assertThat(result.getResponse().getContentAsString()).isEqualTo("Given Purpose (header) is too large. Max length is "
										     + "25, yours was " + purpose.length());
	}

	@Override
	public Class<?> getLoggerType() {
		return ExceptionHandlerControllerV1.class;
	}
}
