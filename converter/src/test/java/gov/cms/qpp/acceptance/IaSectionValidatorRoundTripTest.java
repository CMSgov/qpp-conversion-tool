package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;

import gov.cms.qpp.conversion.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.TransformException;

class IaSectionValidatorRoundTripTest {

	@Test
	void testIaSectionValidatorIncorrectChildren() {
		Path path = Path.of("src/test/resources/negative/iaSectionContainsWrongChild.xml");
		Context context = new Context();
		Converter converter = new Converter(new PathSource(path), context);

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		Integer error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(ProblemCode.getByCode(error))
				.isEqualTo(ProblemCode.IA_SECTION_WRONG_CHILD);
	}

	@Test
	void testIaSectionValidatorMissingMeasures() {
		Path path = Path.of("src/test/resources/negative/iaSectionMissingMeasures.xml");
		Converter converter = new Converter(new PathSource(path));

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		Integer error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(ProblemCode.getByCode(error))
				.isEqualTo(ProblemCode.IA_SECTION_MISSING_IA_MEASURE);
	}

	@Test
	void testIaSectionValidatorMissingReportingParameters() {
		Path path = Path.of("src/test/resources/negative/iaSectionMissingReportingParameter.xml");
		Converter converter = new Converter(new PathSource(path));

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
			Assertions.fail("Should not reach");
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		Integer error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(ProblemCode.getByCode(error))
				.isEqualTo(ProblemCode.IA_SECTION_MISSING_REPORTING_PARAM);
	}
}
