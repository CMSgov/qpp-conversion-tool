package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.hamcrest.core.Is.isA;

public class MeasureConfigsTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@AfterClass
	public static void resetMeasureConfiguration() {
		MeasureConfigs.setMeasureDataFile("measures-data-short.json");
	}

	@Test
	public void testGoodMeasureDataFile() {

		MeasureConfigs.setMeasureDataFile("measures-data-short.json");
		//no exception thrown
	}

	@Test
	public void testNonExistingMeasureDataFile() {

		//set-up
		thrown.expect(IllegalArgumentException.class);
		thrown.expectCause(isA(IOException.class));

		//execute
		MeasureConfigs.setMeasureDataFile("Bogus file name");
	}

	@Test
	public void testBadFormattedMeasureDataFile() {

		//set-up
		thrown.expect(IllegalArgumentException.class);
		thrown.expectCause(isA(JsonMappingException.class));

		//execute
		MeasureConfigs.setMeasureDataFile("bad_formatted_measures_data.json");
	}
}