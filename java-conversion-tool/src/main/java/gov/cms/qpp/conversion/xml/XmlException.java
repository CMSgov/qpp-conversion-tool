package gov.cms.qpp.conversion.xml;

/**
 * Exception used during the JDom parsing.
 *
 */
public class XmlException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a named Exception
	 * @param message String reason the exception is occurring
	 * @param cause Exception that originally was thrown
	 */
	public XmlException(String message, Exception cause) {
		super(message, cause);
	}

	/**
	 * Public Constructor
	 * @param message String reason the exception is occurring
	 */
	public XmlException(String message) {
		super(message);
	}
}
