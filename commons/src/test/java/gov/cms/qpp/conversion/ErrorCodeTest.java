package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.test.enums.EnumContract;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ErrorCodeTest implements EnumContract {

	@ParameterizedTest
	@EnumSource(ErrorCode.class)
	void testGetErrorCodeReturnsSelf(ErrorCode errorCode) {
		Truth.assertThat(errorCode.getErrorCode()).isSameAs(errorCode);
	}

	@ParameterizedTest
	@EnumSource(ErrorCode.class)
	void testGetMessageCodeReturnsNonNull(ErrorCode errorCode) {
		Truth.assertThat(errorCode.getMessage()).isNotNull();
	}

	@ParameterizedTest
	@EnumSource(ErrorCode.class)
	void testGetByCode(ErrorCode errorCode) {
		Truth.assertThat(ErrorCode.getByCode(errorCode.getCode())).isSameAs(errorCode);
	}

	@ParameterizedTest
	@EnumSource(ErrorCode.class)
	void testFormat(ErrorCode errorCode) {
		try {
			String random = UUID.randomUUID().toString();
			Truth.assertThat(errorCode.format(random).getMessage()).contains(random);
		} catch (IllegalStateException exception) {
			Truth.assertThat(exception).hasMessageThat().isEqualTo(errorCode + " does not support formatting");
		}
	}

	@Test
	void testGetCodeIsUnique() {
		long count = Arrays.stream(ErrorCode.values()).mapToInt(ErrorCode::getCode).count();
		long expected = ErrorCode.values().length;
		Truth.assertThat(count).isEqualTo(expected);
	}	@Test
	void testFormatOnNonFormattedErrorCode() {
		Assertions.assertThrows(IllegalStateException.class, ErrorCode.UNEXPECTED_ERROR::format);
	}

	@Test
	void testFormatOnFormattedErrorCode() {
		ErrorCode code = ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE;
		Truth.assertThat(code.format("mock").getMessage()).isEqualTo(code.getMessage()
				.replace("`(Numerator or Denominator)`", "mock"));
	}

	@Test
	void testFormattedEqualsIdentity() {
		LocalizedError formatted = formatted("mock");
		new EqualsTester().addEqualityGroup(formatted).testEquals();
	}

	@Test
	void testGetMessagePrependsConversionToolLabel() {
		ErrorCode code = ErrorCode.NUMERATOR_DENOMINATOR_INVALID_VALUE;
		Truth.assertThat(code.getMessage()).startsWith(ErrorCode.CT_LABEL);
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
