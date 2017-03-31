package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;

import java.util.ArrayList;
import java.util.List;

public abstract class NodeValidator {

	private List<ValidationError> validationErrors = new ArrayList<>();

	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	protected void addValidationError(ValidationError newError) {
		validationErrors.add(newError);
	}

	public abstract void validateNode(Node node);

	public abstract void validateNodes(List<Node> nodes);
}
