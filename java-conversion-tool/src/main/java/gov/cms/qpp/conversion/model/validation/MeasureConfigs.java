package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class MeasureConfigs {

	private static List<MeasureConfig> configurations;
	private static String measureDataFileName = "measures-data-short.json";

	private static Map<String, MeasureConfig> configurationMap;

	static {
		initMeasureConfigs();
		initConfigurationMap();
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
	}

	private static void initConfigurationMap() {
		configurationMap = configurations.stream().collect(Collectors.toMap(config -> {
			String guid = config.getElectronicMeasureVerUuid();
			String electronicMeasureId = config.getElectronicMeasureId();
			String measureId = config.getMeasureId();
			return (guid != null ? guid : (electronicMeasureId != null ? electronicMeasureId : measureId));
		}, Function.identity()));
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
}
