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

	private static TypeReference<Set<String>> SET_OF_STRINGS_TYPE = new TypeReference<Set<String>>() {};

	private Set<String> validApmEntityIds;

	public ApmEntityIds(InputStream cpcPlusFileStream, InputStream pcfFilestream) {
		validCpcPlusApmEntityIds = JsonHelper.readJson(cpcPlusFileStream, SET_OF_STRINGS_TYPE);
		validPcfApmEntityIds = JsonHelper.readJson(pcfFilestream, SET_OF_STRINGS_TYPE);
	}

	public ApmEntityIds(String fileName) {
		InputStream apmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);
		validApmEntityIds = JsonHelper.readJson(apmEntityIdsInput, SET_OF_STRINGS_TYPE);
	}

	public ApmEntityIds() {
		InputStream apmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(DEFAULT_APM_ENTITY_FILE_NAME);
		validApmEntityIds = JsonHelper.readJson(apmEntityIdsInput, SET_OF_STRINGS_TYPE);
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
