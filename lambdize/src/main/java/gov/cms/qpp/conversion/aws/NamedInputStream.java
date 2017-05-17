package gov.cms.qpp.conversion.aws;

import java.io.IOException;
import java.io.InputStream;

public class NamedInputStream extends InputStream {

	private final String name;
	private final InputStream delegate;

	public NamedInputStream(String name, InputStream delegate)
	{
		this.name = name;
		this.delegate = delegate;
	}

	@Override
	public int read() throws IOException
	{
		return this.delegate.read();
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	@Override
	public void close() throws IOException
	{
		super.close();
		this.delegate.close();
	}

}