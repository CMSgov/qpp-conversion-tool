package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;

import gov.cms.qpp.conversion.Context;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.TransformException;

class IaSectionValidatorRoundTripTest {

	@Test
	void testIaSectionValidatorIncorrectChildren() {
		Path path = Paths.get("src/test/resources/negative/iaSectionContainsWrongChild.xml");
		Context context = new Context();
		Converter converter = new Converter(new PathSource(path), context);

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		Integer error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(ErrorCode.getByCode(error))
				.isEqualTo(ErrorCode.IA_SECTION_WRONG_CHILD);
	}

	@Test
	void testIaSectionValidatorMissingMeasures() {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingMeasures.xml");
		Converter converter = new Converter(new PathSource(path));

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		Integer error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(ErrorCode.getByCode(error))
				.isEqualTo(ErrorCode.IA_SECTION_MISSING_IA_MEASURE);
	}

	@Test
	void testIaSectionValidatorMissingReportingParameters() {
		Path path = Paths.get("src/test/resources/negative/iaSectionMissingReportingParameter.xml");
		Converter converter = new Converter(new PathSource(path));

		AllErrors errors = new AllErrors();
		try {
			converter.transform();
			Assertions.fail("Should not reach");
		} catch (TransformException exception) {
			errors = exception.getDetails();
		}

		Integer error = errors.getErrors().get(0).getDetails().get(0).getErrorCode();

		assertThat(ErrorCode.getByCode(error))
				.isEqualTo(ErrorCode.IA_SECTION_MISSING_REPORTING_PARAM);
	}
}
