package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.MEASURE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class QualityMeasureIdValidatorTest {

	public static final String ONE_HUNDRED = "100";
	private final String IPOP = "IPOP";
	private final String IPP = "IPP";
	private final String DENOM = "DENOM";
	private final String NUMER = "NUMER";
	private final String DENEX = "DENEX";
	private final String DENEXCEP = "DENEXCEP";

	private final String REQUIRES_DENOM_EXCLUSION_GUID = "requiresDenominatorExclusionGuid";
	private final String REQUIRES_DENOM_EXCLUSION_IPOP_GUID = "3AD33404-E734-4F67-9144-E4B63CB3F4BE";
	private final String REQUIRES_DENOM_EXCLUSION_DENOM_GUID = "E62FEBA3-0F98-460D-93CD-44314D7203A8";
	private final String REQUIRES_DENOM_EXCLUSION_NUMER_GUID = "F9FEBF42-4B21-47A9-B03E-D2DA5CF8492B";
	private final String REQUIRES_DENOM_EXCLUSION_DENEX_GUID = "55A6D5F3-2029-4896-B850-4C7894161D7D";

	private final String REQUIRES_DENOM_EXCEPTION_GUID = "requiresDenominatorExceptionGuid";
	private final String REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID = "3C100EC4-2990-4D79-AE14-E816F5E78AC8";
	private final String REQUIRES_DENOM_EXCEPTION_IPOP_GUID = "D412322D-11F1-4573-893E-E6A05855DE10";
	private final String REQUIRES_DENOM_EXCEPTION_DENOM_GUID = "375D0559-C749-4BB9-9267-81EDF447650B";
	private final String REQUIRES_DENOM_EXCEPTION_NUMER_GUID = "EFFE261C-0D57-423E-992C-7141B132768C";

	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID = "multiplePopulationDenominatorExceptionGuid";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP1_GUID = "E681DBF8-F827-4586-B3E0-178FF19EC3A2";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM1_GUID = "04BF53CE-6993-4EA2-BFE5-66E36172B388";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID = "58347456-D1F3-4BBB-9B35-5D42825A0AB3";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID = "631C0B49-83F4-4A54-96C4-7E0766B2407C";

	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP2_GUID = "AAC578DB-1900-43BD-BBBF-50014A5457E5";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM2_GUID = "1574973E-EB52-40C7-9709-25ABEDBA99A3";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXECEP2_GUID = "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID = "5B7AC4EC-547A-47E5-AC5E-618401175511";

	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP3_GUID = "AF36C4A9-8BD9-4E21-838D-A47A1845EB90";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM3_GUID = "B95BC0D3-572E-462B-BAA2-46CD33A865CD";
	private final String MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID = "86F74F07-D593-44F6-AA12-405966400963";


	private QualityMeasureIdValidator objectUnderTest = new QualityMeasureIdValidator();

	@BeforeClass
	public static void setupCustomMeasuresData() {
		MeasureConfigs.setMeasureDataFile("reduced-test-measures-data.json");
	}

	@AfterClass
	public static void resetMeasuresData() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	public void validateHappyPath() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", details, empty());
	}

	@Test
	public void validateMissingMeasureId() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, true);

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only one validation error.", details, hasSize(1));
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.MEASURE_GUID_MISSING));
	}

	@Test
	public void validateMissingMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(true, false);

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only one validation error.", details, hasSize(1));
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.NO_CHILD_MEASURE));
	}

	@Test
	public void validateMissingMeasureIdAndMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, false);

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only two validation errors.", details, hasSize(2));
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.MEASURE_GUID_MISSING,
				QualityMeasureIdValidator.NO_CHILD_MEASURE));
	}

	@Test
	public void testDenominatorExclusionExists() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCLUSION_GUID).build();
		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be zero validation errors.", details, empty());
	}

	@Test
	public void testInvalidMeasureId() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("InvalidMeasureId")
				.addSubPopulationMeasureDataWithCounts(IPOP, REQUIRES_DENOM_EXCLUSION_IPOP_GUID, ONE_HUNDRED)
				.addSubPopulationMeasureDataWithCounts(DENOM, REQUIRES_DENOM_EXCLUSION_DENOM_GUID, ONE_HUNDRED)
				.addSubPopulationMeasureDataWithCounts(NUMER, REQUIRES_DENOM_EXCLUSION_NUMER_GUID, ONE_HUNDRED)
				.addSubPopulationMeasureDataWithCounts(DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID, ONE_HUNDRED)
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be one validation errors.", details, hasSize(1));
	}

	@Test
	public void testDenominatorExclusionMissing() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCLUSION_GUID)
			.removeSubPopulationMeasureData(DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(
				String.format(QualityMeasureIdValidator.INCORRECT_POPULATION_CRITERIA_COUNT,
						"CMS165v5", 1, "DENEX", 0)));
	}

	@Test
	public void testInternalExistingDenexcepMeasure() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID).build();
		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, empty());
	}

	@Test
	public void testInternalIPPMeasure() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
			.replaceSubPopulationMeasureData(IPOP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID, IPP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, empty());
	}

	@Test
	public void testInternalMissingDenexcepMeasure() {
		String message = String.format(QualityMeasureIdValidator.INCORRECT_POPULATION_CRITERIA_COUNT, "CMS68v6", 1, "DENEXCEP", 0);

		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
			.removeSubPopulationMeasureData(DENEXCEP, REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(message));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulations() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID).build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, empty());
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsInvalidMeasureId() {
		String message = String.format(QualityMeasureIdValidator.INCORRECT_UUID, "CMS52v5", "DENEXCEP", MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID);

		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.replaceSubPopulationMeasureData(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID, DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID+"INVALID")
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(message));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsMissingMeasureId() {
		String message = String.format(QualityMeasureIdValidator.INCORRECT_POPULATION_CRITERIA_COUNT, "CMS52v5", 2, "DENEXCEP", 1);

		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.removeSubPopulationMeasureData(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(message));
	}

	@Test
	public void testTooManyCriteriaExists() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureDataWithCounts(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID, ONE_HUNDRED)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		String expectedErrorMessage = String.format(QualityMeasureIdValidator.INCORRECT_POPULATION_CRITERIA_COUNT, "CMS52v5", 3, NUMER, 4);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(expectedErrorMessage));
	}

	@Test
	public void testTooFewCriteriaExists() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.removeSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		String expectedErrorMessage = String.format(QualityMeasureIdValidator.INCORRECT_POPULATION_CRITERIA_COUNT, "CMS52v5", 3, NUMER, 2);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(expectedErrorMessage));
	}

	@Test
	public void testIncorrectUuid() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.replaceSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID, NUMER, "incorrectUUID")
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		String expectedErrorMessage = String.format(QualityMeasureIdValidator.INCORRECT_UUID, "CMS52v5", NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(expectedErrorMessage));
	}

	@Test
	public void testInternalDenomCountLessThanIpopCount() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
				.removeSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID)
				.addSubPopulationMeasureDataWithCounts(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID, "50")
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, hasSize(0));
	}

	@Test
	public void testInternalDenomCountEqualToIpopCount() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, hasSize(0));
	}

	@Test
	public void testInternalDenomCountGreaterThanIpopCount() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
				.removeSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID)
				.addSubPopulationMeasureDataWithCounts(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID, "101")
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Must contain the correct error message.", details,
				hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.REQUIRE_VALID_DENOMINATOR_COUNT));
	}

	private MeasureReferenceBuilder createCorrectMeasureReference(String measureId) {
		MeasureReferenceBuilder measureReferenceResultsNode = new MeasureReferenceBuilder();
		measureReferenceResultsNode.addMeasureId(measureId);

		if (MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID.equals(measureId)) {
			measureReferenceResultsNode.addSubPopulationMeasureDataWithCounts(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP1_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM1_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID)

					.addSubPopulationMeasureDataWithCounts(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP2_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM2_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXECEP2_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID)

					.addSubPopulationMeasureDataWithCounts(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP3_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM3_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID);
		} else if (REQUIRES_DENOM_EXCEPTION_GUID.equals(measureId)) {
			measureReferenceResultsNode.addSubPopulationMeasureDataWithCounts(DENEXCEP, REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(IPOP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(NUMER, REQUIRES_DENOM_EXCEPTION_NUMER_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(REQUIRES_DENOM_EXCEPTION_NUMER_GUID);
		} else if (REQUIRES_DENOM_EXCLUSION_GUID.equals(measureId)) {
			measureReferenceResultsNode.addSubPopulationMeasureDataWithCounts(IPOP, REQUIRES_DENOM_EXCLUSION_IPOP_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENOM, REQUIRES_DENOM_EXCLUSION_DENOM_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(NUMER, REQUIRES_DENOM_EXCLUSION_NUMER_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(REQUIRES_DENOM_EXCLUSION_NUMER_GUID);

		}

		return measureReferenceResultsNode;
	}

	private Node createMeasureReferenceResultsNode() {
		return createMeasureReferenceResultsNode(true, true);
	}

	private Node createMeasureReferenceResultsNode(boolean addMeasureGuid, boolean addChildMeasure) {
		MeasureReferenceBuilder builder = new MeasureReferenceBuilder();
		if (addMeasureGuid) {
			builder.addMeasureId("requiresNothingGuid");
		}
		if (addChildMeasure) {
			builder.addSubPopulationMeasureDataWithCounts("", "", ONE_HUNDRED);
		}
		return builder.build();
	}

	private static class MeasureReferenceBuilder {
		Node measureReferenceResultsNode;

		MeasureReferenceBuilder() {
			measureReferenceResultsNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		}

		MeasureReferenceBuilder addMeasureId(String measureId) {
			measureReferenceResultsNode.putValue(MEASURE_ID, measureId);
			return this;
		}

		MeasureReferenceBuilder addSubPopulationPerformanceRate(String populationId) {
			Node measureNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
			measureNode.putValue(PERFORMANCE_RATE_ID, populationId);

			measureReferenceResultsNode.addChildNode(measureNode);

			return this;
		}

		MeasureReferenceBuilder addSubPopulationMeasureDataWithCounts(String type, String populationId, String count) {
			Node measureNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
			measureNode.putValue(MEASURE_TYPE, type);
			measureNode.putValue(MEASURE_POPULATION, populationId);

			Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
			aggregateCountNode.putValue(AggregateCountDecoder.AGGREGATE_COUNT, count);

			measureNode.addChildNode(aggregateCountNode);

			measureReferenceResultsNode.addChildNode(measureNode);

			return this;
		}

		MeasureReferenceBuilder removeSubPopulationMeasureData(String type, String populationId) {
			Node measureNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
			measureNode.putValue(MEASURE_TYPE, type);
			measureNode.putValue(MEASURE_POPULATION, populationId);

			Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
			aggregateCountNode.putValue(AggregateCountDecoder.AGGREGATE_COUNT, ONE_HUNDRED);

			measureNode.addChildNode(aggregateCountNode);

			measureReferenceResultsNode.removeChildNode(measureNode);

			return this;
		}

		MeasureReferenceBuilder replaceSubPopulationMeasureData(String oldType, String oldPopulationId, String newType, String newPopulationId) {
			removeSubPopulationMeasureData(oldType, oldPopulationId);
			addSubPopulationMeasureDataWithCounts(newType, newPopulationId, ONE_HUNDRED);

			return this;
		}

		Node build() {
			return measureReferenceResultsNode;
		}
	}
}
