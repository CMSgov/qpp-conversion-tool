package gov.cms.qpp.acceptance;

import gov.cms.qpp.acceptance.helper.MarkupManipulator;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamQrdaSource;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.util.JsonHelper;
import gov.cms.qpp.conversion.validate.QualityMeasureIdValidator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class QualityMeasureIdMultiRoundTripTest {
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

	@BeforeClass
	public static void setup() throws ParserConfigurationException, SAXException, IOException {
		manipulator = new MarkupManipulator.MarkupManipulatorBuilder()
			.setPathname(JUNK_QRDA3_FILE).build();
	}

	@Test
	public void testRoundTripForQualityMeasureId() throws IOException {
		Converter converter = new Converter(new PathQrdaSource(JUNK_QRDA3_FILE));
		JsonWrapper qpp = converter.transform();
		String json = qpp.toString();

		List<Map<String, ?>> qualityMeasures = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[?(@.category=='quality')].measurements[*]", List.class);

		List<Map<String, Integer>> subPopulation = JsonHelper.readJsonAtJsonPath(json,
				"$.measurementSets[?(@.category=='quality')].measurements[?(@.measureId=='160')].value.strata[*]", List.class);

		assertThat("The measureId in the quality measure should still populate given the junk stuff in the measure.",
				qualityMeasures.get(0).get("measureId"), is("160"));

		assertFirstSubPopulation(subPopulation);

		assertSecondSubPopulation(subPopulation);

		assertThirdSubPopulation(subPopulation);
	}

	@Test
	public void testRoundTripForQualityMeasureIdWithDuplicateIpopMeasureType() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[4]/observation/value/@code";

		List<Detail> details = executeScenario(path, false);
		System.out.println(details.get(0).getMessage());

		Assert.assertThat("Should have no error detail", details,
				hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.SINGLE_MEASURE_TYPE));
	}

	@Test
	public void testRoundTripForQualityMeasureIdWithDuplicateDenomMeasureType() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/value/@code";

		List<Detail> details = executeScenario(path, false);

		Assert.assertThat("Should only have one error detail", details, hasSize(1));
		Assert.assertThat("Error should regard the need for a single measure type", details,
				hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.SINGLE_MEASURE_TYPE));
	}

	@Test
	public void testRoundTripForQualityMeasureIdWithNoDenomMeasureType() {
		String message = String.format(
				QualityMeasureIdValidator.REQUIRED_CHILD_MEASURE, "CMS52v5", QualityMeasureIdValidator.DENOM);
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/value/@code";

		List<Detail> details = executeScenario(path, true);

		Assert.assertThat("Should only have one error detail", details, hasSize(1));
		Assert.assertThat("Error should regard the need for a single measure type", details,
				hasValidationErrorsIgnoringPath(message));
	}

	@Test
	public void testRoundTripForQualityMeasureIdWithDuplicateDenomMeasurePopulation() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/reference/externalObservation/id";

		List<Detail> details = executeScenario(path, false);

		Assert.assertThat("Should only have one error detail", details, hasSize(1));
		Assert.assertThat("error should regard the need for a single measure population", details,
				hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.SINGLE_MEASURE_POPULATION));
	}

	@Test
	public void testRoundTripForQualityMeasureIdWithNoDenomMeasurePopulation() {
		String path = "/ClinicalDocument/component/structuredBody/component/section/entry/organizer/" +
				"component[5]/observation/reference/externalObservation/id";

		List<Detail> details = executeScenario(path, true);

		Assert.assertThat("Should only have two error details", details, hasSize(2));
		Assert.assertThat("Error should regard the need for a single measure population", details,
				hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.SINGLE_MEASURE_POPULATION));
	}

	@Test
	public void testRoundTripQualityMeasureIdWithDenomGreaterThanIpop() {
		Converter converter = new Converter(new PathQrdaSource(DENOM_GREATER_THAN_IPOP));
		List<Detail> details = new ArrayList<>();
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details.addAll(errors.getErrors().get(0).getDetails());
		}

		assertThat("Must contain the right number of errors", details, hasSize(3));
		assertThat("Must contain the correct error message", details,
				hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.REQUIRE_VALID_DENOMINATOR_COUNT));
	}

	private List<Detail>  executeScenario(String path, boolean remove) {
		InputStream modified = manipulator.upsetTheNorm(path, remove);
		Converter converter = new Converter(new InputStreamQrdaSource(JUNK_QRDA3_FILE.toString(), modified));
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
