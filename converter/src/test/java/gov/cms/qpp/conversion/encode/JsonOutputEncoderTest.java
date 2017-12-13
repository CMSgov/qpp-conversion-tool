package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;

class JsonOutputEncoderTest {

	private JsonOutputEncoder joe;

	@BeforeEach
	void before() {
		joe = new JsonOutputEncoder() {
			@Override
			protected void internalEncode(JsonWrapper wrapper, Node node) throws EncodeException {
				EncodeException ee = new EncodeException("thrown", new RuntimeException("test"), "id");
				throw ee;
			}
		};
	}

	@Test
	void testAddValidationAndGetValidations() {
		assertThat(joe.getDetails()).isEmpty();
		Detail detail1 = new Detail();
		detail1.setMessage("error");
		Detail detail2 = new Detail();
		detail2.setMessage("another");
		joe.addValidationError(detail1);
		joe.addValidationError(detail2);
		List<Detail> validations = joe.getDetails();
		assertThat(validations).hasSize(2);
		assertThat(validations.get(0).getMessage()).isEqualTo("error");
		assertThat(validations.get(1).getMessage()).isEqualTo("another");
	}

	@Test
	void testAddValidationAndGetValidationById() {
		List<Detail> validations = joe.getDetails();
		assertThat(validations).hasSize(0);

		Detail detail = new Detail();
		detail.setMessage("err");
		joe.addValidationError(detail);

		validations = joe.getDetails();
		assertThat(validations).isNotNull();
		assertThat(validations).hasSize(1);
		assertThat(validations.get(0).getMessage()).isEqualTo("err");
	}

	@Test
	void testAddValidationByEncodeException() {
		joe.encode((JsonWrapper) null, (Node) null); // the values are not used in the test

		List<Detail> details = joe.getDetails();
		assertWithMessage("Should have one error message")
				.that(details)
				.hasSize(1);
	}
}
