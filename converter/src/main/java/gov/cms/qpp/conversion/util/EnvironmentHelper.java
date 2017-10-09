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
		return valueFor(variable) != null;
	}

	/**
	 * Looks up the value for a named variable by environment variable first and then property.s
	 *
	 * @param variable The variable name to look-up.
	 * @return The value for the variable, null if it is not found.
	 */
	public static String valueFor(String variable) {
		String variableValue = System.getenv(variable);

		if (variableValue != null) {
			return variableValue;
		}

		variableValue = System.getProperty(variable);

		return variableValue;
	}
}
