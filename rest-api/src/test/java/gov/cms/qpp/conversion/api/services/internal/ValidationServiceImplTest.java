package gov.cms.qpp.conversion.api.services.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.ConversionReport;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.ErrorMessage;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Location;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.test.MockitoExtension;
import gov.cms.qpp.test.helper.JsonTestHelper;

@ExtendWith(MockitoExtension.class)
class ValidationServiceImplTest {

	private ValidationServiceImpl objectUnderTest;

	@Mock
	private Environment environment;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private Converter converter;

	private static Path pathToSubmissionError;
	private static Path pathToSubmissionDuplicateEntryError;
	private static JsonWrapper qppWrapper;
	private static AllErrors convertedErrors;
	private static ErrorMessage submissionError;
	private static ValidationServiceImpl service;

	@BeforeAll
	static void setup() throws IOException {
		service = new ValidationServiceImpl(null);
		pathToSubmissionError = Path.of("src/test/resources/submissionErrorFixture.json");
		pathToSubmissionDuplicateEntryError = Path.of("src/test/resources/submissionDuplicateEntryErrorFixture.json");
		Path toConvert = Path.of("../qrda-files/valid-QRDA-III-latest.xml");
		Context context = new Context();
		qppWrapper = new Converter(new PathSource(toConvert), context).transform();
		prepAllErrors();
	}

	private static void prepAllErrors() throws IOException {
		submissionError = JsonTestHelper.readJsonAtJsonPath(
			pathToSubmissionError, "$", ErrorMessage.class);

		String errorJson = new ObjectMapper().writeValueAsString(submissionError);
		convertedErrors = service.convertQppValidationErrorsToQrda(errorJson, qppWrapper);
	}

	@BeforeEach
	void before() throws NoSuchFieldException, IllegalAccessException {
		ValidationServiceImpl meep = new ValidationServiceImpl(environment);
		Field rt = meep.getClass().getDeclaredField("restTemplate");
		rt.setAccessible(true);
		rt.set(meep, restTemplate);
		rt.setAccessible(false);
		objectUnderTest = spy(meep);

		ConversionReport report = mock(ConversionReport.class);
		when(report.getEncodedWithMetadata()).thenReturn(qppWrapper);
		when(converter.getReport()).thenReturn(report);
	}

	@Test
	void testNullValidationUrl() {
		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn(null);

		objectUnderTest.validateQpp(null);

		verifyNoInteractions(restTemplate);
	}

	@Test
	void testEmptyValidationUrl() {
		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn("");

		objectUnderTest.validateQpp(null);

		verifyNoInteractions(restTemplate);
	}

	@Test
	void testValidationPass() {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(HttpStatus.OK));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		objectUnderTest.validateQpp(converter.getReport());

		verify(spiedResponseEntity, never()).getBody();
	}

	@Test
	void testValidationFail() throws IOException {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(FileUtils.readFileToString(pathToSubmissionError.toFile(), "UTF-8") ,HttpStatus.UNPROCESSABLE_ENTITY));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		TransformException thrown = assertThrows(TransformException.class,
				() -> objectUnderTest.validateQpp(converter.getReport()));
	}

	@Test
	void testHeaderCreation() {
		HttpHeaders headers = objectUnderTest.getHeaders();

		assertThat(headers.getFirst(HttpHeaders.CONTENT_TYPE), is(ValidationServiceImpl.CONTENT_TYPE));
		assertThat(headers.getFirst(HttpHeaders.ACCEPT), is(ValidationServiceImpl.CONTENT_TYPE));
	}

	@Test
	void testHeaderCreationNoAuth() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn(null);
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertNull(headers.get(HttpHeaders.AUTHORIZATION));
	}

	@Test
	void testHeaderCreationNoAuthEmpty() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn("");
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertNull(headers.get(HttpHeaders.AUTHORIZATION));
	}

	@Test
	void testHeaderCreationAuth() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn("meep");
		HttpHeaders headers = objectUnderTest.getHeaders();

		assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION), containsString("meep"));
	}

	@Test
	void testJsonDeserialization() {
		assertThat(convertedErrors.getErrors().size(), is(1));
	}

	@Test
	void testJsonDesrializationDuplicateEntry() throws IOException {
		String errorJson = new String(Files.readAllBytes(pathToSubmissionDuplicateEntryError));
		convertedErrors = service.convertQppValidationErrorsToQrda(errorJson, qppWrapper);

		assertThat(convertedErrors.getErrors().size(), is(1));

		assertThat(convertedErrors.getErrors().get(0).getDetails().get(0).getMessage(),
			startsWith(ValidationServiceImpl.SV_LABEL));
	}

	@Test
	void testQppToQrdaErrorPathConversion() {
		Detail detail = submissionError.getError().getDetails().get(0);
		Detail mappedDetails = convertedErrors.getErrors().get(0).getDetails().get(0);

		assertNotEquals(mappedDetails.getLocation().getPath(), detail.getLocation().getPath());
	}

	@Test
	void testCheckForValidationUrlVariableLoggingIfPresent() {
		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn("mock");
		objectUnderTest.checkForValidationUrlVariable();
		Mockito.verify(objectUnderTest, Mockito.times(1)).apiLog(Constants.VALIDATION_URL_ENV_VARIABLE + " is set to mock");
	}

	@Test
	void testCheckForValidationUrlVariableLoggingIfAbsent() {
		objectUnderTest.checkForValidationUrlVariable();
		Mockito.verify(objectUnderTest, Mockito.times(1)).apiLog(Constants.VALIDATION_URL_ENV_VARIABLE + " is unset");
	}

//	@Test
//	void testInvalidSubmissionResponseJsonPath() throws IOException {
//		pathToSubmissionError = Path.of("src/test/resources/invalidSubmissionErrorFixture.json");
//		String errorJson = FileUtils.readFileToString(pathToSubmissionError.toFile(), StandardCharsets.UTF_8);
//		convertedErrors = service.convertQppValidationErrorsToQrda(errorJson, qppWrapper);
//
//		convertedErrors.getErrors().stream().flatMap(error -> error.getDetails().stream())
//			.map(Detail::getLocation).map(Location::getPath)
//			.forEach(path -> assertThat(path, is(ValidationServiceImpl.UNABLE_PROVIDE_XPATH)));
//	}
}
