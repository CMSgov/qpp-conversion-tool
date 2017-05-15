package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MeasureConfigs {

	private static List<MeasureConfig> configurations;
	private static String measureDataFileName = "measures-data-short.json";

	private static Map<String, MeasureConfig> configurationMap;

	static {
		initMeasureConfigs();
	}

	private MeasureConfigs() {
		//empty and private constructor because this is a singleton
	}

	/**
	 * Initialize all measure configurations
	 */
	private static void initMeasureConfigs() {

		ObjectMapper mapper = new ObjectMapper();

		ClassPathResource measuresConfigResource = new ClassPathResource(measureDataFileName);

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<List<MeasureConfig>>() {};
			configurations = mapper.readValue(measuresConfigResource.getInputStream(), measureConfigType);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}

		initConfigurationMap();
	}

	private static void initConfigurationMap() {
		configurationMap = configurations.stream().collect(Collectors.toMap(MeasureConfigs::getMeasureId, Function.identity()));
	}

	private static String getMeasureId(MeasureConfig measureConfig) {
		String guid = measureConfig.getElectronicMeasureVerUuid();
		String electronicMeasureId = measureConfig.getElectronicMeasureId();
		String measureId = measureConfig.getMeasureId();
		return (guid != null ? guid : (electronicMeasureId != null ? electronicMeasureId : measureId));
	}

	public static void setMeasureDataFile(String fileName) {
		measureDataFileName = fileName;
		initMeasureConfigs();
	}

	public static List<MeasureConfig> getMeasureConfigs() {
		return configurations;
	}

	public static Map<String, MeasureConfig> getConfigurationMap() {
		return configurationMap;
	}

	public static List<String> requiredMeasuresForSection(String section) {

		return configurations.stream()
			.filter(measureConfig -> measureConfig.isRequired() && section.equals(measureConfig.getCategory()))
			.map(MeasureConfigs::getMeasureId)
			.collect(Collectors.toList());
	}
}
