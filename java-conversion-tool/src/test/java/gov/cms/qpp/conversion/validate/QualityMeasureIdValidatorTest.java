package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.MEASURE_ID;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.REQUIRED_CHILD_MEASURE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class QualityMeasureIdValidatorTest {

	final String IPOP = "IPOP";
	final String IPP = "IPP";
	final String DENOM = "DENOM";
	final String NUMER = "NUMER";
	final String DENEX = "DENEX";
	final String DENEXCEP = "DENEXCEP";

	final String REQUIRES_DENOM_EXCLUSION_GUID = "requiresDenominatorExclusionGuid";
	final String REQUIRES_DENOM_EXCLUSION_IPOP_GUID = "3AD33404-E734-4F67-9144-E4B63CB3F4BE";
	final String REQUIRES_DENOM_EXCLUSION_DENOM_GUID = "E62FEBA3-0F98-460D-93CD-44314D7203A8";
	final String REQUIRES_DENOM_EXCLUSION_NUMER_GUID = "F9FEBF42-4B21-47A9-B03E-D2DA5CF8492B";
	final String REQUIRES_DENOM_EXCLUSION_DENEX_GUID = "55A6D5F3-2029-4896-B850-4C7894161D7D";

	final String REQUIRES_DENOM_EXCEPTION_GUID = "requiresDenominatorExceptionGuid";
	final String REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID = "3C100EC4-2990-4D79-AE14-E816F5E78AC8";
	final String REQUIRES_DENOM_EXCEPTION_IPOP_GUID = "D412322D-11F1-4573-893E-E6A05855DE10";
	final String REQUIRES_DENOM_EXCEPTION_DENOM_GUID = "375D0559-C749-4BB9-9267-81EDF447650B";
	final String REQUIRES_DENOM_EXCEPTION_NUMER_GUID = "EFFE261C-0D57-423E-992C-7141B132768C";

	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID = "multiplePopulationDenominatorExceptionGuid";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP1_GUID = "E681DBF8-F827-4586-B3E0-178FF19EC3A2";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM1_GUID = "04BF53CE-6993-4EA2-BFE5-66E36172B388";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID = "58347456-D1F3-4BBB-9B35-5D42825A0AB3";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID = "631C0B49-83F4-4A54-96C4-7E0766B2407C";

	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP2_GUID = "AAC578DB-1900-43BD-BBBF-50014A5457E5";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM2_GUID = "1574973E-EB52-40C7-9709-25ABEDBA99A3";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXECEP2_GUID = "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID = "5B7AC4EC-547A-47E5-AC5E-618401175511";

	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP3_GUID = "AF36C4A9-8BD9-4E21-838D-A47A1845EB90";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM3_GUID = "B95BC0D3-572E-462B-BAA2-46CD33A865CD";
	final String MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID = "86F74F07-D593-44F6-AA12-405966400963";


	private QualityMeasureIdValidator objectUnderTest = new QualityMeasureIdValidator();

	@BeforeClass
	public static void setupCustomMeasuresData() {
		MeasureConfigs.setMeasureDataFile("reduced-test-measures-data.json");
	}

	@AfterClass
	public static void resetMeasuresData() {
		MeasureConfigs.setMeasureDataFile("measures-data-short.json");
	}

	@Test
	public void validateHappyPath() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", details, hasSize(0));
	}

	@Test
	public void validateMissingMeasureId() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, true);

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only one validation error.", details, hasSize(1));
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.MEASURE_GUID_MISSING));
	}

	@Test
	public void validateMissingMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(true, false);

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only one validation error.", details, hasSize(1));
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.NO_CHILD_MEASURE));
	}

	@Test
	public void validateMissingMeasureIdAndMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, false);

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only two validation errors.", details, hasSize(2));
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(QualityMeasureIdValidator.MEASURE_GUID_MISSING,
				QualityMeasureIdValidator.NO_CHILD_MEASURE));
	}

	@Test
	public void testDenominatorExclusionExists() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(REQUIRES_DENOM_EXCLUSION_GUID)
			.addSubPopulationMeasureData(IPOP, REQUIRES_DENOM_EXCLUSION_IPOP_GUID)
			.addSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCLUSION_DENOM_GUID)
			.addSubPopulationMeasureData(NUMER, REQUIRES_DENOM_EXCLUSION_NUMER_GUID)
			.addSubPopulationMeasureData(DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be zero validation errors.", details, empty());
	}

	@Test
	public void testInvalidMeasureId() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("InvalidMeasureId")
				.addSubPopulationMeasureData(IPOP, REQUIRES_DENOM_EXCLUSION_IPOP_GUID)
				.addSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCLUSION_DENOM_GUID)
				.addSubPopulationMeasureData(NUMER, REQUIRES_DENOM_EXCLUSION_NUMER_GUID)
				.addSubPopulationMeasureData(DENEX, REQUIRES_DENOM_EXCLUSION_DENEX_GUID)
				.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be one validation errors.", details, hasSize(1));
	}

	@Test
	public void testDenominatorExclusionMissing() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(REQUIRES_DENOM_EXCLUSION_GUID)
			.addSubPopulationMeasureData(DENEXCEP, "anything")
			.addSubPopulationMeasureData(IPOP, REQUIRES_DENOM_EXCLUSION_IPOP_GUID)
			.addSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCLUSION_DENOM_GUID)
			.addSubPopulationMeasureData(NUMER, REQUIRES_DENOM_EXCLUSION_NUMER_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be a validation error.", details, hasSize(1));
		assertThat("Incorrect validation error.", details,
			hasValidationErrorsIgnoringPath(
				String.format(QualityMeasureIdValidator.REQUIRED_CHILD_MEASURE,
				QualityMeasureIdValidator.DENEX)));
	}

	@Test
	public void testInternalExistingDenexcepMeasure() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(REQUIRES_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureData(DENEXCEP, REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID)
			.addSubPopulationMeasureData(IPOP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID)
			.addSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID)
			.addSubPopulationMeasureData(NUMER, REQUIRES_DENOM_EXCEPTION_NUMER_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, hasSize(0));
	}

	@Test
	public void testInternalIPPMeasure() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(REQUIRES_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureData(DENEXCEP, REQUIRES_DENOM_EXCEPTION_DENEXCEP_GUID)
			.addSubPopulationMeasureData(IPP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID)
			.addSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID)
			.addSubPopulationMeasureData(NUMER, REQUIRES_DENOM_EXCEPTION_NUMER_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, hasSize(0));
	}

	@Test
	public void testInternalMissingDenexcepMeasure() {
		String message = String.format(REQUIRED_CHILD_MEASURE, QualityMeasureIdValidator.DENEXCEP);
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(REQUIRES_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureData(IPOP, REQUIRES_DENOM_EXCEPTION_IPOP_GUID)
			.addSubPopulationMeasureData(NUMER, REQUIRES_DENOM_EXCEPTION_NUMER_GUID)
			.addSubPopulationMeasureData(DENOM, REQUIRES_DENOM_EXCEPTION_DENOM_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(message));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulations() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP1_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM1_GUID)
			.addSubPopulationMeasureData(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID)

			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP2_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM2_GUID)
			.addSubPopulationMeasureData(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXECEP2_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID)

			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP3_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM3_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", details, hasSize(0));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsInvalidMeasureId() {
		String message = String.format(REQUIRED_CHILD_MEASURE, QualityMeasureIdValidator.DENEXCEP);
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP1_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM1_GUID)
			.addSubPopulationMeasureData(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXCEP1_GUID+"INVALID")
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID)

			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP2_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM2_GUID)
			.addSubPopulationMeasureData(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXECEP2_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID)

			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP3_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM3_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(message));

	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsMissingMeasureId() {
		String message = String.format(REQUIRED_CHILD_MEASURE, QualityMeasureIdValidator.DENEXCEP);
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
			.addMeasureId(MULTIPLE_POPULATION_DENOM_EXCEPTION_GUID)
			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP1_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM1_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER1_GUID)

			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP2_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM2_GUID)
			.addSubPopulationMeasureData(DENEXCEP, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENEXECEP2_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER2_GUID)

			.addSubPopulationMeasureData(IPOP, MULTIPLE_POPULATION_DENOM_EXCEPTION_IPOP3_GUID)
			.addSubPopulationMeasureData(DENOM, MULTIPLE_POPULATION_DENOM_EXCEPTION_DENOM3_GUID)
			.addSubPopulationMeasureData(NUMER, MULTIPLE_POPULATION_DENOM_EXCEPTION_NUMER3_GUID)
			.build();

		List<Detail> details = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", details, hasValidationErrorsIgnoringPath(message));


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
			builder.addSubPopulationMeasureData("", "");
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


		MeasureReferenceBuilder addSubPopulationMeasureData(String type, String populationId) {
			Node measureNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
			measureNode.putValue(MEASURE_TYPE, type);
			measureNode.putValue(MEASURE_POPULATION, populationId);

			measureReferenceResultsNode.addChildNode(measureNode);
			return this;
		}

		Node build() {
			return measureReferenceResultsNode;
		}
	}
}
