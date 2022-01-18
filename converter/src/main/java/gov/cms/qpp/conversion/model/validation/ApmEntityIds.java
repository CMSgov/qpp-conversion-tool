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

	public static final String DEFAULT_CPC_PLUS_APM_ENTITY_FILE_NAME = "cpc_plus_apm_entity_ids.json";

	public static final String DEFAULT_PCF_APM_ENTITY_FILE_NAME = "pcf_apm_entity_ids.json";

	private static TypeReference<Set<String>> SET_OF_STRINGS_TYPE = new TypeReference<Set<String>>() {};

	private Set<String> validCpcPlusApmEntityIds;

	private Set<String> validPcfApmEntityIds;

	public ApmEntityIds(InputStream cpcPlusFileStream, InputStream pcfFilestream) {
		validCpcPlusApmEntityIds = JsonHelper.readJson(cpcPlusFileStream, SET_OF_STRINGS_TYPE);
		validPcfApmEntityIds = JsonHelper.readJson(pcfFilestream, SET_OF_STRINGS_TYPE);
	}

	public ApmEntityIds(String fileName) {
		InputStream apmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);
		validCpcPlusApmEntityIds = JsonHelper.readJson(apmEntityIdsInput, SET_OF_STRINGS_TYPE);
	}

	public ApmEntityIds(String cpcFileName, String pcfFileName) {
		InputStream cpcPlusApmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(cpcFileName);
		InputStream pcfApmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(pcfFileName);
		validCpcPlusApmEntityIds = JsonHelper.readJson(cpcPlusApmEntityIdsInput, SET_OF_STRINGS_TYPE);
		validPcfApmEntityIds = JsonHelper.readJson(pcfApmEntityIdsInput, SET_OF_STRINGS_TYPE);
	}

	public ApmEntityIds() {
		InputStream cpcPlusApmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(DEFAULT_CPC_PLUS_APM_ENTITY_FILE_NAME);
		InputStream pcfApmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(DEFAULT_PCF_APM_ENTITY_FILE_NAME);

		validCpcPlusApmEntityIds = JsonHelper.readJson(cpcPlusApmEntityIdsInput, SET_OF_STRINGS_TYPE);
		validPcfApmEntityIds = JsonHelper.readJson(pcfApmEntityIdsInput, SET_OF_STRINGS_TYPE);
	}

	/**
	 * Returns a boolean for whether the provided APM Entity ID exists in the set of valid APM Entity IDs.
	 *
	 * @param apmEntityId The APM Entity ID to check.
	 * @return Whether or not the APM Entity ID exists.
	 */
	public boolean cpcIdExists(String apmEntityId) {
		return validCpcPlusApmEntityIds.contains(apmEntityId);
	}

	/**
	 * Returns a boolean for whether the provided PCF APM Entity ID exists in the set of valid PCF APM Entity IDs.
	 *
	 * @param apmEntityId
	 * @return
	 */
	public boolean pcfIdExists(String apmEntityId) {
		return validPcfApmEntityIds.contains(apmEntityId);
	}
}
