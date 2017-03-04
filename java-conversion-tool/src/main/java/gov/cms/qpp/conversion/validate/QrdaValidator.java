package gov.cms.qpp.conversion.validate;

import java.util.ArrayList;
import java.util.List;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

public class QrdaValidator extends NodeValidator {

	protected static Registry<String, QrdaValidator> validators = new Registry<>(Validator.class);
	protected static List<ValidationError> validationErrors = new ArrayList<>();

	public QrdaValidator() {

	}

	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	public static void resetValidationErrors() {
		validationErrors.clear();
	}

	protected void addValidationError(ValidationError newError) {
		validationErrors.add(newError);
	}

	public List<ValidationError> validate(Node node) {

		// iterate through all of the known validators
		// each validator understands if the node it's for is required
		// it also can do other validations
		for (String key : validators.getKeys()) {
			QrdaValidator aValidator = validators.get(key);

			aValidator.internalValidate(node);
		}

		// do we need to do anything with any Nodes that weren't validated?
		// if they are extraneous Nodes, is that an issue?
		// the encoder will only encode the same Nodes we're validating

		return validationErrors;
	}

	/**
	 * the internalValidate method of QppValidator does nothing
	 */
	protected List<ValidationError> internalValidate(Node node) {

		return validationErrors;
	}

}
