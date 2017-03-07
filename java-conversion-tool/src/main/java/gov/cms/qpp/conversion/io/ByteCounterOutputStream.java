package gov.cms.qpp.conversion.io;

import java.io.DataOutputStream;

import org.apache.commons.io.output.NullOutputStream;

/**
 * Utility OutputStream to keep track the size of the output stream.
 * 
 * Useful for keeping track of serialization strategies.
 * @author David Puglielli
 *
 */
public class ByteCounterOutputStream extends DataOutputStream {
	public ByteCounterOutputStream() {
		super(NullOutputStream.NULL_OUTPUT_STREAM);
	}
}
