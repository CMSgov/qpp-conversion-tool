package gov.cms.qpp.conversion.stubs;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.validate.NodeValidator;

import java.util.List;


public class TestDefaultValidator extends NodeValidator {
	@Override
	protected void internalValidateSingleNode(final Node node) {
		if ( node.getValue( "problem" ) != null ){
			this.addValidationError( new ValidationError("Test validation error for Jenny"));
		}
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {}
}