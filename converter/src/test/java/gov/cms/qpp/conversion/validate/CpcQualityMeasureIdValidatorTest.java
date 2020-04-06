package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.util.MeasureConfigHelper;

class CpcQualityMeasureIdValidatorTest {
	private static final String MEASURE_ID = "40280382-6258-7581-0162-92901c56156e";
	private static final String E_MEASURE_ID = "CMS128v7";

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
				.doesNotContain(ProblemCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT
					.format(2, E_MEASURE_ID));
	}

	@Test
	void testPerformanceCountWithIncreasedSizeError() {
		addAnyNumberOfChildren(3);
		List<Detail> details = validator.validateSingleNode(testNode).getErrors();

		assertWithMessage("Must contain 2 invalid performance rate count errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ProblemCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT
					.format(2, E_MEASURE_ID));
	}

	@Test
	void testPerformanceCountWithDecreasedSizeError() {
		addAnyNumberOfChildren(1);
		List<Detail> details = validator.validateSingleNode(testNode).getErrors();

		assertWithMessage("Must contain 2 invalid performance rate count errors")
				.that(details).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.contains(ProblemCode.CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT
					.format(2, E_MEASURE_ID));
	}

	@Test
	void testIgnoreMissingAggCount() {
		addMeasureDataWithoutAggCount();
		List<Detail> details = validator.validateSingleNode(testNode).getErrors();

		assertThat(details)
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.doesNotContain(ProblemCode.CPC_PLUS_PERFORMANCE_DENOM_LESS_THAN_ZERO);
	}

	@Test
	void testIgnoreInvalidAggCount() {
		addMeasureDataWithInvalidAggCount();
		List<Detail> details = validator.validateSingleNode(testNode).getErrors();

		assertThat(details)
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.doesNotContain(ProblemCode.CPC_PLUS_PERFORMANCE_DENOM_LESS_THAN_ZERO);
	}

	private void addAnyNumberOfChildren(int size) {
		for (int count = 0 ; count < size; count++) {
			Node childNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
			testNode.addChildNode(childNode);
		}
	}

	private void addMeasureDataWithoutAggCount() {
		Node childNode = createMeasureDataNode();
		testNode.addChildNode(childNode);
	}

	private void addMeasureDataWithInvalidAggCount() {
		Node childNode = createMeasureDataNode();
		Node aggCount = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggCount.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "ab1234");

		testNode.addChildNode(childNode);
	}

	private Node createMeasureDataNode() {
		Node childNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		childNode.putValue("type", "DENOM");
		return childNode;
	}
}
