package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

public class CpcPerformanceRateValidatorTest {
	private CpcPerformanceRateValidator cpcPerformanceRateValidator;
	private Node node;

	@Before
	public  void setup() {
		cpcPerformanceRateValidator = new CpcPerformanceRateValidator();
		node = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "0");
	}

	@Test
	public void testZeroValue() {
		cpcPerformanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", cpcPerformanceRateValidator.getDetails(), empty());
	}

	@Test
	public void testOneValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "1");
		cpcPerformanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", cpcPerformanceRateValidator.getDetails(), empty());
	}

	@Test
	public void testNAValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "NA");
		cpcPerformanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", cpcPerformanceRateValidator.getDetails(), empty());
	}

	@Test
	public void testNegativeValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "-1");
		cpcPerformanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", cpcPerformanceRateValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcPerformanceRateValidator.INVALID_PERFORMANCE_RATE));
	}

	@Test
	public void testInvalidValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "2");
		cpcPerformanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", cpcPerformanceRateValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcPerformanceRateValidator.INVALID_PERFORMANCE_RATE));
	}

	@Test
	public void testInvalidStringValue() {
		node.putValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, "Invalid");
		cpcPerformanceRateValidator.internalValidateSingleNode(node);
		assertThat("Must contain a proper value", cpcPerformanceRateValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcPerformanceRateValidator.INVALID_PERFORMANCE_RATE));
	}
}
