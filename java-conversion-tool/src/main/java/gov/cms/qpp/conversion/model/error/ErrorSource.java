package gov.cms.qpp.conversion.model.error;

import gov.cms.qpp.conversion.model.ValidationError;

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
	 * @param sourceIdentifier
	 * @param validationErrors
	 */
	public ErrorSource(final String sourceIdentifier, final List<ValidationError> validationErrors) {
		this.sourceIdentifier = sourceIdentifier;
		this.validationErrors = validationErrors;
	}

	/**
	 * @return
	 */
	public String getSourceIdentifier() {
		return sourceIdentifier;
	}

	/**
	 * @param sourceIdentifier
	 */
	public void setSourceIdentifier(final String sourceIdentifier) {
		this.sourceIdentifier = sourceIdentifier;
	}

	/**
	 * @return
	 */
	public List<ValidationError> getValidationErrors() {
		return validationErrors;
	}

	/**
	 * @param validationErrors
	 */
	public void setValidationErrors(final List<ValidationError> validationErrors) {
		this.validationErrors = validationErrors;
	}

	/**
	 * @param validationError
	 */
	public void addValidationError(final ValidationError validationError) {
		if (null == validationErrors) {
			validationErrors = new ArrayList<>();
		}

		validationErrors.add(validationError);
	}
}
