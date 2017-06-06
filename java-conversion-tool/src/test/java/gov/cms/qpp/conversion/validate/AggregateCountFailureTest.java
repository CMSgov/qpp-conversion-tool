package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.ConversionEntry;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class AggregateCountFailureTest {

	@Test
	public void testInvalidAggregateCounts() throws IOException {
		//set-up
		final String errorFileName = "angerTheConverter.err.json";
		File errorFile = new File(errorFileName);
		errorFile.delete();

		//execute
		ConversionEntry.main("src/test/resources/negative/angerTheConverter.xml");

		//assert
		assertThat("The error file must exist", errorFile.exists(), is(true));

		String errorContent = new String(Files.readAllBytes(Paths.get(errorFileName)));
		assertThat("The error file flags a aggregate count type error",
				errorContent,
				containsString(String.format(CommonNumeratorDenominatorValidator.NOT_AN_INTEGER_VALUE, "Numerator")));
		assertThat("The error file flags a aggregate count value error",
				errorContent,
				containsString(String.format(CommonNumeratorDenominatorValidator.INVALID_VALUE, "Denominator")));

		//clean-up
		errorFile.deleteOnExit();
	}

}
