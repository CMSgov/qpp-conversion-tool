package gov.cms.qpp.conversion.util;

import java.io.IOException;
import java.io.UncheckedIOException;

public class JsonReadException extends UncheckedIOException {

	public JsonReadException(String message, IOException thrown) {
		super(message, thrown);
	}

}
