package gov.cms.qpp.conversion.model.error;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

public class AllErrorsTest {

	@Test
	public void errorSourceInit() {
		AllErrors objectUnderTest = new AllErrors();
		assertWithMessage("The error sources should have been null at first")
				.that(objectUnderTest.getErrors())
				.isNull();
	}

	@Test
	public void addErrorSource() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.addError(new Error());

		assertWithMessage("The list should be one")
				.that(objectUnderTest.getErrors()).hasSize(1);
	}

	@Test
	public void addErrorSources() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.addError(new Error());
		objectUnderTest.addError(new Error());

		assertWithMessage("The list should be two")
				.that(objectUnderTest.getErrors()).hasSize(2);
	}

	@Test
	public void setErrorSources() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.setErrors(Collections.singletonList(new Error()));

		assertWithMessage("The list should be one")
				.that(objectUnderTest.getErrors()).hasSize(1);
	}

	@Test
	public void testToString() {
		AllErrors objectUnderTest = new AllErrors();
		Error error = new Error();
		objectUnderTest.addError(error);

		assertWithMessage("Must contain formatted string")
				.that(objectUnderTest.toString()).contains(error.toString());
	}

	@Test
	public void testArgConstructor() {
		List<Error> errors = new ArrayList<>();
		errors.add(new Error());
		new AllErrors(errors);
		assertThat(new AllErrors(errors).getErrors())
				.containsAllIn(errors);
	}
}