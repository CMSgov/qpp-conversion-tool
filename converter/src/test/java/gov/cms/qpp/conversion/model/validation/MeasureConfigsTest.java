package gov.cms.qpp.conversion.model.validation;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.List;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonMappingException;

class MeasureConfigsTest {

	@AfterAll
	static void resetMeasureConfiguration() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

//	@Test
//	void testGoodMeasureDataFile() {
//		MeasureConfigs.setMeasureDataFile("reduced-test-measures-data.json");
//		//no exception thrown
//	}
//
//	@Test
//	void testNonExistingMeasureDataFile() {
//		IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
//			MeasureConfigs.setMeasureDataFile("Bogus file name"));
//
//		assertThat(thrown).hasCauseThat().isInstanceOf(IOException.class);
//	}
//
//	@Test
//	void testBadFormattedMeasureDataFile() {
//		IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () ->
//			MeasureConfigs.setMeasureDataFile("bad_formatted_measures_data.json"));
//
//		assertThat(thrown).hasCauseThat().isInstanceOf(JsonMappingException.class);
//	}
//
//	@Test
//	void privateConstructorTest() throws Exception {
//		// reflection concept to get constructor of a Singleton class.
//		Constructor<MeasureConfigs> constructor = MeasureConfigs.class.getDeclaredConstructor();
//		// change the accessibility of constructor for outside a class object creation.
//		constructor.setAccessible(true);
//		// creates object of a class as constructor is accessible now.
//		MeasureConfigs measureConfigs = constructor.newInstance();
//		// close the accessibility of a constructor.
//		constructor.setAccessible(false);
//
//		assertWithMessage("Expect to have an instance here")
//				.that(measureConfigs).isInstanceOf(MeasureConfigs.class);
//	}
//
//	@Test
//	void getMeasureConfigsTest() {
//		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
//		List<MeasureConfig> configurations = MeasureConfigs.getMeasureConfigs();
//		assertWithMessage("Expect the configurations to be a not empty list")
//				.that(configurations).isNotEmpty();
//	}

//	@Test
//	void getMeasureConfigMisspell() {
//		String measureId = "IA_PCMG";
//		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
//		System.out.println(Instant.now());
//		List<String> blahBlah = MeasureConfigs.getSuggestions(measureId);
//		System.out.println(Instant.now());
//		LevenshteinDistance distanceCheck = new LevenshteinDistance(5);
//		System.out.println(blahBlah);
//		assertThat(blahBlah).contains("IA_PCMH");
//	}

//	@Test
//	void requiredMeasuresForSectionTest() {
//		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
//		List<String>requiredMeasures = MeasureConfigs.requiredMeasuresForSection("aci");
//		List<String>notRequiredMeasures = MeasureConfigs.requiredMeasuresForSection("quality");
//
//		assertWithMessage("Expect the requiredMeasures to be a not empty list")
//				.that(requiredMeasures).isNotEmpty();
//		assertWithMessage("Expect the notRequiredMeasures to be a empty list")
//				.that(notRequiredMeasures).isEmpty();
//	}
}