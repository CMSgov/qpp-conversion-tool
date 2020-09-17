package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.reflections.util.ClasspathHelper;

import java.io.InputStream;
import java.util.Set;

/**
 * Represents all the valid APM Entity IDs
 */
public class ApmEntityIds {

	public static final String DEFAULT_APM_ENTITY_FILE_NAME = "apm_entity_ids.json";

	private Set<String> validApmEntityIds;

	public ApmEntityIds(InputStream fileStream) {
		TypeReference<Set<String>> setOfStringsType = new TypeReference<Set<String>>() {};
		validApmEntityIds = JsonHelper.readJson(fileStream, setOfStringsType);
	}

	public ApmEntityIds(String fileName) {
		TypeReference<Set<String>> setOfStringsType = new TypeReference<Set<String>>() {};
		InputStream apmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);
		validApmEntityIds = JsonHelper.readJson(apmEntityIdsInput, setOfStringsType);
	}

	public ApmEntityIds() {
		TypeReference<Set<String>> setOfStringsType = new TypeReference<Set<String>>() {};
		InputStream apmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(DEFAULT_APM_ENTITY_FILE_NAME);
		validApmEntityIds = JsonHelper.readJson(apmEntityIdsInput, setOfStringsType);
	}

	/**
	 * Returns a boolean for whether the provided APM Entity ID exists in the set of valid APM Entity IDs.
	 *
	 * @param apmEntityId The APM Entity ID to check.
	 * @return Whether or not the APM Entity ID exists.
	 */
	public boolean idExists(String apmEntityId) {
		return validApmEntityIds.contains(apmEntityId);
	}
}
