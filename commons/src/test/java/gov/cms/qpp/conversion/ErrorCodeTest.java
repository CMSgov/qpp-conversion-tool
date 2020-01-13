package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.test.enums.EnumContract;

import java.util.Arrays;
import java.util.UUID;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ErrorCodeTest implements EnumContract {

	@ParameterizedTest
	@EnumSource(ProblemCode.class)
	void testGetErrorCodeReturnsSelf(ProblemCode errorCode) {
		Truth.assertThat(errorCode.getProblemCode()).isSameInstanceAs(errorCode);
	}

	@ParameterizedTest
	@EnumSource(ProblemCode.class)
	void testGetMessageCodeReturnsNonNull(ProblemCode errorCode) {
		Truth.assertThat(errorCode.getMessage()).isNotNull();
	}

	@ParameterizedTest
	@EnumSource(ProblemCode.class)
	void testGetByCode(ProblemCode errorCode) {
		Truth.assertThat(ProblemCode.getByCode(errorCode.getCode())).isSameInstanceAs(errorCode);
	}

	@ParameterizedTest
	@EnumSource(ProblemCode.class)
	void testFormat(ProblemCode errorCode) {
		try {
			String random = UUID.randomUUID().toString();
			Truth.assertThat(errorCode.format(random).getMessage()).contains(random);
		} catch (IllegalStateException exception) {
			Truth.assertThat(exception).hasMessageThat().isEqualTo(errorCode + " does not support formatting");
		}
	}

	@Test
	void testGetCodeIsUnique() {
		long count = Arrays.stream(ProblemCode.values()).mapToInt(ProblemCode::getCode).distinct().count();
		long expected = ProblemCode.values().length;
		Truth.assertThat(count).isEqualTo(expected);
	}

	@Test
	void testFormatOnNonFormattedErrorCode() {
		Assertions.assertThrows(IllegalStateException.class, ProblemCode.UNEXPECTED_ERROR::format);
	}

	@Test
	void testFormatOnFormattedErrorCode() {
		ProblemCode code = ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE;
		Truth.assertThat(code.format("mock").getMessage()).isEqualTo(code.getMessage()
				.replace("`(Numerator or Denominator)`", "mock"));
	}

	@Test
	void testFormattedEqualsIdentity() {
		LocalizedProblem formatted = formatted("mock");
		new EqualsTester().addEqualityGroup(formatted).testEquals();
	}

	@Test
	void testGetMessagePrependsConversionToolLabel() {
		ProblemCode code = ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE;
		Truth.assertThat(code.getMessage()).startsWith(ProblemCode.CT_LABEL);
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
	void testErrorCodeOrder() {
		int last = -1;
		for (ProblemCode errorCode : ProblemCode.values()) {
			int currentCode = errorCode.getCode();
			Truth.assertThat(last).isLessThan(currentCode);
			last = currentCode;
		}
	}

	private LocalizedProblem formatted(String salt) {
		return ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format(salt);
	}

	private LocalizedProblem formattedAlt(String salt) {
		return ProblemCode.NUMERATOR_DENOMINATOR_MUST_BE_INTEGER.format(salt);
	}

	@Override
	public Class<? extends Enum<?>> getEnumType() {
		return ProblemCode.class;
	}

}
