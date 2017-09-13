package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the QRDA Category III Performance Rate Proportion Measure for the cpc+ program
 */
@Validator(value = TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE, program = Program.CPC)
public class CpcPerformanceRateValidator extends NodeValidator {

	protected static final String INVALID_PERFORMANCE_RATE = "Must enter a valid Performance Rate value";
	protected static final String NULL_ATTRIBUTE = "NA";

	/**
	 * Validates that the node given contains a 0, 1, or Null value
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		if (!NULL_ATTRIBUTE.equals(node.getValue(PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE))) {
			check(node)
					.inDecimalRangeOf(INVALID_PERFORMANCE_RATE,
							PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE,0f, 1f);
		}
	}
}
