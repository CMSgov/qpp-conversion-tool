package gov.cms.qpp.acceptance;

import gov.cms.qpp.acceptance.helper.MarkupManipulator;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.util.JsonHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.jayway.jsonpath.TypeRef;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class QualityMeasureIdMultiRoundTripTest {

	private static final String REQUIRE_ELIGIBLE_POPULATION_TOTAL = "Must have a required eligiblePopulation";
	private static final String REQUIRE_PERFORMANCE_MET = "Must have a required performanceMet";
	private static final String REQUIRE_ELIGIBLE_POPULATION_EXCLUSIONS = "Must have a required eligiblePopulationExclusion";
	private static final String ELIGIBLE_POPULATION = "eligiblePopulation";
	private static final String PERFORMANCE_MET = "performanceMet";
	private static final String ELIGIBLE_POPULATION_EXCLUSION = "eligiblePopulationExclusion";

	private static final Path JUNK_QRDA3_FILE =
			Paths.get("src/test/resources/fixtures/multiPerformanceRatePropMeasure.xml");

	private static final Path DENOM_GREATER_THAN_IPOP =
			Paths.get("src/test/resources/negative/mipsDenominatorInitialPopulationFailure.xml");

	private static MarkupManipulator manipulator;

	@BeforeAll
	static void setup() {
		manipulator = new MarkupManipulator.MarkupManipulatorBuilder()
			.setPathname(JUNK_QRDA3_FILE).build();
	}

	@AfterEach
	void teardown() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	void testRoundTripForQualityMeasureId() {
		Converter converter = new Converter(new PathSource(JUNK_QRDA3_FILE));

		JsonWrapper qpp = converter.transform();
		String json = qpp.toString();

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[?(@.category=='quality')].measurements[*]",
				new TypeRef<List<Map<String, ?>>>() { });

		List<Map<String, ?>> subPopulation = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[?(@.category=='quality')].measurements[?(@.measureId=='009')].value.strata[*]",
				new TypeRef<List<Map<String, ?>>>() { });

		String message =
				"The measureId in the quality measure should still populate given the junk stuff in the measure.";

		assertWithMessage(message)
				.that(qualityMeasures.get(0).get("measureId"))
				.isEqualTo("009");

		assertFirstSubPopulation(subPopulation);

		assertSecondSubPopulation(subPopulation);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithDuplicateIpopMeasureType() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[4]/observation/value/@code";

		List<Detail> details = executeScenario(path, false);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ProblemCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithDuplicateDenomMeasureType() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/value/@code";

		List<Detail> details = executeScenario(path, false);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ProblemCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithNoDenexMeasureType() {
		LocalizedProblem error =
			ProblemCode.POPULATION_CRITERIA_COUNT_INCORRECT.format("CMS128v9", 2, SubPopulationLabel.DENEX.name(), 1);
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

		assertThat(details)
				.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ProblemCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION);
	}

	@Test
	void testRoundTripForQualityMeasureIdWithNoDenomMeasurePopulation() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/reference/externalObservation/id";

		List<Detail> details = executeScenario(path, true);

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ProblemCode.QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION);
	}

	@Test
	void testRoundTripQualityMeasureIdWithDenomGreaterThanIpop() {
		MeasureConfigs.initMeasureConfigs(MeasureConfigs.TEST_MEASURE_DATA);
		Converter converter = new Converter(new PathSource(DENOM_GREATER_THAN_IPOP));
		List<Detail> details = new ArrayList<>();
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		assertThat(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ProblemCode.DENOMINATOR_COUNT_INVALID.format("D4D2DEE7-385A-4C28-A09C-884A062A97AA"));
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
				new InputStreamSupplierSource(JUNK_QRDA3_FILE.toString(), modified));
		List<Detail> details = new ArrayList<>();
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}
		return details;
	}

	private void assertFirstSubPopulation(List<Map<String, ?>> subPopulation) {
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_TOTAL)
				.that(subPopulation.get(0).get(ELIGIBLE_POPULATION))
				.isEqualTo(600);
		assertWithMessage(REQUIRE_PERFORMANCE_MET)
				.that(subPopulation.get(0).get(PERFORMANCE_MET))
				.isEqualTo(486);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCLUSIONS)
				.that(subPopulation.get(0).get(ELIGIBLE_POPULATION_EXCLUSION))
				.isEqualTo(35);
	}

	private void assertSecondSubPopulation(List<Map<String, ?>> subPopulation) {
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_TOTAL)
				.that(subPopulation.get(1)
				.get(ELIGIBLE_POPULATION))
				.isEqualTo(800);
		assertWithMessage(REQUIRE_PERFORMANCE_MET)
				.that(subPopulation.get(1).get(PERFORMANCE_MET))
				.isEqualTo(700);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCLUSIONS)
				.that(subPopulation.get(1).get(ELIGIBLE_POPULATION_EXCLUSION))
				.isEqualTo(40);
	}
}
