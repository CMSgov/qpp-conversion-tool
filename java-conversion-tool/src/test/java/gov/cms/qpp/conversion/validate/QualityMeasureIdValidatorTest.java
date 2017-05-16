package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.containsValidationErrorInAnyOrderIgnoringPath;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.validationErrorTextMatches;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.REQUIRED_CHILD_MEASURE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class QualityMeasureIdValidatorTest {
	private QualityMeasureIdValidator objectUnderTest = new QualityMeasureIdValidator();

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
		assertThat("Incorrect validation error.", validationErrors.get(0),
			validationErrorTextMatches(QualityMeasureIdValidator.MEASURE_GUID_MISSING));
	}

	@Test
	public void validateMissingMeasure() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode(true, false);

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must be only one validation error.", validationErrors, hasSize(1));
		assertThat("Incorrect validation error.", validationErrors.get(0),
			validationErrorTextMatches(QualityMeasureIdValidator.NO_CHILD_MEASURE));
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
	public void testInternalValidateSameTemplateIdNodes() {
		List<ValidationError> validationErrors = objectUnderTest.validateSameTemplateIdNodes(
			Arrays.asList(createMeasureReferenceResultsNode(), createMeasureReferenceResultsNode()));

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalExistingDenexcepMeasure() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-52fc-3a32-0153-3d64af97147b")
				.addChildMeasure("DENEXCEP", "3C100EC4-2990-4D79-AE14-E816F5E78AC8")
				.addChildMeasure("IPOP", "D412322D-11F1-4573-893E-E6A05855DE10")
				.addChildMeasure("DENOM", "375D0559-C749-4BB9-9267-81EDF447650B")
				//.addChildMeasure("DENEX", "")
				.addChildMeasure("NUMER", "EFFE261C-0D57-423E-992C-7141B132768C")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalIPPMeasure() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-52fc-3a32-0153-3d64af97147b")
				.addChildMeasure("DENEXCEP", "3C100EC4-2990-4D79-AE14-E816F5E78AC8")
				.addChildMeasure("IPP", "D412322D-11F1-4573-893E-E6A05855DE10")
				.addChildMeasure("DENOM", "375D0559-C749-4BB9-9267-81EDF447650B")
				//.addChildMeasure("DENEX", "")
				.addChildMeasure("NUMER", "EFFE261C-0D57-423E-992C-7141B132768C")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalMissingDenexcepMeasure() {
		String message = String.format(REQUIRED_CHILD_MEASURE, "denominator exception");
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-52fc-3a32-0153-3d64af97147b")
				.addChildMeasure("IPOP", "D412322D-11F1-4573-893E-E6A05855DE10")
				.addChildMeasure("NUMER", "EFFE261C-0D57-423E-992C-7141B132768C")
				.addChildMeasure("DENOM", "375D0559-C749-4BB9-9267-81EDF447650B")
				.addChildMeasure("DENEX", "3C100EC4-2990-4D79-AE14-E816F5E78AC8")
				//.addChildMeasure("DENEXCEP", "3C100EC4-2990-4D79-AE14-E816F5E78AC8")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("Incorrect validation error.", validationErrors.get(0),
				validationErrorTextMatches(message));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulations() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-51f0-825b-0152-2273af5a150b")
				.addChildMeasure("IPOP", "E681DBF8-F827-4586-B3E0-178FF19EC3A2")
				.addChildMeasure("DENOM", "04BF53CE-6993-4EA2-BFE5-66E36172B388")
				//.addChildMeasure("DENEX", "")
				.addChildMeasure("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3")
				.addChildMeasure("NUMER", "631C0B49-83F4-4A54-96C4-7E0766B2407C")

				.addChildMeasure("IPOP", "AAC578DB-1900-43BD-BBBF-50014A5457E5")
				.addChildMeasure("DENOM", "1574973E-EB52-40C7-9709-25ABEDBA99A3")
				//.addChildMeasure("DENEX", "")
				.addChildMeasure("DENEXCEP", "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6")
				.addChildMeasure("NUMER", "5B7AC4EC-547A-47E5-AC5E-618401175511")

				.addChildMeasure("IPOP", "AF36C4A9-8BD9-4E21-838D-A47A1845EB90")
				.addChildMeasure("DENOM", "B95BC0D3-572E-462B-BAA2-46CD33A865CD")
				//.addChildMeasure("DENEX", "")
				//.addChildMeasure("DENEXCEP", "")
				.addChildMeasure("NUMER", "86F74F07-D593-44F6-AA12-405966400963")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsInvalidMeasureId() {
		String message = String.format(REQUIRED_CHILD_MEASURE, "denominator exception");
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-51f0-825b-0152-2273af5a150b")
				.addChildMeasure("IPOP", "E681DBF8-F827-4586-B3E0-178FF19EC3A2")
				.addChildMeasure("DENOM", "04BF53CE-6993-4EA2-BFE5-66E36172B388")
				//.addChildMeasure("DENEX", "")
				.addChildMeasure("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3__")
				.addChildMeasure("NUMER", "631C0B49-83F4-4A54-96C4-7E0766B2407C")

				.addChildMeasure("IPOP", "AAC578DB-1900-43BD-BBBF-50014A5457E5")
				.addChildMeasure("DENOM", "1574973E-EB52-40C7-9709-25ABEDBA99A3")
				//.addChildMeasure("DENEX", "")
				.addChildMeasure("DENEXCEP", "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6")
				.addChildMeasure("NUMER", "5B7AC4EC-547A-47E5-AC5E-618401175511")

				.addChildMeasure("IPOP", "AF36C4A9-8BD9-4E21-838D-A47A1845EB90")
				.addChildMeasure("DENOM", "B95BC0D3-572E-462B-BAA2-46CD33A865CD")
				//.addChildMeasure("DENEX", "")
				//.addChildMeasure("DENEXCEP", "")
				.addChildMeasure("NUMER", "86F74F07-D593-44F6-AA12-405966400963")

				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("Incorrect validation error.", validationErrors.get(0),
				validationErrorTextMatches(message));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsMissingMeasureId() {
		String message = String.format(REQUIRED_CHILD_MEASURE, "denominator exception");
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-51f0-825b-0152-2273af5a150b")
				.addChildMeasure("IPOP", "E681DBF8-F827-4586-B3E0-178FF19EC3A2")
				.addChildMeasure("DENOM", "04BF53CE-6993-4EA2-BFE5-66E36172B388")
				//.addChildMeasure("DENEX", "")
				.addChildMeasure("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3")
				.addChildMeasure("NUMER", "631C0B49-83F4-4A54-96C4-7E0766B2407C")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("Incorrect validation error.", validationErrors.get(0),
				validationErrorTextMatches(message));
	}

	private Node createMeasureReferenceResultsNode() {
		return createMeasureReferenceResultsNode(true, true);
	}

	private Node createMeasureReferenceResultsNode(boolean addMeasureGuid, boolean addChildMeasure) {
		MeasureReferenceBuilder builder = new MeasureReferenceBuilder();

		if (addMeasureGuid) {
			builder.addMeasureId("asdf-1234-jkl-7890");
		}

		if (addChildMeasure) {
			builder.addChildMeasure("", "");
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

		MeasureReferenceBuilder addChildMeasure(String type, String populationId) {
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