package gov.cms.qpp.conversion.model.error;

public interface LocalizedError {

	/**
	 * Gets the error code associated with this error
	 */
	ErrorCode getErrorCode();

	/**
	 * Gets the message associated with this error
	 */
	String getMessage();

}
