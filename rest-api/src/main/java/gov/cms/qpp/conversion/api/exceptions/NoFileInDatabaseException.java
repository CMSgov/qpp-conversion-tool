package gov.cms.qpp.conversion.api.exceptions;

/**
 * Exception for an database not
 */
public class NoFileInDatabaseException extends IllegalArgumentException {

	/**
	 * Build this exception with as a {@link IllegalArgumentException} with a defaulted message
	 *
	 * @param message to be passed into the exception
	 */
	public NoFileInDatabaseException(String message) {
		super(message);
	}
}
