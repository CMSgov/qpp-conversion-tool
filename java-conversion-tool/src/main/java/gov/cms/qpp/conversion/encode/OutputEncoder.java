package gov.cms.qpp.conversion.encode;

import java.io.InputStream;
import java.io.Writer;

/**
 * Interface for encoding output.
 */
public interface OutputEncoder {

	/**
	 * Encode data to a Writer
	 * 
	 * @param writer
	 */
	void encode(Writer writer) throws EncodeException;

	/**
	 * Encode data to a String
	 */
	InputStream encode() throws EncodeException;
}
