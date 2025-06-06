package gov.cms.qpp.conversion.model.error;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a dual collection wrapper that will contain
 * all validation warning and error details for a given QPP Node.
 *
 * If this is called from the QrdaValidator.validate(Node) method
 * then it will contain all validation details for all sub-nodes.
 *
 * However, if NodeValidator.validateSingleNode(Node) is called
 * then it will contain only validations for the given node only.
 *
 * Its primary motivation is that only one value may be returned
 * from a method. This wraps both types into one instance.
 */
public class ValidationResult {

	private final List<Detail> errors;
	private final List<Detail> warnings;

	/**
	 * The all attributes constructor.
	 * @param errors the errors for the node.
	 * @param warnings the warnings for the node.
	 */
	public ValidationResult(List<Detail> errors, List<Detail> warnings) {
		this.errors = errors;
		this.warnings = warnings;
	}

	/**
	 * Get a defensive copy of the errors collection.
	 * @return node errors collection copy
	 */
	public List<Detail> getErrors() {
		return new ArrayList<>(errors);
	}

	/**
	 * Get a defensive copy of the warnings collection.
	 * @return node warnings collection copy
	 */
	public List<Detail> getWarnings() {
		return new ArrayList<>(warnings);
	}

}
