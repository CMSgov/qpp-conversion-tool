package gov.cms.qpp.conversion.util;

import com.google.common.primitives.Ints;

/**
 * Help with environment concerns
 */
public class EnvironmentHelper {

	/**
	 * Constructor that is private and empty because this is a utility class.
	 */
	private EnvironmentHelper() {
		//private and empty because this is a utility class
	}

	public static boolean isPresent(String variable) {
		return System.getenv(variable) != null || System.getProperty(variable) != null;
	}

	public static int getInt(String variable, int defaultValue) {
		String value = System.getProperty(variable);
		if (value == null) {
			value = System.getenv(variable);

			if (value == null) {
				return defaultValue;
			}
		}

		Integer parsed = Ints.tryParse(variable);
		return parsed == null ? defaultValue : parsed;
	}
}
