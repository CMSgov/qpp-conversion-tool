package gov.cms.qpp.conversion.util;


public class JsonReadException extends RuntimeException {
	public JsonReadException(String message, Throwable thrown) {
		super(message, thrown);
	}
}
