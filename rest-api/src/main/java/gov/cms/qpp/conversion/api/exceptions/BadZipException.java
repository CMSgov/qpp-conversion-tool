package gov.cms.qpp.conversion.api.exceptions;

/**
 * Exception for handling an invalid file type
 */
public class BadZipException extends RuntimeException {

	/**
	 * Constructor to call RuntimeException
	 * @param message Error response
	 */
	public BadZipException(String message) {
		super(message);
	}
}
