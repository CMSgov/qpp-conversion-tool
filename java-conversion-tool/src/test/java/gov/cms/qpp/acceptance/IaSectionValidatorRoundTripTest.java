package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import gov.cms.qpp.conversion.validate.IaSectionValidator;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IaSectionValidatorRoundTripTest {
	private static final String WRONG_CHILD_ERROR_FILE = "iaSectionContainsWrongChild.err.json";
	private static final String MISSING_MEASURES_ERROR_FILE = "iaSectionMissingMeasures.err.json";
	private static final String MISSING_REPORTING_PARAMS_ERROR_FILE = "iaSectionMissingReportingParameter.err.json";

	@After
	public void deleteJsonFile() throws IOException {
		Files.deleteIfExists(Paths.get(WRONG_CHILD_ERROR_FILE));
		Files.deleteIfExists(Paths.get(MISSING_MEASURES_ERROR_FILE));
		Files.deleteIfExists(Paths.get(MISSING_REPORTING_PARAMS_ERROR_FILE));
	}

	@Test
	public void testIaSectionValidatorIncorrectChildren() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionContainsWrongChild.xml");
		new ConversionFileWriterWrapper(path).transform();

		String error = JsonHelper.readJsonAtJsonPath(Paths.get(WRONG_CHILD_ERROR_FILE),
				"$.errors[0].details[0].message", String.class);

		assertThat("Must contain correct error message", error,
				is(IaSectionValidator.WRONG_CHILD_ERROR));
	}

	@Test
	public void testIaSectionValidatorMissingMeasures() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingMeasures.xml");
		new ConversionFileWriterWrapper(path).transform();

		String error = JsonHelper.readJsonAtJsonPath(Paths.get(MISSING_MEASURES_ERROR_FILE),
				"$.errors[0].details[0].message", String.class);

		assertThat("Must contain correct error message", error,
				is(IaSectionValidator.MINIMUM_REQUIREMENT_ERROR));
	}

	@Test
	public void testIaSectionValidatorMissingReportingParameters() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingReportingParameter.xml");
		new ConversionFileWriterWrapper(path).transform();

		String error = JsonHelper.readJsonAtJsonPath(Paths.get(MISSING_REPORTING_PARAMS_ERROR_FILE),
				"$.errors[0].details[0].message", String.class);

		assertThat("Must contain correct error message", error,
				is(IaSectionValidator.REPORTING_PARAM_REQUIREMENT_ERROR));
	}
}
