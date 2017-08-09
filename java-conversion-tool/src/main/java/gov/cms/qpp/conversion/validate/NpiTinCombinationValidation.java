package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the QRDA Category II Report Node's national provide identifier/taxpayer identification number combinations
 * for the given programs: MIPS individual, MIPS group, CPC+.
 */
@Validator(value = TemplateId.QRDA_CATEGORY_III_REPORT_V3, required = true)
public class NpiTinCombinationValidation extends NodeValidator {

	static final String CLINICAL_DOCUMENT_REQUIRED = "Clinical Document Node is required";
	static final String EXACTLY_ONE_DOCUMENT_ALLOWED = "Only one Clinical Document Node is allowed";
	static final String AT_LEAST_ONE_NPI_TIN_COMBINATION = "Must have at least one NPI/TIN combination";
	static final String ONLY_ONE_NPI_TIN_COMBINATION_ALLOWED = "Must have only one NPI/TIN combination";
	static final String ONLY_ONE_APM_ALLOWED =
			"One and only one Alternative Payment Model (APM) Entity Identifier should be specified";

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node).childMinimum(CLINICAL_DOCUMENT_REQUIRED, 1, TemplateId.CLINICAL_DOCUMENT)
			.childMaximum(EXACTLY_ONE_DOCUMENT_ALLOWED, 1, TemplateId.CLINICAL_DOCUMENT);

		if (!getDetails().isEmpty()) {
			return;
		}

		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		final String programName = clinicalDocumentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME);

		if (ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME.equalsIgnoreCase(programName)) {
			check(node)
				.childMinimum(AT_LEAST_ONE_NPI_TIN_COMBINATION, 1, TemplateId.NPI_TIN_ID);
			check(clinicalDocumentNode)
				.incompleteValidation()
				.singleValue(ONLY_ONE_APM_ALLOWED, ClinicalDocumentDecoder.ENTITY_ID);
		}

	}
}
