package gov.cms.qpp.conversion.api.exceptions;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UncheckedInterruptedExceptionTest {

	@Test
	public void testConstructor() {
		InterruptedException interruptedException = new InterruptedException();
		UncheckedInterruptedException uncheckedInterruptedException = new UncheckedInterruptedException(interruptedException);

		assertThat("The cause throwable was incorrect.", uncheckedInterruptedException.getCause(), is(interruptedException));
	}
}
