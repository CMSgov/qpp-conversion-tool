package gov.cms.qpp.conversion.api.exceptions;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

class UncheckedInterruptedExceptionTest {

	@Test
	void testConstructor() {
		InterruptedException interruptedException = new InterruptedException();
		UncheckedInterruptedException uncheckedInterruptedException = new UncheckedInterruptedException(interruptedException);

		assertThat(uncheckedInterruptedException).hasCauseThat().isEqualTo(interruptedException);
	}
}
