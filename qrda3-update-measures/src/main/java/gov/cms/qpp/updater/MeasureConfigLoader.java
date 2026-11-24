package gov.cms.qpp.updater;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.util.JsonMapperFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MeasureConfigLoader {
	private Map<String, MeasureConfig> configurationMap;
	private Map<String, List<MeasureConfig>> cpcPlusGroups;

	public MeasureConfigLoader(String filename, Boolean useElectronicMeasureId) {
		initMeasureConfigs(filename, useElectronicMeasureId);
	}

	/**
	 * Initialize all measure configurations
	 */
	private void initMeasureConfigs(String filename, Boolean useElectronicMeasureId) {
		configurationMap = grabConfiguration(filename, useElectronicMeasureId);
		cpcPlusGroups = new HashMap<>();
		getMeasureConfigs().stream()
			.filter(config -> config.getCpcPlusGroup() != null)
			.forEach(config -> cpcPlusGroups.computeIfAbsent(
				config.getCpcPlusGroup(), key -> new ArrayList<>()).add(config));
	}

	private Map<String, MeasureConfig> grabConfiguration(String filename, Boolean useElectronicMeasureId) {
		ObjectMapper mapper = JsonMapperFactory.createMapper();

		InputStream measuresInput = this.getClass().getResourceAsStream(filename);

		try {
			TypeReference<List<MeasureConfig>> measureConfigType = new TypeReference<List<MeasureConfig>>() {};
			List<MeasureConfig> configurations = mapper.readValue(measuresInput, measureConfigType);
			return configurations.stream()
				.collect(Collectors.toMap(config -> MeasureConfigLoader.getMeasureId(config, useElectronicMeasureId),
					Function.identity()));
		} catch (JacksonException e) {
			String message = "failure to correctly read measures config json";
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * Finds the first existing guid, electronicMeasureId, or measureId that exists for an aci, ia, or ecqm section
	 *
	 * @param measureConfig Measure configuration that contains the identifiers
	 * @return An identifier
	 */
	private static String getMeasureId(MeasureConfig measureConfig, Boolean useElectronicMeasureId) {
		String guid = measureConfig.getElectronicMeasureVerUuid();
		String electronicMeasureId = measureConfig.getElectronicMeasureId();
		String id = guid != null ? guid : measureConfig.getMeasureId();

		return !useElectronicMeasureId || electronicMeasureId == null ? id : electronicMeasureId;
	}

	/**
	 * Get list of measure configurations.
	 *
	 * @return measure configurations
	 */
	public List<MeasureConfig> getMeasureConfigs() {
		return new ArrayList<>(configurationMap.values());
	}

	/**
	 * Retrieves a mapping of the configurations
	 *
	 * @return mapped configurations
	 */
	public Map<String, MeasureConfig> getConfigurationMap() {
		return configurationMap;
	}
}
