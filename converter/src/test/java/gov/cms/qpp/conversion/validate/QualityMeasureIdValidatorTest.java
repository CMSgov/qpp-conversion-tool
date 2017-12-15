package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.MEASURE_ID;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

class QualityMeasureIdValidatorTest {

	public static final String ONE_HUNDRED = "100";

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


	private QualityMeasureIdValidator objectUnderTest = new MipsQualityMeasureIdValidator();

	@BeforeAll
	static void setupCustomMeasuresData() {
		MeasureConfigs.setMeasureDataFile("reduced-test-measures-data.json");
	}

	@AfterAll
	static void resetMeasuresData() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@Test
	void validateHappyPath() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertWithMessage("There must not be any validation errors.")
				.that(details).isEmpty();
	}

	@Test
	void validateMissingMeasureId() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, true);

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.MEASURE_GUID_MISSING);
	}

	@Test
	void validateMissingMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(true, false);

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CHILD_MEASURE_MISSING);
	}

	@Test
	void validateMissingMeasureIdAndMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, false);

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.MEASURE_GUID_MISSING,
						ErrorCode.CHILD_MEASURE_MISSING);
	}

	@Test
	void testDenominatorExclusionExists() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCLUSION_GUID).build();
		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("There must be zero validation errors.")
				.that(details).isEmpty();
	}

	@Test
	void testInvalidMeasureId() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("InvalidMeasureId")
				.addSubPopulationMeasureDataWithCounts(SubPopulations.IPOP, REQUIRES_DENOM_EXCLUSION_IPOP_GUID, ONE_HUNDRED)
				.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, REQUIRES_DENOM_EXCLUSION_DENOM_GUID, ONE_HUNDRED)
				.addSubPopulationMeasureDataWithCounts(SubPopulations.NUMER, REQUIRES_DENOM_EXCLUSION_NUMER_GUID, ONE_HUNDRED)
				.addSubPopulationMeasureDataWithCounts(SubPopulations.DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID, ONE_HUNDRED)
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertWithMessage("There must be one validation errors.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.MEASURE_GUID_MISSING);
	}

	@Test
	void testDenominatorExclusionMissing() {
		LocalizedError incorrectCount = ErrorCode.POPULATION_CRITERIA_COUNT_INCORRECT.format(
				"CMS165v5", 1, SubPopulations.DENEX, 0);
		LocalizedError incorrectUuid = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(
				"CMS165v5", SubPopulations.DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID);

		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCLUSION_GUID)
			.removeSubPopulationMeasureData(SubPopulations.DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(incorrectCount, incorrectUuid);
	}

	@Test
	void testInternalExistingDenexcepMeasure() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID).build();
		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("There must not be any validation errors.")
				.that(details).isEmpty();
	}

	@Test
	void testInternalIPPMeasure() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
			.replaceSubPopulationMeasureData(SubPopulations.IPOP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID, SubPopulations.IPP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("There must not be any validation errors.")
				.that(details).isEmpty();
	}

	@Test
	void testInternalMissingDenexcepMeasure() {
		LocalizedError countMessage = ErrorCode.POPULATION_CRITERIA_COUNT_INCORRECT.format("CMS68v6", 1, SubPopulations.DENEXCEP, 0);
		LocalizedError uuidMessage = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format(
				"CMS68v6", SubPopulations.DENEXCEP, REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID);

		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
			.removeSubPopulationMeasureData(SubPopulations.DENEXCEP, REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(countMessage, uuidMessage);
	}

	@Test
	void testInternalDenexcepMultipleSupPopulations() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID).build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertWithMessage("There must not be any validation errors.")
				.that(details).isEmpty();
	}

	@Test
	void testInternalDenexcepMultipleSupPopulationsInvalidMeasureId() {
		LocalizedError message = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format("CMS52v5", SubPopulations.DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID);

		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.replaceSubPopulationMeasureData(
					SubPopulations.DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID,
					SubPopulations.DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID+"INVALID")
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(message);
	}

	@Test
	void testInternalDenexcepMultipleSupPopulationsMissingMeasureId() {
		LocalizedError countMessage = ErrorCode.POPULATION_CRITERIA_COUNT_INCORRECT.format("CMS52v5", 2, SubPopulations.DENEXCEP, 1);
		LocalizedError uuidMessage = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format("CMS52v5", SubPopulations.DENEXCEP,
				MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID);

		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.removeSubPopulationMeasureData(SubPopulations.DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(countMessage, uuidMessage);
	}

	@Test
	void testTooManyCriteriaExists() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureDataWithCounts(SubPopulations.NUMER,
					MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID, ONE_HUNDRED)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		LocalizedError expectedErrorMessage = ErrorCode.POPULATION_CRITERIA_COUNT_INCORRECT.format("CMS52v5", 3, SubPopulations.NUMER, 4);
		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(expectedErrorMessage);
	}

	@Test
	void testTooFewCriteriaExists() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.removeSubPopulationMeasureData(SubPopulations.NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID)
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		LocalizedError expectedErrorMessage = ErrorCode.POPULATION_CRITERIA_COUNT_INCORRECT.format("CMS52v5", 3, SubPopulations.NUMER, 2);
		LocalizedError expectedUuidErrorMessage = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format("CMS52v5", SubPopulations.NUMER,
				MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID);

		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(expectedErrorMessage, expectedUuidErrorMessage);
	}

	@Test
	void testIncorrectUuid() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.replaceSubPopulationMeasureData(SubPopulations.NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID,
					SubPopulations.NUMER, "incorrectUUID")
			.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		LocalizedError expectedErrorMessage = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format("CMS52v5",
				SubPopulations.NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID);

		assertWithMessage("Incorrect validation error.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(expectedErrorMessage);
	}

	@Test
	void testInternalDenomCountLessThanIpopCount() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
				.removeSubPopulationMeasureData(SubPopulations.DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID)
				.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID, "50")
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("There must not be any validation errors.")
				.that(details).isEmpty();
	}

	@Test
	void testInternalDenomCountEqualToIpopCount() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("There must not be any validation errors.")
				.that(details).isEmpty();
	}

	@Test
	void testInternalDenomCountGreaterThanIpopCount() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
				.removeSubPopulationMeasureData(SubPopulations.DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID)
				.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID, "101")
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertWithMessage("Must contain the correct error message.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.DENOMINATOR_COUNT_INVALID);
	}

	@Test
	void testPerformanceRateUuidFail() {
		Node measureReferenceResultsNode = createCorrectMeasureReference(REQUIRES_DENOM_EXCEPTION_GUID)
				.replaceSubPopulationPerformanceRate(REQUIRES_DENOM_EXCEPTION_NUMER_GUID, "fail")
				.build();

		Set<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		LocalizedError expectedErrorMessage = ErrorCode.QUALITY_MEASURE_ID_INCORRECT_UUID.format("CMS68v6",
				PERFORMANCE_RATE_ID, "fail");
		assertWithMessage("Must contain the correct error message.")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(expectedErrorMessage);
	}

	private MeasureReferenceBuilder createCorrectMeasureReference(String measureId) {
		MeasureReferenceBuilder measureReferenceResultsNode = new MeasureReferenceBuilder();
		measureReferenceResultsNode.addMeasureId(measureId);

		if (MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID.equals(measureId)) {
			measureReferenceResultsNode.addSubPopulationMeasureDataWithCounts(SubPopulations.IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP1_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM1_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID)

					.addSubPopulationMeasureDataWithCounts(SubPopulations.IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP2_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM2_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXECEP2_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID)

					.addSubPopulationMeasureDataWithCounts(SubPopulations.IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP3_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM3_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID);
		} else if (REQUIRES_DENOM_EXCEPTION_GUID.equals(measureId)) {
			measureReferenceResultsNode.addSubPopulationMeasureDataWithCounts(SubPopulations.DENEXCEP, REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.IPOP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.NUMER, REQUIRES_DENOM_EXCEPTION_NUMER_GUID, ONE_HUNDRED)
					.addSubPopulationPerformanceRate(REQUIRES_DENOM_EXCEPTION_NUMER_GUID);
		} else if (REQUIRES_DENOM_EXCLUSION_GUID.equals(measureId)) {
			measureReferenceResultsNode.addSubPopulationMeasureDataWithCounts(SubPopulations.IPOP, REQUIRES_DENOM_EXCLUSION_IPOP_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENOM, REQUIRES_DENOM_EXCLUSION_DENOM_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.NUMER, REQUIRES_DENOM_EXCLUSION_NUMER_GUID, ONE_HUNDRED)
					.addSubPopulationMeasureDataWithCounts(SubPopulations.DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID, ONE_HUNDRED)
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

		MeasureReferenceBuilder addSubPopulationPerformanceRate(String id) {
			Node performanceRateNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
			performanceRateNode.putValue(PERFORMANCE_RATE_ID, id);

			measureReferenceResultsNode.addChildNode(performanceRateNode);

			return this;
		}

		MeasureReferenceBuilder removeSubPopulationPerformanceRate(String id) {
			Node performanceRateNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
			performanceRateNode.putValue(PERFORMANCE_RATE_ID, id);

			measureReferenceResultsNode.removeChildNode(performanceRateNode);

			return this;
		}

		MeasureReferenceBuilder replaceSubPopulationPerformanceRate(String oldId, String newId) {
			removeSubPopulationPerformanceRate(oldId);
			addSubPopulationPerformanceRate(newId);

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
