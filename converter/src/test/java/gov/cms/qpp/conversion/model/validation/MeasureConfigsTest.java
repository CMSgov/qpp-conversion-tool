package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.hamcrest.core.Is.isA;

public class MeasureConfigsTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@AfterClass
	public static void resetMeasureConfiguration() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	public void testGoodMeasureDataFile() {
		MeasureConfigs.setMeasureDataFile("reduced-test-measures-data.json");
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

	@Test
	public void privateConstructorTest() throws Exception {
		// reflection concept to get constructor of a Singleton class.
		Constructor<MeasureConfigs> constructor = MeasureConfigs.class.getDeclaredConstructor();
		// change the accessibility of constructor for outside a class object creation.
		constructor.setAccessible(true);
		// creates object of a class as constructor is accessible now.
		MeasureConfigs measureConfigs = constructor.newInstance();
		// close the accessibility of a constructor.
		constructor.setAccessible(false);

		assertWithMessage("Expect to have an instance here")
				.that(measureConfigs).isInstanceOf(MeasureConfigs.class);
	}

	@Test
	public void getMeasureConfigsTest() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
		List<MeasureConfig> configurations = MeasureConfigs.getMeasureConfigs();
		assertWithMessage("Expect the configurations to be a not empty list")
				.that(configurations).isNotEmpty();
	}

	@Test
	public void requiredMeasuresForSectionTest() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
		List<String>requiredMeasures = MeasureConfigs.requiredMeasuresForSection("aci");
		List<String>notRequiredMeasures = MeasureConfigs.requiredMeasuresForSection("quality");

		assertWithMessage("Expect the requiredMeasures to be a not empty list")
				.that(requiredMeasures).isNotEmpty();
		assertWithMessage("Expect the notRequiredMeasures to be a empty list")
				.that(notRequiredMeasures).isEmpty();
	}
}