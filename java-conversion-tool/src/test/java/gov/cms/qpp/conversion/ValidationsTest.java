package gov.cms.qpp.conversion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class ValidationsTest {
	
	Validations<String, String> validations;
	
	@Before
	public void setup() throws Exception {
		validations = new Validations<>();
	}
	
	
	@Test
	public void validationFormatTest() throws Exception {
		
		validations.addValidation("templateid.1", "validation.1");
		validations.addValidation("templateid.1", "validation.2");
		validations.addValidation("templateid.3", "validation.3");
		
		List<String> checkList = Arrays.asList("templateid.1 - validation.1",
												"templateid.1 - validation.2",
												"templateid.3 - validation.3");
		int count = 0;
		for (String validation : validations.validations()) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}
		
		assertThat("Expected count", count, is(3));
		
		checkList = Arrays.asList("validation.1", "validation.2");
		count = 0;
		for (String validation : validations.getValidationsById("templateid.1")) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}
		
		assertThat("Expected count", count, is(2));
	}
	
}
