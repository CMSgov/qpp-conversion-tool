package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsMessageEquals;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

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
				.that(performanceRateValidator.getDetails()).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(PerformanceRateValidator.INVALID_PERFORMANCE_RATE);
	}

	@Test
	public void testInvalidValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "2");
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(PerformanceRateValidator.INVALID_PERFORMANCE_RATE);
	}

	@Test
	public void testInvalidStringValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "Inval");
		performanceRateValidator.internalValidateSingleNode(node);
		assertWithMessage("Must contain a proper value")
				.that(performanceRateValidator.getDetails()).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(PerformanceRateValidator.INVALID_PERFORMANCE_RATE);
	}
}
