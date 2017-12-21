package gov.cms.qpp.conversion.api.exceptions;

/**
 * Exception for handling an invalid file type
 */
public class InvalidFileTypeException extends RuntimeException {

	/**
	 * Constructor to call RuntimeException
	 * @param message
	 */
	public InvalidFileTypeException(String message) {
		super(message);
	}
}
