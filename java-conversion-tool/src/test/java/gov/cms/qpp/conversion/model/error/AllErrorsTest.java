package gov.cms.qpp.conversion.model.error;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;

public class AllErrorsTest {

	@Test
	public void errorSourceInit() {
		AllErrors objectUnderTest = new AllErrors();
		assertThat("The error sources should have been null at first", objectUnderTest.getErrors(), is(nullValue()));
	}

	@Test
	public void addErrorSource() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.addError(new Error());

		assertThat("The error sources should no longer be null", objectUnderTest.getErrors(), is(not(nullValue())));
		assertThat("The list should be one", objectUnderTest.getErrors(), hasSize(1));
	}

	@Test
	public void addErrorSources() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.addError(new Error());
		objectUnderTest.addError(new Error());

		assertThat("The error sources should no longer be null", objectUnderTest.getErrors(), is(not(nullValue())));
		assertThat("The list should be two", objectUnderTest.getErrors(), hasSize(2));
	}

	@Test
	public void setErrorSources() {
		AllErrors objectUnderTest = new AllErrors();
		objectUnderTest.setErrors(Collections.singletonList(new Error()));

		assertThat("The error sources should no longer be null", objectUnderTest.getErrors(), is(not(nullValue())));
		assertThat("The list should be one", objectUnderTest.getErrors(), hasSize(1));
	}

	@Test
	public void testToString() {
		AllErrors objectUnderTest = new AllErrors();
		Error error = new Error();
		objectUnderTest.addError(error);

		assertThat("Must contain formatted string", objectUnderTest.toString(), containsString(error.toString()));
	}
}