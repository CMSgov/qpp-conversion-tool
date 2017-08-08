package gov.cms.qpp.conversion.model.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of error errors.
 */
public class AllErrors {

	private List<Error> errors;

	/**
	 * Constructs an {@code AllErrors} with no errors.
	 */
	public AllErrors() {
		//empty on purpose
	}

	/**
	 * Constructs a {@code AllErrors} with the specified {@link Error}s.
	 *
	 * @param errors The list of {@code Error}s.
	 */
	public AllErrors(List<Error> errors) {
		this.errors = errors;
	}

	/**
	 * Gets all the {@link Error}s.
	 *
	 * @return All the {@code Error}s.
	 */
	public List<Error> getErrors() {
		return errors;
	}

	/**
	 * Sets all the {@link Error}.
	 *
	 * @param errors The {@code Error}s to use.
	 */
	public void setErrors(final List<Error> errors) {
		this.errors = errors;
	}

	/**
	 * Adds a {@link Error} to the list.
	 *
	 * @param error The {@code Error} to add.
	 */
	public void addError(Error error) {
		if (null == errors) {
			errors = new ArrayList<>();
		}

		errors.add(error);
	}
}
