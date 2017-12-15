package gov.cms.qpp.conversion;

import java.io.InputStream;

public interface Source {

	String getName();

	InputStream toInputStream();

	long getSize();
}
