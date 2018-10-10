package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.FormattedErrorCode;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;

class FormattedErrorCodeTest {

	@Test
	void testGetErrorCode() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.getErrorCode()).isEqualTo(ErrorCode.UNEXPECTED_ERROR);
	}

	@Test
	void testGetMessage() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.getMessage()).isEqualTo("Some Message");
	}

	@Test
	void testToString() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.toString()).contains(ErrorCode.UNEXPECTED_ERROR.toString());
		Truth.assertThat(o1.toString()).contains("Some Message");
	}

	@Test
	void testEqualsSelf() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		FormattedErrorCode o2 = o1;
		Truth.assertThat(o1).isEqualTo(o2);
	}

	@Test
	void testEqualsNull() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1).isNotEqualTo(null);
	}

	@Test
	void testEqualsDifferentClass() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		FormattedErrorCode o2 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message") { };
		Truth.assertThat(o1).isNotEqualTo(o2);
	}

	@Test
	void testEqualsDifferentErrorCode() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		FormattedErrorCode o2 = new FormattedErrorCode(ErrorCode.PI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID, "Some Message");
		Truth.assertThat(o1).isNotEqualTo(o2);
	}

	@Test
	void testEqualsDifferentMessage() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		FormattedErrorCode o2 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Other Message");
		Truth.assertThat(o1).isNotEqualTo(o2);
	}

	@Test
	void testEquals() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		FormattedErrorCode o2 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1).isEqualTo(o2);
	}

	@Test
	void testHashCode() {
		FormattedErrorCode o1 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		FormattedErrorCode o2 = new FormattedErrorCode(ErrorCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.hashCode()).isEqualTo(o2.hashCode());
	}

}
