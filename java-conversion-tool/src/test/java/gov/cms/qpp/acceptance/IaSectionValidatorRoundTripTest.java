package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
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
	public void testIaSectionValidatorIncorrectChildren() throws IOException {
		Path path = Paths.get("src/test/resources/negative/incorrectIaSectionChildren.xml");
		new Converter(path).transform();

		String error = JsonHelper.readJsonAtJsonPath(Paths.get(ERROR_FILE),
				"$.errorSources[0].validationErrors[0].errorText", String.class);

		assertThat("Must contain correct error message", error, is("Must have only IA Measures"));
	}
}
