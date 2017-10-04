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

	/**
	 * Checks if an environment variable or system property is present
	 *
	 * @param variable
	 * @return true if System.getenv(variable) or System.getProperty(variable) are not null
	 */
	public static boolean isPresent(String variable) {
		return System.getenv(variable) != null || System.getProperty(variable) != null;
	}

	/**
	 * Tries to parse an integer based on an environment variable or system property.
	 *
	 * @param variable the key to look for in environment variables and system properties
	 * @param defaultValue returned if a value could not be parsed or is null
	 * @return
	 */
	public static int getInt(String variable, int defaultValue) {
		String value = System.getProperty(variable);
		if (value == null) {
			value = System.getenv(variable);

			if (value == null) {
				return defaultValue;
			}
		}

		Integer parsed = Ints.tryParse(value);
		return parsed == null ? defaultValue : parsed;
	}
}
