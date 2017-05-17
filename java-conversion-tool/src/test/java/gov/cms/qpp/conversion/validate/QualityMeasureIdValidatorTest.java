package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.containsValidationErrorInAnyOrderIgnoringPath;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.REQUIRED_CHILD_MEASURE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class QualityMeasureIdValidatorTest {
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

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void validateMissingMeasureId() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, true);

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only one validation error.", validationErrors, hasSize(1));
		assertThat("Incorrect validation error.", validationErrors,
				containsValidationErrorInAnyOrderIgnoringPath(QualityMeasureIdValidator.MEASURE_GUID_MISSING));
	}

	@Test
	public void validateMissingMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(true, false);

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only one validation error.", validationErrors, hasSize(1));
		assertThat("Incorrect validation error.", validationErrors,
				containsValidationErrorInAnyOrderIgnoringPath(QualityMeasureIdValidator.NO_CHILD_MEASURE));
	}

	@Test
	public void validateMissingMeasureIdAndMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(false, false);

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only two validation errors.", validationErrors, hasSize(2));
		assertThat("Incorrect validation error.", validationErrors,
				containsValidationErrorInAnyOrderIgnoringPath(QualityMeasureIdValidator.MEASURE_GUID_MISSING,
						QualityMeasureIdValidator.NO_CHILD_MEASURE));
	}

	@Test
	public void testDenominatorExclusionExists() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("requiresDenominatorExclusionGuid")
				.addSubPopulationMeasureData("IPOP", "3AD33404-E734-4F67-9144-E4B63CB3F4BE")
				.addSubPopulationMeasureData("DENOM", "E62FEBA3-0F98-460D-93CD-44314D7203A8")
				.addSubPopulationMeasureData("NUMER", "F9FEBF42-4B21-47A9-B03E-D2DA5CF8492B")
				.addSubPopulationMeasureData("DENEX", "55A6D5F3-2029-4896-B850-4C7894161D7D")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be zero validation errors.", validationErrors, empty());
	}

	@Test
	public void testDenominatorExclusionMissing() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("requiresDenominatorExclusionGuid")
				.addSubPopulationMeasureData("DENEXCEP", "anything")
				.addSubPopulationMeasureData("IPOP", "3AD33404-E734-4F67-9144-E4B63CB3F4BE")
				.addSubPopulationMeasureData("DENOM", "E62FEBA3-0F98-460D-93CD-44314D7203A8")
				.addSubPopulationMeasureData("NUMER", "F9FEBF42-4B21-47A9-B03E-D2DA5CF8492B")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be a validation error.", validationErrors, hasSize(1));
		assertThat("Incorrect validation error.", validationErrors,
				containsValidationErrorInAnyOrderIgnoringPath(
						String.format(QualityMeasureIdValidator.REQUIRED_CHILD_MEASURE,
						QualityMeasureIdValidator.DENEX)));
	}

	@Test
	public void testInternalValidateSameTemplateIdNodes() {
		List<ValidationError> validationErrors = objectUnderTest.validateSameTemplateIdNodes(
				Arrays.asList(createMeasureReferenceResultsNode(), createMeasureReferenceResultsNode()));

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalExistingDenexcepMeasure() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()

				.addMeasureId("requiresDenominatorExceptionGuid")
				.addSubPopulationMeasureData("DENEXCEP", "3C100EC4-2990-4D79-AE14-E816F5E78AC8")
				.addSubPopulationMeasureData("IPOP", "D412322D-11F1-4573-893E-E6A05855DE10")
				.addSubPopulationMeasureData("DENOM", "375D0559-C749-4BB9-9267-81EDF447650B")
				.addSubPopulationMeasureData("NUMER", "EFFE261C-0D57-423E-992C-7141B132768C")
				.build();


		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalIPPMeasure() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("requiresDenominatorExceptionGuid")
				.addSubPopulationMeasureData("DENEXCEP", "3C100EC4-2990-4D79-AE14-E816F5E78AC8")
				.addSubPopulationMeasureData("IPP", "D412322D-11F1-4573-893E-E6A05855DE10")
				.addSubPopulationMeasureData("DENOM", "375D0559-C749-4BB9-9267-81EDF447650B")
				.addSubPopulationMeasureData("NUMER", "EFFE261C-0D57-423E-992C-7141B132768C")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalMissingDenexcepMeasure() {
		String message = String.format(REQUIRED_CHILD_MEASURE, QualityMeasureIdValidator.DENEXCEP);
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()

				.addMeasureId("requiresDenominatorExceptionGuid")
				.addSubPopulationMeasureData("IPOP", "D412322D-11F1-4573-893E-E6A05855DE10")
				.addSubPopulationMeasureData("NUMER", "EFFE261C-0D57-423E-992C-7141B132768C")
				.addSubPopulationMeasureData("DENOM", "375D0559-C749-4BB9-9267-81EDF447650B")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", validationErrors, containsValidationErrorInAnyOrderIgnoringPath(message));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulations() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()

				.addMeasureId("multiplePopulationDenominatorExceptionGuid")
				.addSubPopulationMeasureData("IPOP", "E681DBF8-F827-4586-B3E0-178FF19EC3A2")
				.addSubPopulationMeasureData("DENOM", "04BF53CE-6993-4EA2-BFE5-66E36172B388")
				.addSubPopulationMeasureData("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3")
				.addSubPopulationMeasureData("NUMER", "631C0B49-83F4-4A54-96C4-7E0766B2407C")

				.addSubPopulationMeasureData("IPOP", "AAC578DB-1900-43BD-BBBF-50014A5457E5")
				.addSubPopulationMeasureData("DENOM", "1574973E-EB52-40C7-9709-25ABEDBA99A3")
				.addSubPopulationMeasureData("DENEXCEP", "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6")
				.addSubPopulationMeasureData("NUMER", "5B7AC4EC-547A-47E5-AC5E-618401175511")

				.addSubPopulationMeasureData("IPOP", "AF36C4A9-8BD9-4E21-838D-A47A1845EB90")
				.addSubPopulationMeasureData("DENOM", "B95BC0D3-572E-462B-BAA2-46CD33A865CD")
				.addSubPopulationMeasureData("NUMER", "86F74F07-D593-44F6-AA12-405966400963")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsInvalidMeasureId() {
		String message = String.format(REQUIRED_CHILD_MEASURE, QualityMeasureIdValidator.DENEXCEP);
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("multiplePopulationDenominatorExceptionGuid")
				.addSubPopulationMeasureData("IPOP", "E681DBF8-F827-4586-B3E0-178FF19EC3A2")
				.addSubPopulationMeasureData("DENOM", "04BF53CE-6993-4EA2-BFE5-66E36172B388")
				.addSubPopulationMeasureData("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3__")
				.addSubPopulationMeasureData("NUMER", "631C0B49-83F4-4A54-96C4-7E0766B2407C")


				.addSubPopulationMeasureData("IPOP", "AAC578DB-1900-43BD-BBBF-50014A5457E5")
				.addSubPopulationMeasureData("DENOM", "1574973E-EB52-40C7-9709-25ABEDBA99A3")
				.addSubPopulationMeasureData("DENEXCEP", "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6")
				.addSubPopulationMeasureData("NUMER", "5B7AC4EC-547A-47E5-AC5E-618401175511")


				.addSubPopulationMeasureData("IPOP", "AF36C4A9-8BD9-4E21-838D-A47A1845EB90")
				.addSubPopulationMeasureData("DENOM", "B95BC0D3-572E-462B-BAA2-46CD33A865CD")
				.addSubPopulationMeasureData("NUMER", "86F74F07-D593-44F6-AA12-405966400963")

				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", validationErrors, containsValidationErrorInAnyOrderIgnoringPath(message));

	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsMissingMeasureId() {
		String message = String.format(REQUIRED_CHILD_MEASURE, QualityMeasureIdValidator.DENEXCEP);
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()

				.addMeasureId("requiresDenominatorExceptionGuid")
				.addSubPopulationMeasureData("IPOP", "D412322D-11F1-4573-893E-E6A05855DE10")
				.addSubPopulationMeasureData("DENOM", "375D0559-C749-4BB9-9267-81EDF447650B")
				.addSubPopulationMeasureData("NUMER", "EFFE261C-0D57-423E-992C-7141B132768C")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("Incorrect validation error.", validationErrors, containsValidationErrorInAnyOrderIgnoringPath(message));


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
			measureReferenceResultsNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId());
		}

		MeasureReferenceBuilder addMeasureId(String measureId) {
			measureReferenceResultsNode.putValue(MEASURE_TYPE, measureId);
			return this;
		}


		MeasureReferenceBuilder addSubPopulationMeasureData(String type, String populationId) {
			Node measureNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
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
