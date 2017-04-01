package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeValidator {

	private List<ValidationError> validationErrors;

	public NodeValidator() {
		validationErrors = new ArrayList<>();
	}

	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	protected void addValidationError(final ValidationError newError) {
		validationErrors.add(newError);
	}

	public abstract void validateNode(final Node node);

	public abstract void validateNodes(final List<Node> nodes);
}
