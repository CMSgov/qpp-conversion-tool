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

	protected static final String INVALID_PERFORMANCE_RATE = "Must contain a valid performance rate proportion measure";

	/**
	 * Validates that the node given contains a 0, 1, or Null value
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
				.valueIn(INVALID_PERFORMANCE_RATE, PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE,
						"0", "1", "NA");
	}
}
