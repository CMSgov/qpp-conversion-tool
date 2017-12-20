package gov.cms.qpp.conversion.model;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class SevereRuntimeExceptionTest {
	@Test
	void testConstuctor() {
		Throwable cause = new Throwable();
		SevereRuntimeException exception = new SevereRuntimeException(cause);

		assertThat(exception).hasCauseThat().isSameAs(cause);
	}
}