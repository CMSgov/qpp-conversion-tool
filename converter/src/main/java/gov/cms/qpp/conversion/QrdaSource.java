package gov.cms.qpp.conversion;

import java.io.InputStream;

public interface QrdaSource {

	String getName();

	InputStream toInputStream();

	long getSize();
}
