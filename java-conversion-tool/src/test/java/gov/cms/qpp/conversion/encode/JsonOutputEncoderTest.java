package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonOutputEncoderTest {

	private JsonOutputEncoder joe;

	@Before
	public void before() {
		joe = new JsonOutputEncoder() {
			@Override
			protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
				EncodeException ee = new EncodeException("thrown", new RuntimeException("test"), "id");
				throw ee;
			}
		};
	}

	@Test
	public void testAddValidationAndGetValidations() {
		assertEquals(0, joe.getValidationErrors().size());
		joe.addValidationError(new ValidationError("error"));
		joe.addValidationError(new ValidationError("another"));
		List<ValidationError> validations = joe.getValidationErrors();
		assertEquals(2, validations.size());
		assertEquals("error", validations.get(0).getErrorText());
		assertEquals("another", validations.get(1).getErrorText());
	}

	@Test
	public void testAddValidationAndGetValidationById() {
		List<ValidationError> validations = joe.getValidationErrors();
		assertEquals(0, validations.size());

		joe.addValidationError(new ValidationError("err"));

		validations = joe.getValidationErrors();
		assertNotNull(validations);
		assertEquals(1, validations.size());
		assertEquals("err", validations.get(0).getErrorText());
	}

	@Test
	public void testAddValidationByEncodeException() {
		joe.encode((JsonWrapper) null, (Node) null); // the values are not used in the test

		List<ValidationError> validationErrors = joe.getValidationErrors();
		assertThat("Should have one error message", validationErrors, hasSize(1));
	}
}
