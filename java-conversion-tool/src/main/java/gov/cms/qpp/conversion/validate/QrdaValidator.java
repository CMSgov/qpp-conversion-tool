package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.ArrayList;
import java.util.List;

public class QrdaValidator {

	private static Registry<String, NodeValidator> validators = new Registry<>(Validator.class);

	public List<ValidationError> validate(Node node) {

		List<ValidationError> validationErrors = new ArrayList<>();

		// iterate through all of the known validators
		// each validator understands if the node it's for is required
		// it also can do other validations
		for (String key : validators.getKeys()) {

			NodeValidator aValidator = validators.get(key);
			aValidator.validateNode(node);
			validationErrors.addAll(aValidator.getValidationErrors());
		}

		// do we need to do anything with any Nodes that weren't validated?
		// if they are extraneous Nodes, is that an issue?
		// the encoder will only encode the same Nodes we're validating

		return validationErrors;
	}
}
