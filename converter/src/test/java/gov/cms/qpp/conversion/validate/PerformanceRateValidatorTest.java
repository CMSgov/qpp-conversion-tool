package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

public class PerformanceRateValidatorTest {
	private PerformanceRateValidator performanceRateValidator;
	private Node node;

	@Before
	public  void setup() {
		performanceRateValidator = new PerformanceRateValidator();
		node = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "0");
	}

	@Test
	public void testZeroValue() {
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).isEmpty();
	}

	@Test
	public void testOneValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "1");
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).isEmpty();
	}

	@Test
	public void testNAValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE, "NA");
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).isEmpty();
	}

	@Test
	public void testNegativeValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "-1");
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.PERFORMANCE_RATE_INVALID_VALUE);
	}

	@Test
	public void testInvalidValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "2");
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.PERFORMANCE_RATE_INVALID_VALUE);
	}

	@Test
	public void testInvalidStringValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "Inval");
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.PERFORMANCE_RATE_INVALID_VALUE);
	}
}
