package gov.cms.qpp.conversion.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidationsTest {
	
	@Before
	public void setup() throws Exception {
		Validations.init();
	}
	
	@After
	public void teardown() throws Exception {
		Validations.clear();
	}

	@Test
	public void validationFormatTest() throws Exception {
		
		Validations.addValidation("templateid.1", "validation.1");
		Validations.addValidation("templateid.1", "validation.2");
		Validations.addValidation("templateid.3", "validation.3");

		List<String> checkList = Arrays.asList("templateid.1 - validation.1",
												"templateid.1 - validation.2",
												"templateid.3 - validation.3");
		int count = 0;
		for (String validation : Validations.values()) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}

		assertThat("Expected count", count, is(3));

		checkList = Arrays.asList("validation.1", "validation.2");
		count = 0;
		for (String validation : Validations.getValidationsById("templateid.1")) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}

		assertThat("Expected count", count, is(2));
	}
}
