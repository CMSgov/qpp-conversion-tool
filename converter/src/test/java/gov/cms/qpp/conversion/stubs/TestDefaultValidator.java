package gov.cms.qpp.conversion.stubs;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.validate.NodeValidator;

/**
 * Validator instance for use in unit tests.
 * 
 * TODO Does not appear to be referenced.
 */
public class TestDefaultValidator extends NodeValidator {

	@Override
	protected void performValidation(Node node) {
		if (node.getValue("problem") != null) {
			Detail detail = Detail.forErrorCode(ErrorCode.UNEXPECTED_ERROR);
			detail.setMessage("Test validation error for Jenny");
			addError(detail);
		}
	}
}