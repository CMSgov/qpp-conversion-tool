package gov.cms.qpp.conversion.decode;

/**
 * This exception indicates a problem with the source XML document.
 * @author David Puglielli
 *
 */
public class XmlInputFileException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public XmlInputFileException(String message, Exception cause) {
		super(message, cause);
	}

	public XmlInputFileException(String message) {
		super(message);
	}
}
