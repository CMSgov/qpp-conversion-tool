package gov.cms.qpp.conversion.util;

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
		return get(variable) != null;
	}

	public static String get(String variable) {
		String value = System.getProperty(variable);
		if (value != null) {
			return value;
		}
		return System.getenv(variable);
	}

	/**
	 * Get the value for a given property / environment variable. Substitute the given default if not found.
	 *
	 * @param variable key used to search for value
	 * @param defaultValue substitute value if not found
	 * @return value for the given variable key or substitute
	 */
	public static String getOrDefault(String variable, String defaultValue) {
		String value = get(variable);
		return value == null ? defaultValue : value;
	}
}
