package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.ErrorCode;

/**
 * Validates the QRDA Category III Report Node's national provide identifier/taxpayer identification number combinations
 * for the given programs: MIPS individual, MIPS group, CPC+.
 */
@Validator(TemplateId.QRDA_CATEGORY_III_REPORT_V3)
public class NpiTinCombinationValidation extends NodeValidator {

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node).childMinimum(ErrorCode.NPI_TIN_COMBINATION_MISSING_CLINICAL_DOCUMENT, 1, TemplateId.CLINICAL_DOCUMENT)
			.childMaximum(ErrorCode.NPI_TIN_COMBINATION_EXACTLY_ONE_DOCUMENT_ALLOWED, 1, TemplateId.CLINICAL_DOCUMENT);
	}
}
