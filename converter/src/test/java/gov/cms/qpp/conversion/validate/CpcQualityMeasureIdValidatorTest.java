package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class CpcQualityMeasureIdValidatorTest {

	private CpcQualityMeasureIdValidator validator;
	private Node testNode;

	@BeforeEach
	void setUp() {
		validator = new CpcQualityMeasureIdValidator();

		testNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		testNode.putValue(CpcQualityMeasureIdValidator.MEASURE_ID,"40280381-51f0-825b-0152-22a112d2172a");
	}

	@Test
	void testPerformanceCountWithNoErrors() {
		addAnyNumberOfChildren(2);
		validator.internalValidateSingleNode(testNode);

		assertWithMessage("Must contain 0 invalid performance rate count errors")
				.that(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.doesNotContain(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT.format(2));
	}

	@Test
	void testPerformanceCountWithIncreasedSizeError() {
		addAnyNumberOfChildren(3);
		validator.internalValidateSingleNode(testNode);

		assertWithMessage("Must contain 2 invalid performance rate count errors")
				.that(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT.format(2));
	}

	@Test
	void testPerformanceCountWithDecreasedSizeError() {
		addAnyNumberOfChildren(1);
		validator.internalValidateSingleNode(testNode);

		assertWithMessage("Must contain 2 invalid performance rate count errors")
				.that(validator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT.format(2));
	}

	private void addAnyNumberOfChildren(int size) {
		for (int count = 0 ; count < size; count++) {
			Node childNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
			testNode.addChildNode(childNode);
		}
	}
}
