package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.validate.IaSectionValidator;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IaSectionValidatorRoundTripTest {

	@Test
	public void testIaSectionValidatorIncorrectChildren() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionContainsWrongChild.xml");
		Converter converter = new Converter(path);

		AllErrors errors = new AllErrors();
		try {
			JsonWrapper qpp = converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		String error = errors.getErrors().get(0).getDetails().get(0).getMessage();

		assertThat("Must contain correct error message", error,
				is(IaSectionValidator.WRONG_CHILD_ERROR));
	}

	@Test
	public void testIaSectionValidatorMissingMeasures() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingMeasures.xml");
		Converter converter = new Converter(path);

		AllErrors errors = new AllErrors();
		try {
			JsonWrapper qpp = converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		String error = errors.getErrors().get(0).getDetails().get(0).getMessage();

		assertThat("Must contain correct error message", error,
				is(IaSectionValidator.MINIMUM_REQUIREMENT_ERROR));
	}

	@Test
	public void testIaSectionValidatorMissingReportingParameters() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingReportingParameter.xml");
		Converter converter = new Converter(path);

		AllErrors errors = new AllErrors();
		try {
			JsonWrapper qpp = converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		String error = errors.getErrors().get(0).getDetails().get(0).getMessage();

		assertThat("Must contain correct error message", error,
				is(IaSectionValidator.REPORTING_PARAM_REQUIREMENT_ERROR));
	}
}
