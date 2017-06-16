package gov.cms.qpp.conversion.encode;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;

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
		assertEquals(0, joe.getDetails().size());
		joe.addValidationError(new Detail("error"));
		joe.addValidationError(new Detail("another"));
		List<Detail> validations = joe.getDetails();
		assertEquals(2, validations.size());
		assertEquals("error", validations.get(0).getMessage());
		assertEquals("another", validations.get(1).getMessage());
	}

	@Test
	public void testAddValidationAndGetValidationById() {
		List<Detail> validations = joe.getDetails();
		assertEquals(0, validations.size());

		joe.addValidationError(new Detail("err"));

		validations = joe.getDetails();
		assertNotNull(validations);
		assertEquals(1, validations.size());
		assertEquals("err", validations.get(0).getMessage());
	}

	@Test
	public void testAddValidationByEncodeException() {
		joe.encode((JsonWrapper) null, (Node) null); // the values are not used in the test

		List<Detail> details = joe.getDetails();
		assertThat("Should have one error message", details, hasSize(1));
	}
}
