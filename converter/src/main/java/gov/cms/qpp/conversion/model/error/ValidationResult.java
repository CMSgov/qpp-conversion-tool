package gov.cms.qpp.conversion.model.error;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a dual collection wrapper that will contain
 * all validation warning and error details for a given QPP Node.
 */
public class ValidationResult {

	private final List<Detail> errors;
	private final List<Detail> warnings;

	/**
	 * The all-attributes constructor.
	 * Makes defensive copies of the incoming lists.
	 */
	public ValidationResult(List<Detail> errors, List<Detail> warnings) {
		this.errors = (errors == null)
				? new ArrayList<>()
				: new ArrayList<>(errors);
		this.warnings = (warnings == null)
				? new ArrayList<>()
				: new ArrayList<>(warnings);
	}

	/**
	 * Get a mutable copy of the errors.
	 * Any modifications to the returned list won’t affect the internal state.
	 */
	public List<Detail> getErrors() {
		return new ArrayList<>(errors);
	}

	/**
	 * Get a mutable copy of the warnings.
	 * Any modifications to the returned list won’t affect the internal state.
	 */
	public List<Detail> getWarnings() {
		return new ArrayList<>(warnings);
	}
}
