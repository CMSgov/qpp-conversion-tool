package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MeasureConfigs {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(MeasureConfigs.class);
	public static final String DEFAULT_MEASURE_DATA_FILE_NAME = "measures-data.json";

	private static String measureDataFileName = DEFAULT_MEASURE_DATA_FILE_NAME;
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
		configurationMap = grabConfiguration(measureDataFileName);
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
		return configurationMap.values().stream().collect(Collectors.toList());
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

		return configurationMap.values().stream()
			.filter(measureConfig -> measureConfig.isRequired() && section.equals(measureConfig.getCategory()))
			.map(MeasureConfigs::getMeasureId)
			.collect(Collectors.toList());
	}
}
