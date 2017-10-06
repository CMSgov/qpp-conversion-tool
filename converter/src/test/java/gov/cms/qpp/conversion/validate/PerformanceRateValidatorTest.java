package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

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
		assertThat("Must contain a proper value", performanceRateValidator.getDetails(), empty());
	}

	@Test
	public void testOneValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "1");
		performanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", performanceRateValidator.getDetails(), empty());
	}

	@Test
	public void testNAValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE, "NA");
		performanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", performanceRateValidator.getDetails(), empty());
	}

	@Test
	public void testNegativeValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "-1");
		performanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", performanceRateValidator.getDetails(),
				hasValidationErrorsIgnoringPath(PerformanceRateValidator.INVALID_PERFORMANCE_RATE));
	}

	@Test
	public void testInvalidValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "2");
		performanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", performanceRateValidator.getDetails(),
				hasValidationErrorsIgnoringPath(PerformanceRateValidator.INVALID_PERFORMANCE_RATE));
	}

	@Test
	public void testInvalidStringValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "Inval");
		performanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", performanceRateValidator.getDetails(),
				hasValidationErrorsIgnoringPath(PerformanceRateValidator.INVALID_PERFORMANCE_RATE));
	}
}
