package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates the QRDA Category III Report Node's national provide identifier/taxpayer identification number combinations
 * for the CPC+ program.
 */
@Validator(value = TemplateId.REPORTING_PARAMETERS_ACT, program = Program.CPC)
public class CpcPerformancePeriodValidation extends NodeValidator {

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.valueIs(ErrorCode.CPC_PERFORMANCE_PERIOD_START_JAN12017, ReportingParametersActDecoder.PERFORMANCE_START, "20170101")
			.valueIs(ErrorCode.CPC_PERFORMANCE_PERIOD_END_DEC312017, ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
	}
}
