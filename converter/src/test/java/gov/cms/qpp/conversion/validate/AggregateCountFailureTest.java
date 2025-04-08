package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.TransformException;

class AggregateCountFailureTest {

	@Test
	void testInvalidAggregateCounts() throws IOException {
		//execute
		Converter converter = new Converter(new PathSource(Path.of("src/test/resources/negative/angerTheConverter.xml")));

		String errorContent = "";
		try {
			converter.transform();
			Assertions.fail("A transformation exception must have been thrown!");
		} catch(TransformException exception) {
			AllErrors errors = exception.getDetails();
			ObjectWriter jsonObjectWriter = new ObjectMapper()
				.setSerializationInclusion(JsonInclude.Include.NON_NULL)
				.writer()
				.withDefaultPrettyPrinter();
			errorContent = jsonObjectWriter.writeValueAsString(errors);
		}

		//assert
		assertWithMessage("The error file flags a aggregate count type error")
				.that(errorContent)
				.contains(ProblemCode.NUMERATOR_DENOMINATOR_MUST_BE_INTEGER.format("Numerator", "Bob").getMessage());

		assertWithMessage("The error file flags a aggregate count value error")
				.that(errorContent)
				.contains(ProblemCode.NUMERATOR_DENOMINATOR_INVALID_VALUE.format("Denominator", "empty").getMessage());
	}

}
