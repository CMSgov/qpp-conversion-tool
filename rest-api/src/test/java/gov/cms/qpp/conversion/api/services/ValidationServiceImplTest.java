package gov.cms.qpp.conversion.api.services;

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
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
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

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private static Path pathToSubmissionError;

	private static JsonWrapper qppWrapper;

	private static AllErrors convertedErrors;

	private static ErrorMessage submissionError;

	@BeforeClass
	public static void setup() throws IOException {
		pathToSubmissionError = Paths.get("src/test/resources/submissionErrorFixture.json");
		Path toConvert = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		qppWrapper = new JsonWrapper(new Converter(new PathQrdaSource(toConvert)).transform(), false);
		prepAllErrors();
	}

	private static void prepAllErrors() throws IOException {
		ValidationServiceImpl service = new ValidationServiceImpl();
		submissionError = JsonHelper.readJsonAtJsonPath(
			pathToSubmissionError, "$", ErrorMessage.class);

		String errorJson = new ObjectMapper().writeValueAsString(submissionError);
		convertedErrors = service.convertQppValidationErrorsToQrda(errorJson, qppWrapper);
	}

	@Test
	public void testNullValidationUrl() {
		when(environment.getProperty(eq(ValidationServiceImpl.VALIDATION_URL_ENV_NAME))).thenReturn(null);

		objectUnderTest.validateQpp(null);

		verifyZeroInteractions(restTemplate);
	}

	@Test
	public void testEmptyValidationUrl() {
		when(environment.getProperty(eq(ValidationServiceImpl.VALIDATION_URL_ENV_NAME))).thenReturn("");

		objectUnderTest.validateQpp(null);

		verifyZeroInteractions(restTemplate);
	}

	@Test
	public void testValidationPass() {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(ValidationServiceImpl.VALIDATION_URL_ENV_NAME))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(HttpStatus.OK));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		objectUnderTest.validateQpp(new JsonWrapper());

		verify(spiedResponseEntity, never()).getBody();
	}

	@Test
	public void testValidationFail() throws IOException {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(ValidationServiceImpl.VALIDATION_URL_ENV_NAME))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(FileUtils.readFileToString(pathToSubmissionError.toFile(), "UTF-8") ,HttpStatus.UNPROCESSABLE_ENTITY));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		thrown.expect(TransformException.class);
		thrown.expectMessage("Converted QPP failed validation");

		objectUnderTest.validateQpp(qppWrapper);
	}

	@Test
	public void testHeaderCreation() {
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertEquals(HttpHeaders.CONTENT_TYPE + " should be " + ValidationServiceImpl.CONTENT_TYPE,
				headers.getFirst(HttpHeaders.CONTENT_TYPE), ValidationServiceImpl.CONTENT_TYPE);
		assertEquals(HttpHeaders.ACCEPT + " should be " + ValidationServiceImpl.CONTENT_TYPE,
				headers.getFirst(HttpHeaders.CONTENT_TYPE), ValidationServiceImpl.CONTENT_TYPE);
	}

	@Test
	public void testHeaderCreationNoAuth() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn(null);
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertNull(HttpHeaders.AUTHORIZATION + " should not be set", headers.get(HttpHeaders.AUTHORIZATION));
	}

	@Test
	public void testHeaderCreationNoAuthEmpty() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn("");
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertNull(HttpHeaders.AUTHORIZATION + " should not be set", headers.get(HttpHeaders.AUTHORIZATION));
	}

	@Test
	public void testHeaderCreationAuth() {
		when(environment.getProperty(eq(Constants.SUBMISSION_API_TOKEN_ENV_VARIABLE))).thenReturn("meep");
		HttpHeaders headers = objectUnderTest.getHeaders();
		assertThat(HttpHeaders.AUTHORIZATION + " should be set",
				headers.getFirst(HttpHeaders.AUTHORIZATION), containsString("meep"));
	}

	@Test
	public void testJsonDeserialization() {
		assertThat("Error json should map to AllErrors", convertedErrors.getErrors(), hasSize(1));
	}

	@Test
	public void testQppToQrdaErrorPathConversion() {
		Detail detail = submissionError.getError().getDetails().get(0);
		Detail mappedDetails = convertedErrors.getErrors().get(0).getDetails().get(0);

		System.out.println(detail.getPath());
		System.out.println(mappedDetails.getPath());
		assertNotEquals("Json path should be converted to xpath",
			detail.getPath(), mappedDetails.getPath());
	}
}
