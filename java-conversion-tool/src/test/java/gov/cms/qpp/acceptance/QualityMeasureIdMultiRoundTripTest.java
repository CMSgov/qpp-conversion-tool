package gov.cms.qpp.acceptance;

import gov.cms.qpp.conversion.ConversionFileWriterWrapper;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class QualityMeasureIdMultiRoundTripTest {
	private final String REQUIRE_POPULATION_TOTAL = "Must have a required populationTotal";
	private final String REQUIRE_PERFORMANCE_MET = "Must have a required performanceMet";
	private final String REQUIRE_INITIAL_POPULATION = "Must have a required initialPopulation";
	private final String INITIAL_POPULATION = "initialPopulation";
	private final String REQUIRE_DENOM_EXCEP = "Must have a required denominatorExceptions";
	private final String REQUIRE_DENOM = "Must have a required denominator";
	private final String REQUIRE_NUMER = "Must have a required numerator";
	private final String POPULATION_TOTAL = "populationTotal";
	private final String PERFORMANCE_MET = "performanceMet";
	private final String DENOMINATOR_EXCEPTIONS = "denominatorExceptions";
	private final String NUMERATOR = "numerator";
	private final String DENOMINATOR = "denominator";

	private static final Path JUNK_QRDA3_FILE =
			Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml");

	private final String SUCCESS_JSON = "multiPerformanceRatePropMeasure.qpp.json";

	@After
	public void deleteJsonFile() throws IOException {
		Files.deleteIfExists(Paths.get(SUCCESS_JSON));
	}

	@Test
	public void testRoundTripForQualityMeasureId() throws IOException {
		ConversionFileWriterWrapper converterWrapper = new ConversionFileWriterWrapper(JUNK_QRDA3_FILE);
		converterWrapper.transform();

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
				"$.measurementSets[?(@.category=='quality')].measurements[*]", List.class);

		List<Map<String, Integer>> subPopulation = JsonHelper.readJsonAtJsonPath(Paths.get(SUCCESS_JSON),
				"$.measurementSets[?(@.category=='quality')].measurements[?(@.measureId=='CMS52v5')].value.strata[*]", List.class);

		assertThat("There should still be a quality measure even with the junk stuff in quality measure.",
				qualityMeasures, hasSize(1));
		assertThat("The measureId in the quality measure should still populate given the junk stuff in the measure.",
				qualityMeasures.get(0).get("measureId"), is("CMS52v5"));

		assertFirstSubPopulation(subPopulation);

		assertSecondSubPopulation(subPopulation);

		assertThirdSubPopulation(subPopulation);
	}

	private void assertFirstSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertThat(REQUIRE_POPULATION_TOTAL, subPopulation.get(0).get(POPULATION_TOTAL), CoreMatchers.is(600));
		assertThat(REQUIRE_PERFORMANCE_MET, subPopulation.get(0).get(PERFORMANCE_MET), CoreMatchers.is(486));
		assertThat(REQUIRE_INITIAL_POPULATION, subPopulation.get(0).get(INITIAL_POPULATION), CoreMatchers.is(600));
		assertThat(REQUIRE_DENOM_EXCEP, subPopulation.get(0).get(DENOMINATOR_EXCEPTIONS), CoreMatchers.is(35));
		assertThat(REQUIRE_NUMER, subPopulation.get(0).get(NUMERATOR), CoreMatchers.is(486));
		assertThat(REQUIRE_DENOM, subPopulation.get(0).get(DENOMINATOR), CoreMatchers.is(600));
	}

	private void assertSecondSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertThat(REQUIRE_POPULATION_TOTAL, subPopulation.get(1).get(POPULATION_TOTAL), CoreMatchers.is(800));
		assertThat(REQUIRE_PERFORMANCE_MET, subPopulation.get(1).get(PERFORMANCE_MET), CoreMatchers.is(700));
		assertThat(REQUIRE_INITIAL_POPULATION, subPopulation.get(1).get(INITIAL_POPULATION), CoreMatchers.is(800));
		assertThat(REQUIRE_DENOM_EXCEP, subPopulation.get(1).get(DENOMINATOR_EXCEPTIONS), CoreMatchers.is(40));
		assertThat(REQUIRE_NUMER, subPopulation.get(1).get(NUMERATOR), CoreMatchers.is(700));
		assertThat(REQUIRE_DENOM, subPopulation.get(1).get(DENOMINATOR), CoreMatchers.is(800));
	}

	private void assertThirdSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertThat(REQUIRE_POPULATION_TOTAL, subPopulation.get(2).get(POPULATION_TOTAL), CoreMatchers.is(580));
		assertThat(REQUIRE_PERFORMANCE_MET, subPopulation.get(2).get(PERFORMANCE_MET), CoreMatchers.is(520));
		assertThat(REQUIRE_INITIAL_POPULATION, subPopulation.get(2).get(INITIAL_POPULATION), CoreMatchers.is(580));
		assertThat(REQUIRE_NUMER, subPopulation.get(2).get(NUMERATOR), CoreMatchers.is(520));
		assertThat(REQUIRE_DENOM, subPopulation.get(2).get(DENOMINATOR), CoreMatchers.is(580));
	}
}
