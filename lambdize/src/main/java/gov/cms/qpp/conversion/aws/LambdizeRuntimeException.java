package gov.cms.qpp.conversion.aws;

/**
 * Exception used during Conversion
 */
public class LambdizeRuntimeException extends RuntimeException {
	/**
	 * Public Constructor
	 * @param exception the exception that occurred
	 */
	public LambdizeRuntimeException(Exception exception) {
		super(exception);
	}
}
