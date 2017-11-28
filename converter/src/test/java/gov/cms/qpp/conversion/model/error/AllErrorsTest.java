package gov.cms.qpp.conversion.model.error;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class AllErrorsTest {

	@Test
	void testErrorSourceInit() {
		AllErrors objectUnderTest = new AllErrors();
		assertWithMessage("The error sources should have been null at first")
				.that(objectUnderTest.getErrors())
				.isNull();
	}

	@Test
	void testAddErrorSource() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.addError(new Error());

		assertWithMessage("The list should be one")
				.that(objectUnderTest.getErrors()).hasSize(1);
	}

	@Test
	void testAddErrorSources() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.addError(new Error());
		objectUnderTest.addError(new Error());

		assertWithMessage("The list should be two")
				.that(objectUnderTest.getErrors()).hasSize(2);
	}

	@Test
	void testSetErrorSources() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.setErrors(Collections.singletonList(new Error()));

		assertWithMessage("The list should be one")
				.that(objectUnderTest.getErrors()).hasSize(1);
	}

	@Test
	void testToString() {
		AllErrors objectUnderTest = new AllErrors();
		Error error = new Error();
		objectUnderTest.addError(error);

		assertWithMessage("Must contain formatted string")
				.that(objectUnderTest.toString()).contains(error.toString());
	}

	@Test
	void testArgConstructor() {
		List<Error> errors = new ArrayList<>();
		errors.add(new Error());
		new AllErrors(errors);
		assertThat(new AllErrors(errors).getErrors())
				.containsAllIn(errors);
	}
}