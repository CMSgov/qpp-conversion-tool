package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the Clinical Document for the CPC+ program.
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.CPC, required = true)
public class CpcClinicalDocumentValidator extends NodeValidator {

	static final String ONLY_ONE_APM_ALLOWED =
		"One and only one Alternative Payment Model (APM) Entity Identifier should be specified";

	/**
	 * Validates the APM in the clinical document.
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.singleValue(ONLY_ONE_APM_ALLOWED, ClinicalDocumentDecoder.ENTITY_ID);
	}
}
