package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

/**
 * Validates the format of a TIN
 */
@Validator(value = TemplateId.NPI_TIN_ID, program = Program.CPC)
public class CpcNpiTinFormatValidator extends NodeValidator {

	static final int TAX_PAYER_ID_LENGTH = 9;
	static final String TAX_PAYER_ID_MUST_BE_LENGTH = MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER + 
			" must be length " + TAX_PAYER_ID_LENGTH;
	static final String TAX_PAYER_ID_MUST_BE_INTEGER = MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER + 
			" must be an integer";

	@Override
	protected void internalValidateSingleNode(Node node) {
		check(node)
			.valueIsLength(TAX_PAYER_ID_MUST_BE_LENGTH, MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, TAX_PAYER_ID_LENGTH)
			.intValue(TAX_PAYER_ID_MUST_BE_INTEGER, MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);
	}

}
