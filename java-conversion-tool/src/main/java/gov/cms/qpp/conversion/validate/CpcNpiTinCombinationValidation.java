package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the QRDA Category III Report Node's national provide identifier/taxpayer identification number combinations
 * for the CPC+ program.
 */
@Validator(value = TemplateId.QRDA_CATEGORY_III_REPORT_V3, program = Program.CPC, required = true)
public class CpcNpiTinCombinationValidation extends NodeValidator {

	static final String AT_LEAST_ONE_NPI_TIN_COMBINATION = "Must have at least one NPI/TIN combination";


	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.childMinimum(AT_LEAST_ONE_NPI_TIN_COMBINATION, 1, TemplateId.NPI_TIN_ID);
	}
}
