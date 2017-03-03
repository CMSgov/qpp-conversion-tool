package gov.cms.qpp.conversion.decode;

public class XmlInputFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public XmlInputFileException(String message, Exception cause) {
		super(message, cause);
	}

	public XmlInputFileException(String message) {
		super(message);
	}
}
