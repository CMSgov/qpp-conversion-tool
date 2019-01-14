package gov.cms.qpp.conversion.encode;

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
	void encode(Writer writer, boolean filter);

	/**
	 * Encode data to JsonWrapper
	 */
	JsonWrapper encode();
}
