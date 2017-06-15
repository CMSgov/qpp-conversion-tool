package gov.cms.qpp.conversion.model.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.JsonMappingException;

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
		assertThat("Expect to have an instance here ", measureConfigs, instanceOf(MeasureConfigs.class));
	}

	@Test
	public void getMeasureConfigsTest() {
		List<MeasureConfig> configurations = MeasureConfigs.getMeasureConfigs();
		assertThat("Expect the configurations to be a not empty list", configurations, is(not(empty())));
	}

	@Test
	public void requiredMeasuresForSectionTest() {
		List<String>requiredMeasures = MeasureConfigs.requiredMeasuresForSection("aci");
		List<String>notRequiredMeasures = MeasureConfigs.requiredMeasuresForSection("quality");
		assertThat("Expect the requiredMeasures to be a not empty list", requiredMeasures, is(not(empty())));
		assertThat("Expect the notRequiredMeasures to be a empty list", notRequiredMeasures, is(empty()));
	}
}