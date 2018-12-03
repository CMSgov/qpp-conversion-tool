package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates the QRDA Category III Performance Rate Proportion Measure for the cpc+ program
 */
@Validator(value = TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
public class PerformanceRateValidator extends NodeValidator {

	protected static final String NULL_ATTRIBUTE = "NA";

	/**
	 * Validates that the node given contains a value in range of 0-1 or null attribute
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		if (!NULL_ATTRIBUTE.equals(node.getValue(PerformanceRateProportionMeasureDecoder.NULL_PERFORMANCE_RATE))) {

			check(node)
				.valueIsNotEmpty(ErrorCode.PERFORMANCE_RATE_MISSING, PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE);

			if (getDetails().isEmpty()) {
				String performanceRate = node.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE);

				check(node)
					.inDecimalRangeOf(ErrorCode.PERFORMANCE_RATE_INVALID_VALUE.format(performanceRate),
						PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE, 0F, 1F);
			}
		}
	}
}
