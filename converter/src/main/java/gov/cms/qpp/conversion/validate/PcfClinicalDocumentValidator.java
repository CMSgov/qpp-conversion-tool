package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;


/**
 * Validates the Clinical Document level node for the given program: PCF
 * Using the same validation of CPC+
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.PCF)
public class PcfClinicalDocumentValidator extends CpcClinicalDocumentValidator {

	public PcfClinicalDocumentValidator(Context context) {
		super(context);
	}

	@Override
	protected void performValidation(final Node node) {
		super.performValidation(node);
	}
}
