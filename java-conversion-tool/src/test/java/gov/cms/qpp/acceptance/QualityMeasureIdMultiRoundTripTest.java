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
	private final String REQUIRE_ELIGIBLE_POPULATION_TOTAL = "Must have a required eligiblePopulation";
	private final String REQUIRE_PERFORMANCE_MET = "Must have a required performanceMet";
	private final String REQUIRE_ELIGIBLE_POPULATION_EXCEPTIONS = "Must have a required eligiblePopulationException";
	private final String ELIGIBLE_POPULATION = "eligiblePopulation";
	private final String PERFORMANCE_MET = "performanceMet";
	private final String ELIGIBLE_POPULATION_EXCEPTION = "eligiblePopulationException";

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
		assertThat(REQUIRE_ELIGIBLE_POPULATION_TOTAL, subPopulation.get(0).get(ELIGIBLE_POPULATION), CoreMatchers.is(600));
		assertThat(REQUIRE_PERFORMANCE_MET, subPopulation.get(0).get(PERFORMANCE_MET), CoreMatchers.is(486));
		assertThat(REQUIRE_ELIGIBLE_POPULATION_EXCEPTIONS, subPopulation.get(0).get(ELIGIBLE_POPULATION_EXCEPTION), CoreMatchers.is(35));
	}

	private void assertSecondSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertThat(REQUIRE_ELIGIBLE_POPULATION_TOTAL, subPopulation.get(1).get(ELIGIBLE_POPULATION), CoreMatchers.is(800));
		assertThat(REQUIRE_PERFORMANCE_MET, subPopulation.get(1).get(PERFORMANCE_MET), CoreMatchers.is(700));
		assertThat(REQUIRE_ELIGIBLE_POPULATION_EXCEPTIONS, subPopulation.get(1).get(ELIGIBLE_POPULATION_EXCEPTION), CoreMatchers.is(40));
	}

	private void assertThirdSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertThat(REQUIRE_ELIGIBLE_POPULATION_TOTAL, subPopulation.get(2).get(ELIGIBLE_POPULATION), CoreMatchers.is(580));
		assertThat(REQUIRE_PERFORMANCE_MET, subPopulation.get(2).get(PERFORMANCE_MET), CoreMatchers.is(520));
	}
}
