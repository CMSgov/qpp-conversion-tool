package gov.cms.qpp.conversion.util;

import java.util.concurrent.ExecutionException;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;

public class ConverterTestHelper {

	public static JsonWrapper run(Converter converter) {
		try {
			return converter.transform().get();
		} catch (ExecutionException | InterruptedException exception) {
			Throwable cause = exception.getCause();
			if (cause instanceof RuntimeException) {
				throw (RuntimeException) cause;
			}
			throw new RuntimeException(exception);
		}
	}

	private ConverterTestHelper() {
	}

}