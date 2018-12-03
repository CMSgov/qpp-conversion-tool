package gov.cms.qpp.conversion.api.exceptions;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class InvalidFileTypeExceptionTest {

	@Test
	void testConstructor() {
		InvalidFileTypeException invalidFileTypeException = new InvalidFileTypeException("test");


		assertThat(invalidFileTypeException).hasMessageThat().isEqualTo("test");
	}
}
