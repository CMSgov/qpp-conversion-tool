package gov.cms.qpp.conversion.xml;

/**
 * Exception used during the JDom parsing.
 * @author David Uselmann
 *
 */
public class XmlException extends Exception {
	private static final long serialVersionUID = 1L;

	public XmlException(String message, Exception cause) {
		super(message, cause);
	}
}
