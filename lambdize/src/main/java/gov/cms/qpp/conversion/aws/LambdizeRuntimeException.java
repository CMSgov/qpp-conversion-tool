package gov.cms.qpp.conversion.aws;

/**
 * Exception used during Conversion
 */
public class LambdizeRuntimeException extends RuntimeException {
	/**
	 * Public Constructor
	 * @param cause the exception that occurred
	 */
	public LambdizeRuntimeException(Exception cause) {
		super(cause);
	}
}
