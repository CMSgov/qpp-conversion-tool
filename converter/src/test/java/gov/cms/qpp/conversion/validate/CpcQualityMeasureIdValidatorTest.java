package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.util.MeasureConfigHelper;

class CpcQualityMeasureIdValidatorTest {
	private static final String MEASURE_ID = "40280382-5abd-fa46-015b-49909a0e3822";
	private static final String E_MEASURE_ID = "CMS128v6";

	private CpcQualityMeasureIdValidator validator;
	private Node testNode;

	@BeforeEach
	void setUp() {
		validator = new CpcQualityMeasureIdValidator();

		testNode = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		testNode.putValue(MeasureConfigHelper.MEASURE_ID,MEASURE_ID);
	}

	@Test
	void testPerformanceCountWithNoErrors() {
		addAnyNumberOfChildren(2);
		List<Detail> details = validator.validateSingleNode(testNode).getErrors();

		assertWithMessage("Must contain 0 invalid performance rate count errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.doesNotContain(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT
					.format(2, E_MEASURE_ID));
	}

	@Test
	void testPerformanceCountWithIncreasedSizeError() {
		addAnyNumberOfChildren(3);
		List<Detail> details = validator.validateSingleNode(testNode).getErrors();

		assertWithMessage("Must contain 2 invalid performance rate count errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT
					.format(2, E_MEASURE_ID));
	}

	@Test
	void testPerformanceCountWithDecreasedSizeError() {
		addAnyNumberOfChildren(1);
		List<Detail> details = validator.validateSingleNode(testNode).getErrors();

		assertWithMessage("Must contain 2 invalid performance rate count errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ErrorCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT
					.format(2, E_MEASURE_ID));
	}

	private void addAnyNumberOfChildren(int size) {
		for (int count = 0 ; count < size; count++) {
			Node childNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
			testNode.addChildNode(childNode);
		}
	}
}
