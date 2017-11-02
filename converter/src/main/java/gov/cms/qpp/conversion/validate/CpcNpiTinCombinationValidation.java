package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates the QRDA Category III Report Node's national provide identifier/taxpayer identification number combinations
 * for the CPC+ program.
 */
@Validator(value = TemplateId.QRDA_CATEGORY_III_REPORT_V3, program = Program.CPC)
public class CpcNpiTinCombinationValidation extends NodeValidator {


	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.childMinimum(ErrorCode.CPC_NPI_TIN_COMBINATION_MISSING_NPI_TIN_COMBINATION, 1, TemplateId.NPI_TIN_ID);
	}
}
