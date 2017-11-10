package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.reflections.util.ClasspathHelper;

import java.io.InputStream;
import java.util.Set;

/**
 * Represents all the valid APM Entity IDs
 */
public final class ApmEntityIds {

	public static final String DEFAULT_APM_ENTITY_FILE_NAME = "apm_entity_ids.json";

	private static String apmEntityIdsFileName = DEFAULT_APM_ENTITY_FILE_NAME;
	private static Set<String> validApmEntityIds;

	/**
	 * Static initialization
	 */
	static {
		initApmEntityIds();
	}

	/**
	 * Empty private constructor for singleton
	 */
	private ApmEntityIds() {
		//empty and private constructor because this is a singleton
	}

	/**
	 * Populates the set of APM Entity IDs.
	 */
	private static void initApmEntityIds() {
		validApmEntityIds = grabConfiguration(apmEntityIdsFileName);
	}

	/**
	 * Given the file name, returns a {@link Set} of {@link String}s from the file.
	 *
	 * @param fileName The file to parse.
	 * @return Set of Strings.
	 */
	private static Set<String> grabConfiguration(final String fileName) {
		TypeReference<Set<String>> setOfStringsType = new TypeReference<Set<String>>() {};

		InputStream apmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);

		return JsonHelper.readJson(apmEntityIdsInput, setOfStringsType);
	}

	/**
	 * Sets the file to use as a data source for the set of valid APM Entity IDs.
	 *
	 * @param fileName The file name to use.
	 */
	public static void setApmDataFile(final String fileName) {
		apmEntityIdsFileName = fileName;
		initApmEntityIds();
	}

	/**
	 * Returns a boolean for whether the provided APM Entity ID exists in the set of valid APM Entity IDs.
	 *
	 * @param apmEntityId The APM Entity ID to check.
	 * @return Whether or not the APM Entity ID exists.
	 */
	public static boolean idExists(final String apmEntityId) {
		return validApmEntityIds.contains(apmEntityId);
	}
}
