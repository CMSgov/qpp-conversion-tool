package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.cms.qpp.conversion.util.JsonHelper;
import org.reflections.util.ClasspathHelper;

import java.io.InputStream;
import java.util.Set;

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

	private static void initApmEntityIds() {
		validApmEntityIds = grabConfiguration(apmEntityIdsFileName);
	}

	private static Set<String> grabConfiguration(final String fileName) {
		TypeReference<Set<String>> setOfStringsType = new TypeReference<Set<String>>() {};

		InputStream apmEntityIdsInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);

		return JsonHelper.readJson(apmEntityIdsInput, setOfStringsType);
	}

	public static void setMeasureDataFile(final String fileName) {
		apmEntityIdsFileName = fileName;
		initApmEntityIds();
	}

	public static boolean idExists(final String apmEntityId) {
		return validApmEntityIds.contains(apmEntityId);
	}
}
