package gov.cms.qpp.conversion.io;

import java.io.DataOutputStream;

import org.apache.commons.io.output.NullOutputStream;

public class ByteCounterOutputStream extends DataOutputStream {
	public ByteCounterOutputStream() {
		super(NullOutputStream.NULL_OUTPUT_STREAM);
	}
}
