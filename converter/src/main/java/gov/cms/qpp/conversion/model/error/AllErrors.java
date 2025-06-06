package gov.cms.qpp.conversion.model.error;

import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Contains a list of error errors.
 */
public class AllErrors implements Serializable {
	private static final long serialVersionUID = -223805249639231357L;
	private List<Error> errors;

	/**
	 * Constructs an {@code AllErrors} with no errors.
	 */
	public AllErrors() {
		// empty on purpose
	}

	/**
	 * Constructs an {@code AllErrors} with the specified {@link Error}s.
	 *
	 * @param errors The list of {@code Error}s.
	 */
	public AllErrors(List<Error> errors) {
		this.errors = errors;
	}

	/**
	 * Gets all the {@link Error}s.
	 *
	 * @return A defensive copy of the list of {@code Error}s (or null if none).
	 */
	public List<Error> getErrors() {
		if (errors == null) {
			return null;
		}
		// Perform the deduplication logic on the internal list
		for (Error error : errors) {
			if (!error.getDetails().isEmpty()) {
				List<Detail> deduped = new ArrayList<>(
						new LinkedHashSet<>(error.getDetails())
				);
				error.setDetails(deduped);
			}
		}
		// Return a new ArrayList to avoid exposing the internal list directly
		return new ArrayList<>(errors);
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
		if (errors == null) {
			errors = new ArrayList<>();
		}
		errors.add(error);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("errors", errors)
				.toString();
	}
}
