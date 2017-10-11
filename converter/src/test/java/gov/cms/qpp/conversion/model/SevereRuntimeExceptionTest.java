package gov.cms.qpp.conversion.model;

import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class SevereRuntimeExceptionTest {
	@Test
	public void testConstuctor() {
		Throwable cause = new Throwable();
		SevereRuntimeException exception = new SevereRuntimeException(cause);

		assertWithMessage("The exception's cause is incorrect.")
				.that(exception).hasCauseThat().isSameAs(cause);
	}
}