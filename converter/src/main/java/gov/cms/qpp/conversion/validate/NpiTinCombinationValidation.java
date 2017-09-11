package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the QRDA Category III Report Node's national provide identifier/taxpayer identification number combinations
 * for the given programs: MIPS individual, MIPS group, CPC+.
 */
@Validator(TemplateId.QRDA_CATEGORY_III_REPORT_V3)
public class NpiTinCombinationValidation extends NodeValidator {

	static final String CLINICAL_DOCUMENT_REQUIRED = "Clinical Document Node is required";
	static final String EXACTLY_ONE_DOCUMENT_ALLOWED = "Only one Clinical Document Node is allowed";

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node).childMinimum(CLINICAL_DOCUMENT_REQUIRED, 1, TemplateId.CLINICAL_DOCUMENT)
			.childMaximum(EXACTLY_ONE_DOCUMENT_ALLOWED, 1, TemplateId.CLINICAL_DOCUMENT);
	}
}
