package gov.cms.qpp.conversion.model.error;

import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;

public class ErrorTest {

	@Test
	public void validationErrorInit() {
		Error objectUnderTest = new Error();
		assertThat("The validation errors should have been empty at first", objectUnderTest.getDetails(), hasSize(0));
	}

	@Test
	public void addValidationError() {
		Error objectUnderTest = new Error();
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

	@Test
	public void testToString() {
		Error objectUnderTest = new Error();
		objectUnderTest.setSourceIdentifier("sourceID");
		objectUnderTest.setDetails(Collections.singletonList(new Detail("description", "path")));
		objectUnderTest.setType("aType");
		objectUnderTest.setMessage("coolMessage");

		assertThat("Must contain formatted string", objectUnderTest.toString(),
				allOf(containsString(String.valueOf(objectUnderTest.getSourceIdentifier())),
					containsString(String.valueOf(objectUnderTest.getDetails())),
					containsString(String.valueOf(objectUnderTest.getType())),
					containsString(String.valueOf(objectUnderTest.getMessage()))));
	}
}