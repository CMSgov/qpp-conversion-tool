package gov.cms.qpp.conversion.stubs;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.validate.NodeValidator;

public class TestDefaultValidator extends NodeValidator {

	@Override
	protected void internalValidateSingleNode(final Node node) {
		if ( node.getValue( "problem" ) != null ){
			this.addValidationError( new Detail("Test validation error for Jenny"));
		}
	}
}