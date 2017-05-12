package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class MeasureConfigs {

	private static List<MeasureConfig> configurations;
	private static String measureDataFileName = "measures-data-short.json";

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
	}

	public static void setMeasureDataFile(String fileName) {
		measureDataFileName = fileName;
		initMeasureConfigs();
	}

	public static List<MeasureConfig> getMeasureConfigs() {
		return configurations;
	}
}
