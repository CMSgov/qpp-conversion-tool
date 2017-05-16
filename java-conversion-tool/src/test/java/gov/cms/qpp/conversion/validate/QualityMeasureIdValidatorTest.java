package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalMissingDenexcepMeasure() {
		String message = String.format(REQUIRED_CHILD_MEASURE, "denominator exception");
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-52fc-3a32-0153-3d64af97147b")
				.addChildMeasure("MEEP", "MAWP")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("Incorrect validation error.", validationErrors.get(0),
				validationErrorTextMatches(message));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulations() {
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-51f0-825b-0152-2273af5a150b")
				.addChildMeasure("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3")
				.addChildMeasure("DENEXCEP", "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6")
				.build();

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	@Test
	public void testInternalDenexcepMultipleSupPopulationsInvalidMeasureId() {
		String message = String.format(REQUIRED_CHILD_MEASURE, "denominator exception");
		Node measureReferenceResultsNode = new MeasureReferenceBuilder()
				.addMeasureId("40280381-51f0-825b-0152-2273af5a150b")
				.addChildMeasure("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3")
				.addChildMeasure("DENEXCEP", "MEEP")
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
				.addChildMeasure("DENEXCEP", "58347456-D1F3-4BBB-9B35-5D42825A0AB3")
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
			measureReferenceResultsNode.putValue("measureId", measureId);
			return this;
		}

		MeasureReferenceBuilder addChildMeasure(String type, String measureId) {
			Node measureNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
			measureNode.putValue("type", type);
			measureNode.putValue("measureId", measureId);
			measureReferenceResultsNode.addChildNode(measureNode);
			return this;
		}

		Node build() {
			return measureReferenceResultsNode;
		}
	}
}