package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class QppXmlDecoderTest extends QppXmlDecoder {
	
	@Test
	public void validationFormatTest() throws Exception {
		QppXmlDecoder target = new QppXmlDecoder();
		target.addValidation("templateid.1", "validation.1");
		target.addValidation("templateid.1", "validation.2");
		target.addValidation("templateid.3", "validation.3");
		
		List<String> checkList = Arrays.asList("templateid.1 - validation.1",
												"templateid.1 - validation.2",
												"templateid.3 - validation.3");
		int count = 0;
		for (String validation : target.validations()) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}
		
		assertThat("Expected count", count, is(3));
		
		checkList = Arrays.asList("validation.1", "validation.2");
		count = 0;
		for (String validation : target.getValidationsById("templateid.1")) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}
		
		assertThat("Expected count", count, is(2));
	}
	
	@Test
	public void sillyCoverageTest() throws Exception {
		assertThat("Should be benign", new QppXmlDecoder().internalDecode(null, null), is(nullValue()));
	}
}
