package gov.cms.qpp.acceptance;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import gov.cms.qpp.acceptance.helper.MarkupManipulator;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierQrdaSource;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import gov.cms.qpp.conversion.util.JsonHelper;

class QualityMeasureIdMultiRoundTripTest {

	private static final String REQUIRE_ELIGIBLE_POPULATION_TOTAL = "Must have a required eligiblePopulation";
	private static final String REQUIRE_PERFORMANCE_MET = "Must have a required performanceMet";
	private static final String REQUIRE_ELIGIBLE_POPULATION_EXCEPTIONS = "Must have a required eligiblePopulationException";
	private static final String ELIGIBLE_POPULATION = "eligiblePopulation";
	private static final String PERFORMANCE_MET = "performanceMet";
	private static final String ELIGIBLE_POPULATION_EXCEPTION = "eligiblePopulationException";

	private static final Path JUNK_QRDA3_FILE =
			Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml");

	private static final Path DENOM_GREATER_THAN_IPOP =
			Paths.get("src/test/resources/negative/mipsDenominatorInitialPopulationFailure.xml");

	private static MarkupManipulator manipulator;

	@BeforeAll
	static void setup() throws ParserConfigurationException, SAXException, IOException {
		manipulator = new MarkupManipulator.MarkupManipulatorBuilder()
			.setPathname(JUNK_QRDA3_FILE).build();
	}

	@Test
	void testRoundTripForQualityMeasureId() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform();
		String json = qpp.toString();

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[?(@.category=='quality')].measurements[*]", List.class);

		List<Map<String, Integer>> subPopulation = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[?(@.category=='quality')].measurements[?(@.measureId=='160')].value.strata[*]", List.class);

		String message =
				"The measureId in the quality measure should still populate given the junk stuff in the measure.";

		assertWithMessage(message)
				.that(qualityMeasures.get(0).get("measureId"))
				.isEqualTo("160");

		assertFirstSubPopulation(subPopulation);

		assertSecondSubPopulation(subPopulation);

		assertThirdSubPopulation(subPopulation);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithDuplicateIpopMeasureType() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[4]/observation/value/@code";

		List<Detail> details = executeScenario(path, false);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithDuplicateDenomMeasureType() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/value/@code";

		List<Detail> details = executeScenario(path, false);

		assertThat(details).hasSize(1);
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithNoDenomMeasureType() {
		LocalizedError error = ErrorCode.POPULATION_CRITERIA_COUNT_INCORRECT.format("CMS52v5", 3, SubPopulations.DENOM, 2);
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/value/@code";

		List<Detail> details = executeScenario(path, true);

		assertThat(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(error);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithDuplicateDenomMeasurePopulation() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/reference/externalObservation/id";

		List<Detail> details = executeScenario(path, false);

		assertThat(details).hasSize(1);
		assertThat(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithNoDenomMeasurePopulation() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/reference/externalObservation/id";

		List<Detail> details = executeScenario(path, true);

		assertThat(details).hasSize(2);
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION);
	}

	@Test
	void testRoundTripQualityMeasureIdWithDenomGreaterThanIpop() {
		Converter converter = new Converter(new PathQrdaSource(DENOM_GREATER_THAN_IPOP));
		List<Detail> details = new ArrayList<>();
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		assertThat(details).hasSize(3);
		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.DENOMINATOR_COUNT_INVALID);
	}

	@Test
	void testRoundTripQualityMeasureMissingOnePerformanceRateSuccess() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[1]";
		List<Detail> expectedOutput = executeScenario(path, true);
		assertThat(expectedOutput).isEmpty();
	}

	private List<Detail> executeScenario(String path, boolean remove) {
		InputStream modified = manipulator.upsetTheNorm(path, remove);
		Converter converter = new Converter(
				new InputStreamSupplierQrdaSource(JUNK_QRDA3_FILE.toString(), () -> modified));
		List<Detail> details = new ArrayList<>();
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}
		return details;
	}

	private void assertFirstSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_TOTAL)
				.that(subPopulation.get(0).get(ELIGIBLE_POPULATION))
				.isEqualTo(600);
		assertWithMessage(REQUIRE_PERFORMANCE_MET)
				.that(subPopulation.get(0).get(PERFORMANCE_MET))
				.isEqualTo(486);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCEPTIONS)
				.that(subPopulation.get(0).get(ELIGIBLE_POPULATION_EXCEPTION))
				.isEqualTo(35);
	}

	private void assertSecondSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_TOTAL)
				.that(subPopulation.get(1)
				.get(ELIGIBLE_POPULATION))
				.isEqualTo(800);
		assertWithMessage(REQUIRE_PERFORMANCE_MET)
				.that(subPopulation.get(1).get(PERFORMANCE_MET))
				.isEqualTo(700);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCEPTIONS)
				.that(subPopulation.get(1).get(ELIGIBLE_POPULATION_EXCEPTION))
				.isEqualTo(40);
	}

	private void assertThirdSubPopulation(List<Map<String, Integer>> subPopulation) {
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_TOTAL)
				.that(subPopulation.get(2).get(ELIGIBLE_POPULATION))
				.isEqualTo(580);
		assertWithMessage(REQUIRE_PERFORMANCE_MET)
				.that(subPopulation.get(2).get(PERFORMANCE_MET))
				.isEqualTo(520);
	}
}
