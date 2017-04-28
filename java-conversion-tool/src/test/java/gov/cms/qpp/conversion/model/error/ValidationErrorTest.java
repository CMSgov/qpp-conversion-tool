package gov.cms.qpp.conversion.model.error;


import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ValidationErrorTest {
	@Test
	public void stringRepresentation() {
		ValidationError objectUnderTest = new ValidationError("text", "path");

		assertThat("toString representation", objectUnderTest.toString(), is("ValidationError{errorText='text', path='path'}"));
	}
}