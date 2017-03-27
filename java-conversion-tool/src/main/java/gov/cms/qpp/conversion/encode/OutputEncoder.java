package gov.cms.qpp.conversion.encode;

import java.io.Writer;

/**
 * Interface for encoding output.
 * @author Scott Fradkin
 * 
 */
public interface OutputEncoder {

	/**
	 * Encode data to a Writer
	 * 
	 * @param writer
	 */
	void encode(Writer writer) throws EncodeException;
}
