package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MeasureConfigs {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(MeasureConfigs.class);
	public static final String DEFAULT_MEASURE_DATA_FILE_NAME = "measures-data.json";
	public static final String TEST_MEASURE_DATA = "measure-data-static.json";

	private static String measureDataFileName = DEFAULT_MEASURE_DATA_FILE_NAME;

	// These are the internal, mutable structures:
	private static Map<String, MeasureConfig> configurationMap;
	private static Map<String, List<MeasureConfig>> cpcPlusGroup;

	static {
		initMeasureConfigs();
	}

	private MeasureConfigs() {
		// private no-arg constructor; this is effectively a singleton utility class
	}

	public static void init() {
		// no-op; just forces class loading
	}

	private static void initMeasureConfigs() {
		configurationMap = grabConfiguration(measureDataFileName);
		cpcPlusGroup = new HashMap<>();
		setUpGroups();
	}

	public static void initMeasureConfigs(String filename) {
		measureDataFileName = filename;
		configurationMap = grabConfiguration(measureDataFileName);
		cpcPlusGroup = new HashMap<>();
		setUpGroups();
	}

	public static Map<String, MeasureConfig> grabConfiguration(String fileName) {
		ObjectMapper mapper = new ObjectMapper();
		InputStream measuresInput = ClasspathHelper.contextClassLoader().getResourceAsStream(fileName);

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<>() {};
			List<MeasureConfig> configurations = mapper.readValue(measuresInput, measureConfigType);

			return configurations.stream()
					.collect(Collectors.toMap(MeasureConfigs::getMeasureId, Function.identity()));
		} catch (IOException e) {
			String message = "failure to correctly read measures config json";
			DEV_LOG.error(message, e);
			throw new IllegalArgumentException(message, e);
		}
	}

	private static void setUpGroups() {
		getMeasureConfigs().stream()
				.filter(config -> config.getCpcPlusGroup() != null)
				.forEach(config -> {
					String groupKey = config.getCpcPlusGroup();
					cpcPlusGroup.computeIfAbsent(groupKey, key -> new ArrayList<>()).add(config);
				});
	}

	private static String getMeasureId(MeasureConfig measureConfig) {
		String guid = measureConfig.getElectronicMeasureVerUuid();
		String electronicMeasureId = measureConfig.getElectronicMeasureId();
		String measureId = measureConfig.getMeasureId();
		String chosen = (electronicMeasureId != null) ? electronicMeasureId : measureId;
		return (guid != null ? guid : chosen).toLowerCase(Locale.US);
	}

	public static void setMeasureDataFile(String fileName) {
		measureDataFileName = fileName;
		initMeasureConfigs();
	}

	/**
	 * Returns a fresh List of all MeasureConfig instances.
	 * Since this is a new ArrayList, callers cannot mutate the internal map.
	 */
	public static List<MeasureConfig> getMeasureConfigs() {
		return new ArrayList<>(configurationMap.values());
	}

	/**
	 * Returns an unmodifiable copy of the internal configurationMap.
	 * This prevents callers from mutating the original map.
	 */
	public static Map<String, MeasureConfig> getConfigurationMap() {
		return Collections.unmodifiableMap(new HashMap<>(configurationMap));
	}

	/**
	 * Returns an unmodifiable copy of the internal cpcPlusGroup map.
	 * Each List<MeasureConfig> inside is also defensively copied and wrapped.
	 */
	public static Map<String, List<MeasureConfig>> getCpcPlusGroup() {
		if (cpcPlusGroup == null) {
			return Collections.emptyMap();
		}
		Map<String, List<MeasureConfig>> copy = new HashMap<>();
		for (Map.Entry<String, List<MeasureConfig>> entry : cpcPlusGroup.entrySet()) {
			// Defensive‐copy each list, then wrap as unmodifiable
			List<MeasureConfig> originalList = entry.getValue();
			List<MeasureConfig> listCopy = new ArrayList<>(originalList);
			copy.put(entry.getKey(), Collections.unmodifiableList(listCopy));
		}
		return Collections.unmodifiableMap(copy);
	}

	static List<String> requiredMeasuresForSection(String section) {
		return configurationMap.values().stream()
				.filter(measureConfig -> measureConfig.isRequired()
						&& section.equals(measureConfig.getCategory()))
				.map(MeasureConfigs::getMeasureId)
				.collect(Collectors.toList());
	}
}
