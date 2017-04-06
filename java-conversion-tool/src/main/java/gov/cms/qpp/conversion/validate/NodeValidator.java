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

	public List<ValidationError> validateSingleNode(final Node node) {

		internalValidateSingleNode(node);
		return getValidationErrors();
	}

	public List<ValidationError> validateSameTemplateIdNodes(final List<Node> nodes) {

		internalValidateSameTemplateIdNodes(nodes);
		return getValidationErrors();
	}

	protected abstract void internalValidateSingleNode(final Node node);

	protected abstract void internalValidateSameTemplateIdNodes(final List<Node> nodes);
}
