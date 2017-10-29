package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.validate.IaSectionValidator;

public class IaSectionValidatorRoundTripTest {

	@Test
	public void testIaSectionValidatorIncorrectChildren() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionContainsWrongChild.xml");
		Converter converter = new Converter(new PathQrdaSource(path));

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		ErrorCode error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(error)
				.isEqualTo(ErrorCode.IA_SECTION_WRONG_CHILD);
	}

	@Test
	public void testIaSectionValidatorMissingMeasures() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingMeasures.xml");
		Converter converter = new Converter(new PathQrdaSource(path));

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		ErrorCode error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(error)
				.isEqualTo(ErrorCode.IA_SECTION_MISSING_IA_MEASURE);
	}

	@Test
	public void testIaSectionValidatorMissingReportingParameters() throws IOException {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingReportingParameter.xml");
		Converter converter = new Converter(new PathQrdaSource(path));

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
			Assert.fail("Should not reach");
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		ErrorCode error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(error)
				.isEqualTo(ErrorCode.IA_SECTION_MISSING_REPORTING_PARAM);
	}
}
