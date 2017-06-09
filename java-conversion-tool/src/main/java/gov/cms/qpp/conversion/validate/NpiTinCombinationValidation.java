package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import java.util.List;

/**
 * Validates the QRDA Category II Report Node's national provide identifier/taxpayer identification number combinations
 * for the given programs: MIPS individual, MIPS group, CPC+.
 */
@Validator(value = TemplateId.QRDA_CATEGORY_III_REPORT_V3, required = true)
public class NpiTinCombinationValidation extends NodeValidator {

	protected static final String ONLY_ONE_NPI_TIN_COMBINATION_ALLOWED = "Must have only one NPI/TIN combination";
	protected static final String NO_NPI_ALLOWED = "Must contain a Taxpayer Identification Number";
	protected static final String CONTAINS_TAXPAYER_IDENTIFICATION_NUMBER =
			"Must contain a Taxpayer Identification Number";

	/**
	 * Validates the NPI/TIN Combination within the QRDA Category III Report V3 section
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		Node clinicalDocumentNode = node.findFirstNode(TemplateId.CLINICAL_DOCUMENT);
		final String programName = clinicalDocumentNode.getValue(ClinicalDocumentDecoder.PROGRAM_NAME);
		final String entityType = clinicalDocumentNode.getValue(ClinicalDocumentDecoder.ENTITY_TYPE);

		if (isMipsIndividual(programName, entityType)) {
			ensureOneNpiTinCombinationExists(node);

		} else if (isMipsGroup(programName, entityType)) {
			ensureOneNpiTinCombinationExists(node);
			check(node.findFirstNode(TemplateId.NPI_TIN_ID))
					.value(CONTAINS_TAXPAYER_IDENTIFICATION_NUMBER,
							MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER)
					.valueIsNull(NO_NPI_ALLOWED,
							MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER);
		}

	}

	/**
	 * Validates that only one NPI/TIN combination was decoded
	 *
	 * @param node object to be validated
	 */
	private void ensureOneNpiTinCombinationExists(Node node) {
		check(node)
			.childMaximum(ONLY_ONE_NPI_TIN_COMBINATION_ALLOWED, 1, TemplateId.NPI_TIN_ID)
			.childMinimum(ONLY_ONE_NPI_TIN_COMBINATION_ALLOWED, 1, TemplateId.NPI_TIN_ID);
	}

	/**
	 * Check the Program name and entity type for Mips Individual
	 *
	 * @param programName name to be checked
	 * @param entityType type to be checked
	 * @return true for proper program name and type
	 */
	private boolean isMipsIndividual(String programName, String entityType) {
		 return (ClinicalDocumentDecoder.MIPS.equalsIgnoreCase(programName)
				 && ClinicalDocumentDecoder.ENTITY_INDIVIDUAL.equalsIgnoreCase(entityType));
	}

	/**
	 * Checks the program name and entity type for Mips Group
	 *
	 * @param programName name to be checked
	 * @param entityType type to be checked
	 * @return true for a proper program name and type
	 */
	private boolean isMipsGroup(String programName, String entityType) {
		return (ClinicalDocumentDecoder.MIPS.equalsIgnoreCase(programName)
				&& ClinicalDocumentDecoder.ENTITY_GROUP.equalsIgnoreCase(entityType));
	}

	/**
	 * Checks the interdependency of nodes in the parsed tree.
	 * QRDA Category Report has no dependencies on other nodes in the document.
	 *
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// No current cross node QRDA Category Report validations
	}
}
