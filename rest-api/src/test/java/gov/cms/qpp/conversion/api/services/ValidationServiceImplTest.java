package gov.cms.qpp.conversion.api.services;

import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.TransformException;
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
}
