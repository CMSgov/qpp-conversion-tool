package gov.cms.qpp.conversion.model.error;

import java.util.List;

/**
 * This is a dual collection wrapper that will contain
 * all validation warning and error details for a given QPP Node.
 * 
 * If this is called from the QrdaValidator.validate(Node) method
 * then it will contains all validation details for all sub-nodes.
 * 
 * However, if NodeValidator.validateSingleNode(Node) is called
 * then it will contain only validations for the given node only.
 * 
 * Its primary motivation is that only one value may be returned
 * from a method. This wraps both types into one instance.
 */
public class ValidationResult {

	/**
	 * The collection of all error details for the Nodes visited.
	 */
	private final List<Detail> errors;
	/**
	 * The collection of all warnings details for the Nodes visited.
	 */
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
	 * Get the errors collection.
	 * @return node errors collection
	 */
	public List<Detail> getErrors() {
		return errors;
	}

	/**
	 * Get the warnings collection.
	 * @return node errors collection
	 */
	public List<Detail> getWarnings() {
		return warnings;
	}

}
