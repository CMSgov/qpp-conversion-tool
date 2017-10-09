package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.assertEquals;

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
		assertThat(validations).hasSize(2);
		assertThat(validations.get(0).getMessage()).isEqualTo("error");
		assertThat(validations.get(1).getMessage()).isEqualTo("another");
	}

	@Test
	public void testAddValidationAndGetValidationById() {
		List<Detail> validations = joe.getDetails();
		assertThat(validations).hasSize(0);

		joe.addValidationError(new Detail("err"));

		validations = joe.getDetails();
		assertThat(validations).isNotNull();
		assertThat(validations).hasSize(1);
		assertThat(validations.get(0).getMessage()).isEqualTo("err");
	}

	@Test
	public void testAddValidationByEncodeException() {
		joe.encode((JsonWrapper) null, (Node) null); // the values are not used in the test

		List<Detail> details = joe.getDetails();
		assertWithMessage("Should have one error message")
				.that(details)
				.hasSize(1);
	}
}
