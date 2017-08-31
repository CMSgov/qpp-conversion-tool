package gov.cms.qpp.conversion.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.internal.matchers.ThrowableCauseMatcher.hasCause;

public class SevereRuntimeExceptionTest {
	@Test
	public void testConstuctor() {
		Throwable cause = new Throwable();
		SevereRuntimeException exception = new SevereRuntimeException(cause);

		assertThat("The exception's cause is incorrect.", exception, hasCause(is(cause)));
	}
}