package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

public class CpcQualityMeasureIdValidatorTest {
	private CpcQualityMeasureIdValidator validator;
	private Node testNode;

	@Before
	public void setUp() {
		validator = new CpcQualityMeasureIdValidator();

		testNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		testNode.putValue(QualityMeasureIdValidator.MEASURE_ID,"40280381-51f0-825b-0152-22a112d2172a");
	}

	@Test
	public void testPerformanceCountWithNoErrors() {
		addAnyNumberOfChildren(2);
		validator.internalValidateSingleNode(testNode);

		assertThat("Must not contain errors", validator.getDetails(), empty());
	}

	@Test
	public void testPerformanceCountWithIncreasedSizeError() {
		addAnyNumberOfChildren(3);
		validator.internalValidateSingleNode(testNode);

		assertThat("Must not contain errors", validator.getDetails(),
				hasValidationErrorsIgnoringPath(
						String.format(CpcQualityMeasureIdValidator.INVALID_PERFORMANCE_RATE_COUNT, 2)));
	}

	@Test
	public void testPerformanceCountWithDecreasedSizeError() {
		addAnyNumberOfChildren(1);
		validator.internalValidateSingleNode(testNode);

		assertThat("Must not contain errors", validator.getDetails(),
				hasValidationErrorsIgnoringPath(
						String.format(CpcQualityMeasureIdValidator.INVALID_PERFORMANCE_RATE_COUNT, 2)));
	}

	private void addAnyNumberOfChildren(int size) {
		for (int count = 0 ; count < size; count++) {
			Node childNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
			testNode.addChildNode(childNode);
		}
	}
}
