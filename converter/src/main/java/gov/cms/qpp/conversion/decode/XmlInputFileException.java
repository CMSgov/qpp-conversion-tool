package gov.cms.qpp.conversion.decode;

import java.io.Serial;

/**
 * This exception indicates a problem with the source XML document.
 */
public class XmlInputFileException extends RuntimeException {

	@Serial private static final long serialVersionUID = 1L;

	public XmlInputFileException(String message, Exception cause) {
		super(message, cause);
	}
}
