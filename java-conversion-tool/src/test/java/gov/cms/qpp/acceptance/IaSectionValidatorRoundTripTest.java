package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IaSectionValidatorRoundTripTest {
	private static final String ERROR_FILE = "incorrectIaSectionChildren.err.json";

	@After
	public void deleteJsonFile() throws IOException {
		Files.deleteIfExists(Paths.get(ERROR_FILE));
	}

	@Test
	public void testIaSectionValidatorIncorrectChildren() throws Exception {
		Path path = Paths.get("src/test/resources/negative/incorrectIaSectionChildren.xml");
		new ConversionFileWriterWrapper(path).transform().call();

		String error = JsonHelper.readJsonAtJsonPath(Paths.get(ERROR_FILE),
				"$.errors[0].details[0].message", String.class);

		assertThat("Must contain correct error message", error, is("Must have only IA Measures"));
	}
}
