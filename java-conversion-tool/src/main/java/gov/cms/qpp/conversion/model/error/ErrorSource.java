package gov.cms.qpp.conversion.model.error;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of validation errors associated with a single source.
 *
 * The source could be a file, a stream, or some other entity.
 */
public class ErrorSource {
	private String sourceIdentifier;
	private List<ValidationError> validationErrors;

	/**
	 * Constructs an empty {@code ErrorSource}.
	 */
	public ErrorSource() {
		//empty on purpose
	}

	/**
	 * Constructs an {@code ErrorSource} with the specified source identifier and list of
	 * {@link ValidationError}.
	 *
	 * @param sourceIdentifier The identifier of a source that contains the validation errors
	 * @param validationErrors The list of {@code ValidationError}s.
	 */
	public ErrorSource(final String sourceIdentifier, final List<ValidationError> validationErrors) {
		this.sourceIdentifier = sourceIdentifier;
		this.validationErrors = validationErrors;
	}

	/**
	 * Gets the source identifier.
	 *
	 * @return The source identifier.
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	/**
	 * Sets the source identifier.
	 *
	 * @param sourceIdentifier The source identifier.
	 */
	public void setSourceIdentifier(final String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	/**
	 * getValidationErrors returns the list of ValidationErrors
	 *
	 * @return A list of ValidationErrors.
	 */
	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	/**
	 * setValidationErrors sets the internal List of ValidationError
	 *
	 * @param validationErrors A list of ValidationErrors.
	 */
	public void setValidationErrors(final List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}

	/**
	 * addValidationError will add an error to the list of validation errors
	 *
	 * @param validationError The ValidationError to add.
	 */
	public void addValidationError(final ValidationError validationError) {
		if (null == validationErrors) {
			validationErrors = new ArrayList<>();
		}

		validationErrors.add(validationError);
	}
}
