package gov.cms.qpp.conversion.validate;

import java.util.List;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;

public abstract class NodeValidator {

	public NodeValidator() {

	}

	public abstract List<ValidationError> validate(Node node);

	protected abstract List<ValidationError> internalValidate(Node node);

}
