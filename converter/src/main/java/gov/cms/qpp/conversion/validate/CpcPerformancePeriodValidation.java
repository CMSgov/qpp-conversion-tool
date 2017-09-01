package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the QRDA Category III Report Node's national provide identifier/taxpayer identification number combinations
 * for the CPC+ program.
 */
@Validator(value = TemplateId.REPORTING_PARAMETERS_ACT, program = Program.CPC)
public class CpcPerformancePeriodValidation extends NodeValidator {

	static final String PERFORMANCE_START_JAN12017 = "Must be 01/01/2017";
	static final String PERFORMANCE_END_DEC312017 = "Must be 12/31/2017";

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.valueIs(PERFORMANCE_START_JAN12017, ReportingParametersActDecoder.PERFORMANCE_START, "20170101")
			.valueIs(PERFORMANCE_END_DEC312017, ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
	}
}
