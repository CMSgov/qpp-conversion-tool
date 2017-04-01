package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeValidator {

	private List<ValidationError> validationErrors = new ArrayList<>();

	protected List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	protected void addValidationError(final ValidationError newError) {
		validationErrors.add(newError);
	}

	public List<ValidationError> validateNode(final Node node) {

		internalValidateNode(node);
		return getValidationErrors();
	}

	public List<ValidationError> validateNodes(final List<Node> nodes) {

		internalValidateNodes(nodes);
		return getValidationErrors();
	}

	protected abstract void internalValidateNode(final Node node);

	protected abstract void internalValidateNodes(final List<Node> nodes);
}
