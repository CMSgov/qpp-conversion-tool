package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MeasureConfigs {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(MeasureConfigs.class);
	public static final String DEFAULT_MEASURE_DATA_FILE_NAME = "measures-data.json";

	private static String measureDataFileName = DEFAULT_MEASURE_DATA_FILE_NAME;
	private static Map<String, MeasureConfig> configurationMap;
	private static Map<String, List<MeasureConfig>> cpcPlusGroups;

	/**
	 * Static initialization
	 */
	static {
		initMeasureConfigs();
	}

	/**
	 * Empty private constructor for singleton
	 */
	private MeasureConfigs() {
		//empty and private constructor because this is a singleton
	}

	/**
	 * Method to ensure MeasureConfigs is initialized
	 */
	public static void init() {
		// Hack to load the class without doing any extra work
	}

	/**
	 * Initialize all measure configurations
	 */
	private static void initMeasureConfigs() {
		configurationMap = grabConfiguration(measureDataFileName);
		cpcPlusGroups = new HashMap<>();
		getMeasureConfigs().stream()
				.filter(config -> config.getCpcPlusGroup() != null)
				.forEach(config -> cpcPlusGroups.computeIfAbsent(
						config.getCpcPlusGroup(), key -> new ArrayList<>()).add(config));
	}

	public static Map<String, MeasureConfig> grabConfiguration(String fileName) {
		ObjectMapper mapper = new ObjectMapper();

		InputStream measuresInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<List<MeasureConfig>>() {};
			List<MeasureConfig> configurations = mapper.readValue(measuresInput, measureConfigType);
			return configurations.stream()
					.collect(Collectors.toMap(MeasureConfigs::getMeasureId, Function.identity()));
		} catch (IOException e) {
			String message = "failure to correctly read measures config json";
			DEV_LOG.error(message);
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * Finds the first existing guid, electronicMeasureId, or measureId that exists for an aci, ia, or ecqm section
	 *
	 * @param measureConfig Measure configuration that contains the identifiers
	 * @return An identifier
	 */
	private static String getMeasureId(MeasureConfig measureConfig) {
		String guid = measureConfig.getElectronicMeasureVerUuid();
		String electronicMeasureId = measureConfig.getElectronicMeasureId();
		String measureId = measureConfig.getMeasureId();
		String chosenMeasureId = electronicMeasureId != null ? electronicMeasureId : measureId;
		return guid != null ? guid : chosenMeasureId;
	}

	/**
	 * Reconfigures a filename and initializes the measure configurations from that file
	 *
	 * @param fileName Name to be used
	 */
	public static void setMeasureDataFile(String fileName) {
		measureDataFileName = fileName;
		initMeasureConfigs();
	}

	/**
	 * Get list of measure configurations.
	 *
	 * @return measure configurations
	 */
	public static List<MeasureConfig> getMeasureConfigs() {
		return new ArrayList<>(configurationMap.values());
	}

	/**
	 * Retrieves a mapping of the configurations
	 *
	 * @return mapped configurations
	 */
	public static Map<String, MeasureConfig> getConfigurationMap() {
		return configurationMap;
	}

	/**
	 * Retrieves a mapping of CPC+ measure groups
	 *
	 * @return mapped CPC+ measure groups
	 */
	public static Map<String, List<MeasureConfig>> getCpcPlusGroups() {
		return cpcPlusGroups;
	}

	/**
	 * Retrieves a list of required mappings for any given section
	 *
	 * @param section Specified section for measures required
	 * @return The list of required measures
	 */
	static List<String> requiredMeasuresForSection(String section) {

		return configurationMap.values().stream()
			.filter(measureConfig -> measureConfig.isRequired() && section.equals(measureConfig.getCategory()))
			.map(MeasureConfigs::getMeasureId)
			.collect(Collectors.toList());
	}

}
