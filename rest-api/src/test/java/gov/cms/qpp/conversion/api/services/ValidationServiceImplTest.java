package gov.cms.qpp.conversion.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.api.model.Constants;
import gov.cms.qpp.conversion.api.model.ErrorMessage;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ValidationServiceImplTest {

	@InjectMocks
	private ValidationServiceImpl objectUnderTest;

	@Mock
	private Environment environment;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private Converter converter;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static Path pathToSubmissionError;
	private static Path pathToSubmissionDuplicateEntryError;
	private static JsonWrapper qppWrapper;
	private static AllErrors convertedErrors;
	private static ErrorMessage submissionError;
	private static ValidationServiceImpl service;

	@BeforeClass
	public static void setup() throws IOException {
		service = new ValidationServiceImpl();
		pathToSubmissionError = Paths.get("src/test/resources/submissionErrorFixture.json");
		pathToSubmissionDuplicateEntryError = Paths.get("src/test/resources/submissionDuplicateEntryErrorFixture.json");
		Path toConvert = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		qppWrapper = new JsonWrapper(new Converter(new PathQrdaSource(toConvert)).transform(), false);
		prepAllErrors();
	}

	private static void prepAllErrors() throws IOException {
		submissionError = JsonHelper.readJsonAtJsonPath(
			pathToSubmissionError, "$", ErrorMessage.class);

		String errorJson = new ObjectMapper().writeValueAsString(submissionError);
		convertedErrors = service.convertQppValidationErrorsToQrda(errorJson, qppWrapper);
	}

	@Before
	public void before() {
		Converter.ConversionReport report = mock(Converter.ConversionReport.class);
		when(report.getEncoded()).thenReturn(qppWrapper);
		when(converter.getReport()).thenReturn(report);
	}

	@Test
	public void testNullValidationUrl() {
		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn(null);

		objectUnderTest.validateQpp(null);

		verifyZeroInteractions(restTemplate);
	}

	@Test
	public void testEmptyValidationUrl() {
		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn("");

		objectUnderTest.validateQpp(null);

		verifyZeroInteractions(restTemplate);
	}

	@Test
	public void testValidationPass() {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(HttpStatus.OK));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		objectUnderTest.validateQpp(converter.getReport());

		verify(spiedResponseEntity, never()).getBody();
	}

	@Test
	public void testValidationFail() throws IOException {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(Constants.VALIDATION_URL_ENV_VARIABLE))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(FileUtils.readFileToString(pathToSubmissionError.toFile(), "UTF-8") ,HttpStatus.UNPROCESSABLE_ENTITY));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		thrown.expect(TransformException.class);
		thrown.expectMessage("Converted QPP failed validation");

		objectUnderTest.validateQpp(converter.getReport());
	}

	@Test
	public void testHeaderCreation() {
		HttpHeaders headers = objectUnderTest.getHeaders();

		assertThat(headers.getFirst(HttpHeaders.CONTENT_TYPE)).isEqualTo(ValidationServiceImpl.CONTENT_TYPE);
		assertThat(headers.getFirst(HttpHeaders.ACCEPT)).isEqualTo(ValidationServiceImpl.CONTENT_TYPE);
	}

	@Test
	public void testHeaderCreationNoAuth() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn(null);
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertThat(headers.get(HttpHeaders.AUTHORIZATION)).isNull();
	}

	@Test
	public void testHeaderCreationNoAuthEmpty() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn("");
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertThat(headers.get(HttpHeaders.AUTHORIZATION)).isNull();
	}

	@Test
	public void testHeaderCreationAuth() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn("meep");
		HttpHeaders headers = objectUnderTest.getHeaders();

		assertThat(headers.getFirst(HttpHeaders.AUTHORIZATION)).contains("meep");
	}

	@Test
	public void testJsonDeserialization() {
		assertWithMessage("Error json should map to AllErrors")
				.that(convertedErrors.getErrors())
				.hasSize(1);
	}

	@Test
	public void testJsonDesrializationDuplicateEntry() throws IOException {
		String errorJson = new String(Files.readAllBytes(pathToSubmissionDuplicateEntryError));
		convertedErrors = service.convertQppValidationErrorsToQrda(errorJson, qppWrapper);

		assertWithMessage("Error json should map to AllErrors")
				.that(convertedErrors.getErrors())
				.hasSize(1);
	}

	@Test
	public void testQppToQrdaErrorPathConversion() {
		Detail detail = submissionError.getError().getDetails().get(0);
		Detail mappedDetails = convertedErrors.getErrors().get(0).getDetails().get(0);

		assertWithMessage("Json path should be converted to xpath")
				.that(detail.getPath())
				.isNotEqualTo(mappedDetails.getPath());
	}
}
