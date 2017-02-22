package gov.cms.qpp.conversion.parser;

public class DecodeEception extends Exception {
	private static final long serialVersionUID = 1L;

	public DecodeEception(String message, Exception cause) {
		super(message, cause);
	}
}
