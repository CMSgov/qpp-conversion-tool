package gov.cms.qpp.conversion.decode;

/**
 * This exception indicates an decoding issue encountered during the decoding (XML parsing) process.
 * @author David Uselmann
 *
 */
public class DecodeException extends Exception {
	private static final long serialVersionUID = 1L;

	public DecodeException(String message, Exception cause) {
		super(message, cause);
	}

	public DecodeException(String message) {
		super(message);
	}
}
