package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.FormattedProblemCode;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;

class FormattedErrorCodeTest {

	@Test
	void testGetErrorCode() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.getProblemCode()).isEqualTo(ProblemCode.UNEXPECTED_ERROR);
	}

	@Test
	void testGetMessage() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.getMessage()).isEqualTo("Some Message");
	}

	@Test
	void testToString() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.toString()).contains(ProblemCode.UNEXPECTED_ERROR.toString());
		Truth.assertThat(o1.toString()).contains("Some Message");
	}

	@Test
	void testEqualsSelf() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		FormattedProblemCode o2 = o1;
		Truth.assertThat(o1).isEqualTo(o2);
	}

	@Test
	void testEqualsNull() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1).isNotEqualTo(null);
	}

	@Test
	void testEqualsDifferentClass() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		FormattedProblemCode o2 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message") { };
		Truth.assertThat(o1).isNotEqualTo(o2);
	}

	@Test
	void testEqualsDifferentErrorCode() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		FormattedProblemCode o2 = new FormattedProblemCode(ProblemCode.PI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID, "Some Message");
		Truth.assertThat(o1).isNotEqualTo(o2);
	}

	@Test
	void testEqualsDifferentMessage() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		FormattedProblemCode o2 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Other Message");
		Truth.assertThat(o1).isNotEqualTo(o2);
	}

	@Test
	void testEquals() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		FormattedProblemCode o2 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1).isEqualTo(o2);
	}

	@Test
	void testHashCode() {
		FormattedProblemCode o1 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		FormattedProblemCode o2 = new FormattedProblemCode(ProblemCode.UNEXPECTED_ERROR, "Some Message");
		Truth.assertThat(o1.hashCode()).isEqualTo(o2.hashCode());
	}

}
