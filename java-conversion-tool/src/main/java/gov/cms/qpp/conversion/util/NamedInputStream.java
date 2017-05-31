package gov.cms.qpp.conversion.util;

import java.io.InputStream;

import org.apache.commons.io.input.ProxyInputStream;

public class NamedInputStream extends ProxyInputStream {

	private final String name;

	public NamedInputStream(InputStream delegate, String name) {
		super(delegate);
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}