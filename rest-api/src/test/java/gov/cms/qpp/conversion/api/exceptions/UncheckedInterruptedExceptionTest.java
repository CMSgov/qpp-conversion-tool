package gov.cms.qpp.conversion.api.exceptions;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class UncheckedInterruptedExceptionTest {

	@Test
	public void testConstructor() {
		InterruptedException interruptedException = new InterruptedException();
		UncheckedInterruptedException uncheckedInterruptedException = new UncheckedInterruptedException(interruptedException);

		assertWithMessage("The cause throwable was incorrect.")
				.that(uncheckedInterruptedException).hasCauseThat().isEqualTo(interruptedException);
	}
}
