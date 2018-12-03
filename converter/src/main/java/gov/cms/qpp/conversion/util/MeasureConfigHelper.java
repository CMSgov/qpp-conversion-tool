package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

import java.util.Objects;
import java.util.stream.Stream;

public class MeasureConfigHelper {

	public static final String MEASURE_ID = "measureId";
	public static final String NO_MEASURE = "No given measure id";

	private MeasureConfigHelper() {
		// private for this helper class
	}

	/**
	 * Convenience method to retrieve the measure configuration for validation from an ecqm node
	 *
	 * @param node Contains the id that associates with the measure config
	 * @return
	 */
	public static MeasureConfig getMeasureConfig(Node node) {
		String measureId =  node.getValue(MEASURE_ID);
		return MeasureConfigs.getConfigurationMap().get(measureId);
	}

	/**
	 * Gets the electronic measure id by uuid or defaults to null if none exists
	 *
	 * @param uuid identifier used to fined the electronic measure id
	 * @return electronic measure id
	 */
	public static String getMeasureConfigIdByUuidOrDefault(String uuid) {
		MeasureConfig config = MeasureConfigs.getConfigurationMap().get(uuid);
		if (config != null) {
			return config.getElectronicMeasureId();
		}
		return null;
	}


	/**
	 * Determine which measure id-ish value should be used for a given node.
	 *
	 * @param node a decoded node
	 * @return the best available measure id value
	 */
	public static String getPrioritizedId(Node node) {
		MeasureConfig measureConfig = getMeasureConfig(node);
		return getPrioritizedId(measureConfig);
	}

	/**
	 * Find the best available measure id value within the given {@link MeasureConfig}
	 *
	 * @param measureConfig a configuration that details a measure
	 * @return the best available measure id from the given config or a message stating that none were found.
	 */
	static String getPrioritizedId(MeasureConfig measureConfig) {
		return Stream.of(measureConfig.getElectronicMeasureId(),
			measureConfig.getElectronicMeasureVerUuid(), measureConfig.getMeasureId())
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(NO_MEASURE);
	}
}
