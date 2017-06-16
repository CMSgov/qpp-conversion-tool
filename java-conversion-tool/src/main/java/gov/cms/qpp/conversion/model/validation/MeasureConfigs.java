package gov.cms.qpp.conversion.model.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.reflections.util.ClasspathHelper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MeasureConfigs {

	private static List<MeasureConfig> configurations;
	private static String measureDataFileName = "measures-data-short.json";

	private static Map<String, MeasureConfig> configurationMap;

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
	 * Initialize all measure configurations
	 */
	private static void initMeasureConfigs() {

		ObjectMapper mapper = new ObjectMapper();

		InputStream measuresInput = ClasspathHelper.contextClassLoader().getResourceAsStream(measureDataFileName);

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<List<MeasureConfig>>() {};
			configurations = mapper.readValue(measuresInput, measureConfigType);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}

		initConfigurationMap();
	}

	/**
	 * Initialize a configuration mapping of measure configurations
	 */
	private static void initConfigurationMap() {
		configurationMap = configurations.stream().collect(Collectors.toMap(MeasureConfigs::getMeasureId, Function.identity()));
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
		return (guid != null ? guid : (electronicMeasureId != null ? electronicMeasureId : measureId));
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

	public static List<MeasureConfig> getMeasureConfigs() {
		return configurations;
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
	 * Retrieves a list of required mappings for any given section
	 *
	 * @param section Specified section for measures required
	 * @return The list of required measures
	 */
	public static List<String> requiredMeasuresForSection(String section) {

		return configurations.stream()
			.filter(measureConfig -> measureConfig.isRequired() && section.equals(measureConfig.getCategory()))
			.map(MeasureConfigs::getMeasureId)
			.collect(Collectors.toList());
	}
}
