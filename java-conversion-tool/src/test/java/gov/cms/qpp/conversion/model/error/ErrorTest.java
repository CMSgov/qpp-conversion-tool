package gov.cms.qpp.conversion.model.error;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.util.Collections;

import org.junit.Test;

public class ErrorTest {
	@Test
	public void addValidationError() {
		Error objectUnderTest = new Error();

		assertThat("The validation errors should have been null at first", objectUnderTest.getDetails(), is(nullValue()));

		objectUnderTest.addValidationError(new Detail("description", "path"));
		objectUnderTest.addValidationError(new Detail("description", "path"));

		assertThat("The validation errors should no longer be null", objectUnderTest.getDetails(), is(not(nullValue())));
		assertThat("The list should be one", objectUnderTest.getDetails(), hasSize(2));
	}

	@Test
	public void mutability() {
		Error objectUnderTest = new Error();

		objectUnderTest.setSourceIdentifier("meep");
		objectUnderTest.setDetails(Collections.singletonList(new Detail("description", "path")));

		assertThat("The validation errors should no longer be null", objectUnderTest.getDetails(), is(not(nullValue())));
		assertThat("The list should be one", objectUnderTest.getDetails(), hasSize(1));
		assertThat("The source identifier should be set", objectUnderTest.getSourceIdentifier(), is("meep"));
	}
}