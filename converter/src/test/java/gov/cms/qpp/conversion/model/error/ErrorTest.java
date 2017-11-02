package gov.cms.qpp.conversion.model.error;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.google.common.truth.StringSubject;

class ErrorTest {

	@Test
	void testValidationErrorInit() {
		Error objectUnderTest = new Error();
		assertWithMessage("The validation errors should have been empty at first")
				.that(objectUnderTest.getDetails()).isEmpty();
	}

	@Test
	void testAddValidationError() {
		Error objectUnderTest = new Error();
		objectUnderTest.addValidationError(new Detail());
		objectUnderTest.addValidationError(new Detail());

		assertWithMessage("The list should have two items")
				.that(objectUnderTest.getDetails()).hasSize(2);
	}

	@Test
	void testInitializesDetailsWhenNull() {
		Error objectUnderTest = new Error();
		objectUnderTest.setDetails(null);
		objectUnderTest.addValidationError(new Detail());

		assertWithMessage("The list should have zero items")
				.that(objectUnderTest.getDetails()).hasSize(1);
	}

	@Test
	void testMutability() {
		Error objectUnderTest = new Error();

		objectUnderTest.setSourceIdentifier("meep");
		objectUnderTest.setDetails(Collections.singletonList(new Detail()));

		assertWithMessage("The list should be one")
				.that(objectUnderTest.getDetails()).hasSize(1);
		assertWithMessage("The source identifier should be set")
				.that(objectUnderTest.getSourceIdentifier()).isSameAs("meep");
	}

	@Test
	void testToString() {
		Error objectUnderTest = new Error();
		objectUnderTest.setSourceIdentifier("sourceID");
		objectUnderTest.setDetails(Collections.singletonList(Detail.forErrorCode(ErrorCode.UNEXPECTED_ERROR)));
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