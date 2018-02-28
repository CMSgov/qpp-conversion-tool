package gov.cms.qpp.conversion;

import gov.cms.qpp.conversion.model.error.ErrorCode;

import java.util.Arrays;
import java.util.UUID;

import com.google.common.truth.Truth;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ErrorCodeTest {

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
	}

}
