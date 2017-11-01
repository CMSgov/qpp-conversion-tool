package gov.cms.qpp.conversion.model.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;

class ErrorCodeTest {

	@Test
	void testFormatOnNonFormattedErrorCode() {
		Assertions.assertThrows(IllegalStateException.class, ErrorCode.UNEXPECTED_ERROR::format);
	}

	@Test
	void testFormatOnFormattedErrorCode() {
		ErrorCode code = ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE;
		Truth.assertThat(code.format("mock").getMessage()).isEqualTo(code.getMessage().replace("%s", "mock"));
	}

	@Test
	void testFormattedEqualsIdentity() {
		LocalizedError formatted = formatted("mock");
		new EqualsTester().addEqualityGroup(formatted).testEquals();
	}

	@Test
	void testFormattedEqualsNull() {
		Truth.assertThat(formatted("mock")).isNotEqualTo(null);
	}

	@Test
	void testFormattedEqualsNonFormatted() {
		Truth.assertThat(formatted("mock")).isNotEqualTo(new Object());
	}

	@Test
	void testFormattedEqualsFormattedWithDifferentSalt() {
		Truth.assertThat(formatted("mock")).isNotEqualTo(formatted("mock2"));
	}

	private LocalizedError formatted(String salt) {
		return ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format(salt);
	}

}
