package gov.cms.qpp.conversion.model.error;

/**
 * An {@link Exception} that is thrown from the {@link gov.cms.qpp.conversion.Converter} on error.
 */
public class TransformException extends RuntimeException {
	private final AllErrors details;

	/**
	 * Construct a new {@code TransformException} exception.
	 *
	 * @param message The detail message
	 * @param cause A Throwable that caused this exception to occur.
	 * @param details The {@link AllErrors} that detail what went wrong.
	 */
	public TransformException(String message, Throwable cause, AllErrors details) {
		super(message, cause);
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
