package gov.cms.qpp.conversion.model.error;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;

import gov.cms.qpp.test.enums.EnumContract;

class ErrorCodeTest implements EnumContract {

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

	@Test
	void testFormattedEqualsFormattedWithDifferentErrorCode() {
		Truth.assertThat(formatted("mock")).isNotEqualTo(formattedAlt("mock2"));
	}

	@Test
	void testAllValuesHaveUniqueCodes() {
		Set<Integer> codes = new HashSet<>();
		for (ErrorCode error : ErrorCode.values()) {
			Truth.assertThat(codes).doesNotContain(error.getCode());
			codes.add(error.getCode());
		}
	}

	private LocalizedError formatted(String salt) {
		return ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format(salt);
	}

	private LocalizedError formattedAlt(String salt) {
		return ErrorCode.NUMERATOR_DENOMINATOR_MUST_BE_INTEGER.format(salt);
	}

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return ErrorCode.class;
	}

}
