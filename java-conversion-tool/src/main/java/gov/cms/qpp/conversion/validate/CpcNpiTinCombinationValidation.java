package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the QRDA Category III Report Node's national provide identifier/taxpayer identification number combinations
 * for the given programs: MIPS individual, MIPS group, CPC+.
 */
@Validator(value = TemplateId.QRDA_CATEGORY_III_REPORT_V3, program = Program.CPC, required = true)
public class CpcNpiTinCombinationValidation extends NodeValidator {

	static final String AT_LEAST_ONE_NPI_TIN_COMBINATION = "Must have at least one NPI/TIN combination";
	static final String ONLY_ONE_APM_ALLOWED =
			"One and only one Alternative Payment Model (APM) Entity Identifier should be specified";

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
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
