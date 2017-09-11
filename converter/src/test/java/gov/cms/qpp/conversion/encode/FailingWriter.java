package gov.cms.qpp.conversion.encode;

import java.io.IOException;
import java.io.Writer;

public class FailingWriter extends Writer {

	public FailingWriter() {
		// empty
	}

	public FailingWriter(Object lock) {
		super(lock);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		throw new IOException("Fake IOException");

	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public void close() throws IOException {

	}

}
