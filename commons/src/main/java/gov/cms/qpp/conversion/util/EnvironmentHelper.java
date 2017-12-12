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

	public static String getOrDefault(String variable, String defaultValue) {
		String value = get(variable);
		return value == null ? defaultValue : value;
	}
}
