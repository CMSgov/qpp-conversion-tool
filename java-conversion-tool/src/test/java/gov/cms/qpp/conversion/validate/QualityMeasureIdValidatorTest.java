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

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.containsValidationErrorInAnyOrderIgnoringPath;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.validationErrorTextMatches;
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
	public void testDenominatorExclusionExists() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode("requiresDenominatorExclusionGuid", "DENEX");

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be zero validation errors.", validationErrors, empty());
	}

	@Test
	public void testDenominatorExclusionMissing() {
		Node measureReferenceResultsNode = createMeasureReferenceResultsNode("requiresDenominatorExclusionGuid", "DENEXCEP");

		List<ValidationError> validationErrors = objectUnderTest.validateSingleNode(measureReferenceResultsNode);
		assertThat("There must be a validation error.", validationErrors, hasSize(1));
		assertThat("Incorrect validation error.", validationErrors,
			containsValidationErrorInAnyOrderIgnoringPath(String.format(QualityMeasureIdValidator.REQUIRED_CHILD_MEASURE, QualityMeasureIdValidator.DENEX)));
	}

	@Test
	public void testInternalValidateSameTemplateIdNodes() {
		List<ValidationError> validationErrors = objectUnderTest.validateSameTemplateIdNodes(
			Arrays.asList(createMeasureReferenceResultsNode(), createMeasureReferenceResultsNode()));

		assertThat("There must not be any validation errors.", validationErrors, hasSize(0));
	}

	private Node createMeasureReferenceResultsNode() {
		return createMeasureReferenceResultsNode(true, true);
	}

	private Node createMeasureReferenceResultsNode(boolean addMeasureGuid, boolean addChildMeasure) {
		Node measureReferenceResultsNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId());

		if (addMeasureGuid) {
			measureReferenceResultsNode.putValue("measureId", "requiresNothingGuid");
		}

		if (addChildMeasure) {
			Node measureNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
			measureReferenceResultsNode.addChildNode(measureNode);
		}

		return measureReferenceResultsNode;
	}

	private Node createMeasureReferenceResultsNode(String measureGuid, String... childrenSubPopulations) {
		Node measureReferenceResultsNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId());

		measureReferenceResultsNode.putValue("measureId", measureGuid);

		for (String subPopulation : childrenSubPopulations) {
			Node measureNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
			measureNode.putValue("type", subPopulation);
			measureReferenceResultsNode.addChildNode(measureNode);
		}

		return measureReferenceResultsNode;
	}
}