package gov.cms.qpp.conversion.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.api.model.ErrorMessage;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertNotEquals;
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

	@Test
	public void testNoValidationUrl() {
		when(environment.getProperty(eq(ValidationServiceImpl.VALIDATION_URL_ENV_NAME))).thenReturn(null);

		objectUnderTest.validateQpp(null, null);

		verifyZeroInteractions(restTemplate);
	}

	@Test
	public void testValidationPass() {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(ValidationServiceImpl.VALIDATION_URL_ENV_NAME))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(HttpStatus.OK));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		objectUnderTest.validateQpp(new JsonWrapper(), null);

		verify(spiedResponseEntity, never()).getBody();
	}

	@Test
	public void testValidationFail() {
		String validationUrl = "https://qpp.net/validate";

		when(environment.getProperty(eq(ValidationServiceImpl.VALIDATION_URL_ENV_NAME))).thenReturn(validationUrl);
		ResponseEntity<String> spiedResponseEntity = spy(new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY));
		when(restTemplate.postForEntity(eq(validationUrl), any(HttpEntity.class), eq(String.class))).thenReturn(spiedResponseEntity);

		thrown.expect(TransformException.class);
		thrown.expectMessage("Converted QPP failed validation");

		objectUnderTest.validateQpp(new JsonWrapper(), null);
	}

	private static Path path;
	private static JsonWrapper wrapper;
	private static AllErrors errors;
	private static ErrorMessage message;


	@BeforeClass
	public static void setup() throws IOException {
		path = Paths.get("src/test/resources/submissionErrorFixture.json");
		Path toConvert = Paths.get("../qrda-files/valid-QRDA-III-latest.xml");
		wrapper = new JsonWrapper(new Converter(toConvert).transform(), false);
		prepAllErrors();
	}

	private static void prepAllErrors() throws IOException {
		ValidationServiceImpl service = new ValidationServiceImpl();
		message = JsonHelper.readJsonAtJsonPath(
			path, "$.invalidMeasureId", ErrorMessage.class);

		String errorJson = new ObjectMapper().writeValueAsString(message);
		errors = service.convertQppValidationErrorsToQrda(errorJson, wrapper);
	}

	@Test
	public void testJsonDeserialization() {
		assertThat("Error json should map to AllErrors", errors.getErrors(), hasSize(1));
	}

	@Test
	public void testQppToQrdaErrorPathConversion() {
		Detail detail = message.getError().getDetails().get(0);
		Detail mappedDetails = errors.getErrors().get(0).getDetails().get(0);

		assertNotEquals("Json path should be converted to xpath",
			detail.getPath(), mappedDetails.getPath());
	}
}
