package gov.cms.qpp.conversion.model;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

class SevereRuntimeExceptionTest {
	@Test
	void testConstuctor() {
		Throwable cause = new Throwable();
		SevereRuntimeException exception = new SevereRuntimeException(cause);

		assertWithMessage("The exception's cause is incorrect.")
				.that(exception).hasCauseThat().isSameAs(cause);
	}
}