package gov.cms.qpp.negative;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.validate.AggregateCountValidator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

public class AggregateCountFailureTest {

	@Test
	public void testInvalidAggregateCounts() throws IOException {
		//set-up
		final String errorFileName = "QRDA-III-invalid-aggregate-count.err.json";
		File errorFile = new File(errorFileName);
		errorFile.delete();

		//execute
		Converter.main("src/test/resources/negative/QRDA-III-invalid-aggregate-count.xml");

		//assert
		assertThat("The error file must exist", errorFile.exists(), is(true));

		String errorContent = new String(Files.readAllBytes(Paths.get(errorFileName)));
		assertThat("The error file flags a aggregate count type error",
				errorContent,
				containsString( AggregateCountValidator.TYPE_ERROR ));
		assertThat("The error file flags a aggregate count value error",
				errorContent,
				containsString( AggregateCountValidator.VALUE_ERROR ));

		//clean-up
		errorFile.deleteOnExit();
	}

}
