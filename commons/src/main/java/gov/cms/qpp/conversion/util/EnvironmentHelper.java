package gov.cms.qpp.conversion.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

/**
 * Help with environment concerns
 */
public class EnvironmentHelper {
	private static final Logger LOG = LoggerFactory.getLogger(EnvironmentHelper.class);
	static final String NOT_FOUND = "Could not find a property or environment variable named: %s.";

	/**
	 * Constructor that is private and empty because this is a utility class.
	 */
	private EnvironmentHelper() {
		//private and empty because this is a utility class
	}

	/**
	 * Checks if an environment variable or system property is present
	 *
	 * @param variable key used to search for value
	 * @return true if System.getenv(variable) or System.getProperty(variable) are not null
	 */
	public static boolean isPresent(String variable) {
		return get(variable) != null;
	}

	/**
	 * Get the value for a given property / environment variable.
	 *
	 * @param variable key used to search for value
	 * @return value for the given variable key
	 */
	public static String get(String variable) {
		String value = Stream.of(System.getProperty(variable), System.getenv(variable))
			.filter(var -> var != null && !var.isEmpty())
			.findFirst()
			.orElse(null);
		if (value == null) {
			LOG.warn(
				String.format(NOT_FOUND, variable));
		}
		return value;
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
