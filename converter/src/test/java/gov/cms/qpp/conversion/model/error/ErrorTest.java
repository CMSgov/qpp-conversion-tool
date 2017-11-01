package gov.cms.qpp.conversion.model.error;

import com.google.common.truth.StringSubject;
import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.assertWithMessage;

public class ErrorTest {

	@Test
	public void validationErrorInit() {
		Error objectUnderTest = new Error();
		assertWithMessage("The validation errors should have been empty at first")
				.that(objectUnderTest.getDetails()).isEmpty();
	}

	@Test
	public void addValidationError() {
		Error objectUnderTest = new Error();
		objectUnderTest.addValidationError(new Detail("description", "path"));
		objectUnderTest.addValidationError(new Detail("description", "path"));

		assertWithMessage("The list should have two items")
				.that(objectUnderTest.getDetails()).hasSize(2);
	}

	@Test
	public void initializesDetailsWhenNull() {
		Error objectUnderTest = new Error();
		objectUnderTest.setDetails(null);
		objectUnderTest.addValidationError(new Detail());

		assertWithMessage("The list should have zero items")
				.that(objectUnderTest.getDetails()).hasSize(1);
	}

	@Test
	public void mutability() {
		Error objectUnderTest = new Error();

		objectUnderTest.setSourceIdentifier("meep");
		objectUnderTest.setDetails(Collections.singletonList(new Detail("description", "path")));

		assertWithMessage("The list should be one")
				.that(objectUnderTest.getDetails()).hasSize(1);
		assertWithMessage("The source identifier should be set")
				.that(objectUnderTest.getSourceIdentifier()).isSameAs("meep");
	}

	@Test
	public void testToString() {
		Error objectUnderTest = new Error();
		objectUnderTest.setSourceIdentifier("sourceID");
		objectUnderTest.setDetails(Collections.singletonList(new Detail("description", "path")));
		objectUnderTest.setType("aType");
		objectUnderTest.setMessage("coolMessage");

		StringSubject subject = assertWithMessage("Must contain formatted string")
				.that(objectUnderTest.toString());
		subject.contains(String.valueOf(objectUnderTest.getSourceIdentifier()));
		subject.contains(String.valueOf(objectUnderTest.getDetails()));
		subject.contains(String.valueOf(objectUnderTest.getType()));
		subject.contains(String.valueOf(objectUnderTest.getMessage()));
	}
}