package gov.cms.qpp.conversion;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Test for the TransformationStatus to increase jacoco coverage
 */
public class TransformationStatusTest {

	@Test
	public void testValues() {
		TransformationStatus[] values = TransformationStatus.values();
		assertThat("We have three values", values.length, is(3));
	}
	@Test
	public void testValueOf() {
		TransformationStatus success = TransformationStatus.valueOf("SUCCESS");
		TransformationStatus error = TransformationStatus.valueOf("ERROR");
		TransformationStatus nonRecoverable  = TransformationStatus.valueOf("NON_RECOVERABLE");
		assertThat("Success is one ", success, is(TransformationStatus.SUCCESS));
		assertThat("Error is one ", error, is(TransformationStatus.ERROR));
		assertThat("Non Recoverable is one ", nonRecoverable, is(TransformationStatus.NON_RECOVERABLE));
	}

}