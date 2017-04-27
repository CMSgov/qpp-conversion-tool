package gov.cms.qpp.conversion.model;

/**
 * Holds the error information from Validators.
 */
public class ValidationError {

	private String errorText;
	private String path = "";

	/**
	 * Dummy constructor for Jackson mapping
	 */
	public ValidationError() {
		//Dummy constructor for jackson mapping
	}

	/**
	 * Constructs a {@code ValidationError} with just a description.
	 *
	 * @param text A description of the error.
	 */
	public ValidationError(String text) {
		this.errorText = text;
	}

	/**
	 * Constructs a {@code ValidationError} with a description and an path to point where the error is in the original document.
	 *
	 * @param text A description of the error.
	 * @param path A path to where the error is.
	 */
	public ValidationError(String text, String path) {
		this.errorText = text;
		this.path = path;
	}

	/**
	 * A description of what this error is about.
	 *
	 * @return An error description.
	 */
	public String getErrorText() {
		return errorText;
	}

	/**
	 * Gets the path that this error references.
	 *
	 * @return The path that this error references.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return A string representation.
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ValidationError{");
		sb.append("errorText='").append(errorText).append('\'');
		sb.append(", path='").append(path).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
