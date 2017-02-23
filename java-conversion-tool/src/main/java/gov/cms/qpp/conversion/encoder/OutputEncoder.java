package gov.cms.qpp.conversion.encoder;

import java.io.Writer;

/**
 * Interface for encoding output
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
