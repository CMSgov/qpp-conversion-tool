package gov.cms.qpp.conversion.decode;

public class DecodeException extends Exception {
	private static final long serialVersionUID = 1L;

	public DecodeException(String message, Exception cause) {
		super(message, cause);
	}
}
