package gov.cms.qpp.conversion.model.error;

/**
 * An {@link Exception} that is thrown from the {@link gov.cms.qpp.conversion.Converter} on error.
 */
public class TransformException extends RuntimeException {
	private final transient AllErrors details; // transient to make sonar happy. This is never serialized.

	/**
	 * Construct a new {@code TransformException} exception.
	 *
	 * @param message The detail message
	 * @param details The {@link AllErrors} that detail what went wrong.
	 */
	public TransformException(String message, AllErrors details) {
		super(message);
		this.details = details;
	}

	/**
	 * Get the errors that detail went wrong during the conversion.
	 *
	 * @return The errors.
	 */
	public AllErrors getDetails() {
		return details;
	}
}
